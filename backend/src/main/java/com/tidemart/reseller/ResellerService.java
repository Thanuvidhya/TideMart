package com.tidemart.reseller;

import com.tidemart.cart.CartService;
import com.tidemart.common.Role;
import com.tidemart.common.exception.BadRequestException;
import com.tidemart.common.exception.ResourceNotFoundException;
import com.tidemart.delivery.ServiceablePincodeRepository;
import com.tidemart.order.*;
import com.tidemart.product.*;
import com.tidemart.product.dto.ProductCard;
import com.tidemart.user.User;
import com.tidemart.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class ResellerService {
    public record SignupRequest(String upiId, String bankAccount, String ifsc) {}
    public record Item(Long productId, Long variantId, int qty) {}
    public record CustomerOrder(String customerName, String customerPhone, String line1, String city, String state, String pincode, String paymentMethod, List<Item> items) {}
    public record CatalogItem(ProductCard card, double margin, double customerPrice) {}
    public record OrderRow(Long id, String orderNo, String status, String customer, String city, double total, double margin, String createdAt) {}
    public record Earnings(double earned, double pending, int deliveredOrders, List<OrderRow> orders) {}
    static final double DEFAULT_MARGIN = 50;
    private final ResellerRepository resellers;
    private final ResellerProductRepository margins;
    private final ResellerCustomerRepository customers;
    private final UserRepository users;
    private final ProductService productService;
    private final ProductRepository products;
    private final ProductVariantRepository variants;
    private final CartService cart;
    private final ServiceablePincodeRepository pins;
    private final OrderRepository orders;
    private final OrderItemRepository items;
    private final OrderStatusHistoryRepository hist;
    private final ReturnRepository returns;

    public ResellerService(ResellerRepository resellers, ResellerProductRepository margins, ResellerCustomerRepository customers, UserRepository users, ProductService productService,
                           ProductRepository products, ProductVariantRepository variants, CartService cart, ServiceablePincodeRepository pins, OrderRepository orders,
                           OrderItemRepository items, OrderStatusHistoryRepository hist, ReturnRepository returns) {
        this.resellers = resellers; this.margins = margins; this.customers = customers; this.users = users; this.productService = productService; this.products = products;
        this.variants = variants; this.cart = cart; this.pins = pins; this.orders = orders; this.items = items; this.hist = hist; this.returns = returns;
    }

    @Transactional
    public Reseller signup(Long uid, SignupRequest r) {
        if (resellers.findByUserId(uid).isPresent()) throw new BadRequestException("You are already a reseller");
        Reseller rs = new Reseller();
        rs.userId = uid; rs.upiId = r.upiId(); rs.bankAccount = r.bankAccount(); rs.ifsc = r.ifsc();
        User u = users.findById(uid).orElseThrow();
        if (u.role == Role.CUSTOMER || u.role == Role.SELLER) { u.role = Role.RESELLER; users.save(u); }
        return resellers.save(rs);
    }

    public Reseller me(Long uid) { return reseller(uid); }

    public Reseller savePayout(Long uid, SignupRequest r) { Reseller rs = reseller(uid); rs.upiId = r.upiId(); rs.bankAccount = r.bankAccount(); rs.ifsc = r.ifsc(); return resellers.save(rs); }

    public Page<CatalogItem> catalog(Long uid, String q, long cat, int page) {
        Reseller rs = reseller(uid);
        return productService.search(q, cat, 0, 999999, "popular", page, 12).map(c -> { double m = margin(rs.id, c.id()); return new CatalogItem(c, m, c.price() + m); });
    }

    public CatalogItem setMargin(Long uid, Long productId, double margin) {
        Reseller rs = reseller(uid);
        Product p = products.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (margin < 0 || margin > 5000) throw new BadRequestException("Margin must be between 0 and 5000");
        ResellerProduct rp = margins.findByResellerIdAndProductId(rs.id, productId).orElseGet(ResellerProduct::new);
        rp.resellerId = rs.id; rp.productId = productId; rp.margin = margin;
        margins.save(rp);
        return new CatalogItem(productService.card(p), margin, p.price + margin);
    }

    /** The shared text shows only the customer price. The supplier price stays hidden. */
    public Map<String, String> share(Long uid, List<Long> ids) {
        Reseller rs = reseller(uid);
        StringBuilder sb = new StringBuilder("Shop on Tidemart:\n");
        for (Long id : ids) products.findById(id).ifPresent(p -> sb.append(p.name).append(" - Rs ").append((long) (p.price + margin(rs.id, p.id))).append("\nhttp://localhost:5173/product/").append(p.id).append("\n"));
        return Map.of("text", sb.toString(), "whatsappUrl", "https://wa.me/?text=" + URLEncoder.encode(sb.toString(), StandardCharsets.UTF_8));
    }

    @Transactional
    public Order placeOrder(Long uid, CustomerOrder r) {
        Reseller rs = reseller(uid);
        if (r.customerName() == null || r.customerName().isBlank() || r.customerPhone() == null || !r.customerPhone().matches("\\d{10}") || r.line1() == null || r.line1().isBlank() || r.city() == null || r.state() == null)
            throw new BadRequestException("Enter the customer's name, 10-digit phone and full address");
        var pin = pins.findByPincode(r.pincode() == null ? "" : r.pincode()).orElseThrow(() -> new BadRequestException("We do not deliver to this pincode yet"));
        String pay = "UPI".equalsIgnoreCase(r.paymentMethod()) ? "UPI" : "COD";
        if ("COD".equals(pay) && !pin.codAvailable) throw new BadRequestException("Cash on delivery is not available here");
        if (r.items() == null || r.items().isEmpty()) throw new BadRequestException("Add at least one product");
        List<CartService.Line> lines = new ArrayList<>();
        Map<Long, Double> mg = new HashMap<>();
        for (Item it : r.items()) {
            ProductVariant v = variants.findById(it.variantId()).filter(x -> x.productId.equals(it.productId())).orElseThrow(() -> new BadRequestException("Choose a size"));
            if (it.qty() <= 0 || it.qty() > v.stock) throw new BadRequestException("Only " + v.stock + " left for size " + v.size);
            Product p = products.findById(it.productId()).orElseThrow();
            double m = margin(rs.id, p.id);
            mg.put(v.id, m);
            lines.add(new CartService.Line(null, p.id, v.id, p.name, v.size, p.price + m, p.mrp, p.mrp > 0 ? (int)Math.round((1-(p.price+m)/p.mrp)*100) : 0, it.qty(), v.stock, productService.card(p).imageUrl(), "TideMart Store", 0));
        }
        var price = cart.price(lines, null, pay);
        Order o = new Order();
        o.orderNo = "TM" + (100000 + new Random().nextInt(900000)); o.userId = uid; o.resellerId = rs.id;
        o.shipName = r.customerName(); o.shipPhone = r.customerPhone(); o.shipLine1 = r.line1(); o.shipCity = r.city(); o.shipState = r.state(); o.shipPincode = r.pincode();
        o.subtotal = price.subtotal(); o.discount = 0; o.deliveryFee = price.deliveryFee(); o.codFee = price.codFee(); o.total = price.total();
        o.paymentMethod = pay; o.paymentStatus = "COD".equals(pay) ? "PENDING" : "SUCCESS";
        o = orders.save(o);
        for (CartService.Line l : lines) {
            OrderItem i = new OrderItem();
            i.orderId = o.id; i.productId = l.productId(); i.variantId = l.variantId(); i.qty = l.qty(); i.unitPrice = l.price(); i.resellerMargin = mg.get(l.variantId());
            i.sellerId = products.findById(l.productId()).orElseThrow().sellerId;
            items.save(i);
            ProductVariant v = variants.findById(l.variantId()).orElseThrow();
            v.stock -= l.qty(); variants.save(v);
        }
        OrderStatusHistory h = new OrderStatusHistory();
        h.orderId = o.id; h.status = "PLACED"; h.note = "Placed by reseller";
        hist.save(h);
        ResellerCustomer c = customers.findByResellerIdAndPhone(rs.id, r.customerPhone()).orElseGet(ResellerCustomer::new);
        c.resellerId = rs.id; c.name = r.customerName(); c.phone = r.customerPhone(); c.address = r.line1() + ", " + r.city() + " " + r.pincode();
        customers.save(c);
        return o;
    }

    public List<OrderRow> orders(Long uid) {
        Reseller rs = reseller(uid);
        return orders.findByResellerIdOrderByIdDesc(rs.id).stream().map(o -> new OrderRow(o.id, o.orderNo, o.status, o.shipName, o.shipCity, o.total, marginOf(o), String.valueOf(o.createdAt).substring(0, 10))).toList();
    }

    public List<ResellerCustomer> customers(Long uid) { return customers.findByResellerIdOrderByIdDesc(reseller(uid).id); }

    public Earnings earnings(Long uid) {
        List<OrderRow> rows = orders(uid);
        double earned = 0, pending = 0;
        int delivered = 0;
        for (OrderRow r : rows) {
            if ("DELIVERED".equals(r.status())) { earned += r.margin(); delivered++; }
            else if (!"CANCELLED".equals(r.status())) pending += r.margin();
        }
        return new Earnings(earned, pending, delivered, rows);
    }

    /** Margin on items whose return was refunded is not counted. */
    private double marginOf(Order o) {
        return items.findByOrderId(o.id).stream().filter(i -> returns.findByOrderItemId(i.id).map(q -> !"REFUNDED".equals(q.status)).orElse(true)).mapToDouble(i -> i.resellerMargin * i.qty).sum();
    }

    private double margin(Long resellerId, Long productId) { return margins.findByResellerIdAndProductId(resellerId, productId).map(m -> m.margin).orElse(DEFAULT_MARGIN); }

    private Reseller reseller(Long uid) { return resellers.findByUserId(uid).orElseThrow(() -> new ResourceNotFoundException("Become a reseller first")); }
}
