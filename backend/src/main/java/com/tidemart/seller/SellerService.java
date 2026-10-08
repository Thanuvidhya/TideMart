package com.tidemart.seller;

import com.tidemart.category.CategoryRepository;
import com.tidemart.common.Role;
import com.tidemart.common.exception.BadRequestException;
import com.tidemart.common.exception.ResourceNotFoundException;
import com.tidemart.finance.CommissionRuleRepository;
import com.tidemart.order.*;
import com.tidemart.product.*;
import com.tidemart.user.User;
import com.tidemart.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SellerService {
    public record SignupRequest(String businessName, String gstNo, String panNo, String bankAccount, String ifsc) {}
    public record PickupRequest(String line1, String city, String state, String pincode) {}
    public record VariantInput(String size, int stock) {}
    public record ProductRequest(String name, String description, double price, double mrp, Long categoryId, String imageUrl, List<VariantInput> variants) {}
    public record ProductRow(Product product, List<ProductVariant> variants) {}
    public record OrderLine(Long orderId, String orderNo, String status, String product, String size, int qty, double unitPrice, String shipName, String shipCity, String paymentMethod, String createdAt) {}
    public record EarningLine(String orderNo, String product, double gross, double commission, double net) {}
    public record Earnings(double gross, double commission, double net, int deliveredOrders, List<EarningLine> lines) {}
    public record Label(String orderNo, String shipTo, String phone, String address, String paymentMethod, double collectOnDelivery, String sentBy, String pickup, List<String> items) {}
    private static final Map<String, String> NEXT = Map.of("PLACED", "PACKED", "PACKED", "SHIPPED");
    @org.springframework.beans.factory.annotation.Autowired
    private com.tidemart.notification.NotificationService notifier;
    @org.springframework.beans.factory.annotation.Autowired
    private com.tidemart.product.StockAlertRepository alerts;
    @org.springframework.beans.factory.annotation.Autowired
    private com.tidemart.wallet.WalletService wallet;
    @Value("${tidemart.auto-approve:true}")
    private boolean autoApprove;
    private final SellerRepository sellers;
    private final PickupAddressRepository pickups;
    private final UserRepository users;
    private final ProductRepository products;
    private final ProductVariantRepository variants;
    private final ProductMediaRepository media;
    private final CategoryRepository categories;
    private final CommissionRuleRepository rules;
    private final OrderRepository orders;
    private final OrderItemRepository items;
    private final OrderStatusHistoryRepository hist;

    public SellerService(SellerRepository sellers, PickupAddressRepository pickups, UserRepository users, ProductRepository products, ProductVariantRepository variants,
                         ProductMediaRepository media, CategoryRepository categories, CommissionRuleRepository rules, OrderRepository orders, OrderItemRepository items, OrderStatusHistoryRepository hist) {
        this.sellers = sellers; this.pickups = pickups; this.users = users; this.products = products; this.variants = variants; this.media = media;
        this.categories = categories; this.rules = rules; this.orders = orders; this.items = items; this.hist = hist;
    }

    @Transactional
    public Seller signup(Long uid, SignupRequest r) {
        if (sellers.findByUserId(uid).isPresent()) throw new BadRequestException("You already have a seller account");
        if (r.businessName() == null || r.businessName().isBlank() || r.panNo() == null || r.bankAccount() == null || r.ifsc() == null) throw new BadRequestException("Business name, PAN, bank account and IFSC are required");
        Seller s = new Seller();
        s.userId = uid; s.businessName = r.businessName(); s.gstNo = r.gstNo(); s.panNo = r.panNo(); s.bankAccount = r.bankAccount(); s.ifsc = r.ifsc();
        if (autoApprove) {
            s.status = "APPROVED";
            User u = users.findById(uid).orElseThrow();
            if (u.role == Role.CUSTOMER || u.role == Role.RESELLER) { u.role = Role.SELLER; users.save(u); }
        }
        return sellers.save(s);
    }

    public Map<String, Object> me(Long uid) { Seller s = seller(uid); Map<String, Object> m = new HashMap<>(); m.put("seller", s); m.put("pickup", pickups.findBySellerId(s.id).orElse(null)); return m; }

    public PickupAddress savePickup(Long uid, PickupRequest r) {
        Seller s = seller(uid);
        PickupAddress p = pickups.findBySellerId(s.id).orElseGet(PickupAddress::new);
        p.sellerId = s.id; p.line1 = r.line1(); p.city = r.city(); p.state = r.state(); p.pincode = r.pincode();
        return pickups.save(p);
    }

    public List<ProductRow> listProducts(Long uid) { return products.findBySellerIdOrderByIdDesc(seller(uid).id).stream().map(p -> new ProductRow(p, variants.findByProductId(p.id))).toList(); }

    @Transactional
    public ProductRow createProduct(Long uid, ProductRequest r) {
        Seller s = seller(uid);
        if (!"APPROVED".equals(s.status)) throw new BadRequestException("Your seller account is waiting for approval");
        if (r.name() == null || r.name().isBlank() || r.price() <= 0 || r.mrp() < r.price()) throw new BadRequestException("Enter a name, a price, and an MRP that is not below the price");
        if (r.categoryId() == null || !categories.existsById(r.categoryId())) throw new BadRequestException("Choose a category");
        Product p = new Product();
        p.sellerId = s.id; p.categoryId = r.categoryId(); p.name = r.name(); p.description = r.description(); p.price = r.price(); p.mrp = r.mrp();
        p.status = autoApprove ? "APPROVED" : "PENDING";
        p = products.save(p);
        List<VariantInput> vs = (r.variants() == null || r.variants().isEmpty()) ? List.of(new VariantInput("Free", 10)) : r.variants();
        for (VariantInput v : vs) { ProductVariant pv = new ProductVariant(); pv.productId = p.id; pv.size = v.size(); pv.stock = Math.max(0, v.stock()); variants.save(pv); }
        if (r.imageUrl() != null && !r.imageUrl().isBlank()) { ProductMedia m = new ProductMedia(); m.productId = p.id; m.url = r.imageUrl(); media.save(m); }
        return new ProductRow(p, variants.findByProductId(p.id));
    }

    public Product updatePrice(Long uid, Long id, double price, double mrp) {
        Product p = ownProduct(uid, id);
        if (price <= 0 || mrp < price) throw new BadRequestException("MRP cannot be below the price");
        p.price = price; p.mrp = mrp;
        return products.save(p);
    }

    public ProductVariant setStock(Long uid, Long variantId, int stock) {
        ProductVariant v = variants.findById(variantId).orElseThrow(() -> new ResourceNotFoundException("Variant not found"));
        Product owned = ownProduct(uid, v.productId);
        boolean wasOut = v.stock == 0;
        v.stock = Math.max(0, stock);
        ProductVariant saved = variants.save(v);
        if (wasOut && v.stock > 0) for (var a : alerts.findByProductId(owned.id)) { notifier.notify(a.userId, "Back in stock", owned.name + " is available again."); alerts.delete(a); }
        return saved;
    }

    public List<OrderLine> orders(Long uid) {
        Seller s = seller(uid);
        List<OrderLine> out = new ArrayList<>();
        for (OrderItem i : items.findBySellerIdOrderByIdDesc(s.id)) {
            Order o = orders.findById(i.orderId).orElse(null);
            if (o == null) continue;
            out.add(new OrderLine(o.id, o.orderNo, o.status, products.findById(i.productId).map(p -> p.name).orElse("Product"), variants.findById(i.variantId).map(v -> v.size).orElse(""),
                    i.qty, i.unitPrice, o.shipName, o.shipCity, o.paymentMethod, String.valueOf(o.createdAt).substring(0, 10)));
        }
        return out;
    }

    @Transactional
    public Order setStatus(Long uid, Long orderId, String to) {
        Order o = ownOrder(uid, orderId);
        if (!to.equals(NEXT.get(o.status))) throw new BadRequestException("Sellers can only move an order from Placed to Packed to Shipped");
        o.status = to;
        orders.save(o);
        OrderStatusHistory h = new OrderStatusHistory();
        h.orderId = orderId; h.status = to; h.note = "Updated by seller";
        hist.save(h);
        notifier.notify(o.userId, "Order update", "Order " + o.orderNo + " is now " + to.toLowerCase() + ".");
        return o;
    }

    public Label label(Long uid, Long orderId) {
        Seller s = seller(uid);
        Order o = ownOrder(uid, orderId);
        List<String> names = items.findByOrderId(orderId).stream().filter(i -> s.id.equals(i.sellerId)).map(i -> products.findById(i.productId).map(p -> p.name).orElse("Product") + " x" + i.qty).toList();
        var pk = pickups.findBySellerId(s.id).orElse(null);
        return new Label(o.orderNo, o.shipName, o.shipPhone, o.shipLine1 + ", " + o.shipCity + ", " + o.shipState + " " + o.shipPincode, o.paymentMethod,
                "COD".equals(o.paymentMethod) ? o.total : 0, s.businessName, pk == null ? "Add a pickup address" : pk.line1 + ", " + pk.city + " " + pk.pincode, names);
    }

    public Earnings earnings(Long uid) {
        Seller s = seller(uid);
        List<EarningLine> lines = new ArrayList<>();
        Set<Long> delivered = new HashSet<>();
        double g = 0, c = 0;
        for (OrderItem i : items.findBySellerIdOrderByIdDesc(s.id)) {
            Order o = orders.findById(i.orderId).orElse(null);
            if (o == null || !"DELIVERED".equals(o.status)) continue;
            Product p = products.findById(i.productId).orElseThrow();
            double gross = (i.unitPrice - i.resellerMargin) * i.qty, pct = rules.findByCategoryId(p.categoryId).map(x -> x.percent).orElse(10.0), com = Math.round(gross * pct) / 100.0;
            g += gross; c += com; delivered.add(o.id);
            lines.add(new EarningLine(o.orderNo, p.name, gross, com, gross - com));
        }
        return new Earnings(g, c, g - c, delivered.size(), lines);
    }

    public record Stat(String product, long units, double revenue) {}

    public List<Stat> analytics(Long uid) {
        Map<String, double[]> m = new LinkedHashMap<>();
        for (OrderItem i : items.findBySellerIdOrderByIdDesc(seller(uid).id)) {
            Order o = orders.findById(i.orderId).orElse(null);
            if (o == null || "CANCELLED".equals(o.status)) continue;
            double[] a = m.computeIfAbsent(products.findById(i.productId).map(p -> p.name).orElse("Product"), k -> new double[2]);
            a[0] += i.qty; a[1] += (i.unitPrice - i.resellerMargin) * i.qty;
        }
        return m.entrySet().stream().map(e -> new Stat(e.getKey(), (long) e.getValue()[0], e.getValue()[1])).sorted((a, b) -> Double.compare(b.revenue(), a.revenue())).toList();
    }

    /** CSV lines: name,description,price,mrp,categoryId,sizes separated by |,stock per size */
    public Map<String, Object> bulk(Long uid, String csv) {
        int ok = 0;
        List<String> errors = new ArrayList<>();
        String[] lines = csv.split("\\R");
        for (int n = 0; n < lines.length; n++) {
            String line = lines[n].trim();
            if (line.isEmpty() || (n == 0 && line.toLowerCase().startsWith("name"))) continue;
            String[] c = line.split(",", -1);
            try {
                if (c.length < 5) throw new BadRequestException("needs name, description, price, mrp, categoryId");
                int stock = c.length > 6 && !c[6].isBlank() ? Integer.parseInt(c[6].trim()) : 10;
                List<VariantInput> vs = new ArrayList<>();
                if (c.length > 5 && !c[5].isBlank()) for (String sz : c[5].split("\\|")) vs.add(new VariantInput(sz.trim(), stock));
                createProduct(uid, new ProductRequest(c[0].trim(), c[1].trim(), Double.parseDouble(c[2].trim()), Double.parseDouble(c[3].trim()), Long.parseLong(c[4].trim()), null, vs));
                ok++;
            } catch (Exception e) {
                errors.add("Line " + (n + 1) + ": " + e.getMessage());
            }
        }
        return Map.of("created", ok, "errors", errors);
    }

    @Transactional
    public Order reject(Long uid, Long orderId, String reason) {
        Order o = ownOrder(uid, orderId);
        if (!"PLACED".equals(o.status)) throw new BadRequestException("You can only reject an order that is not packed yet");
        for (OrderItem i : items.findByOrderId(orderId)) variants.findById(i.variantId).ifPresent(v -> { v.stock += i.qty; variants.save(v); });
        o.status = "CANCELLED";
        if ("SUCCESS".equals(o.paymentStatus)) { o.paymentStatus = "REFUNDED"; wallet.credit(o.userId, o.total, "Refund for order " + o.orderNo); }
        orders.save(o);
        OrderStatusHistory h = new OrderStatusHistory();
        h.orderId = orderId; h.status = "CANCELLED"; h.note = "Rejected by seller: " + reason;
        hist.save(h);
        notifier.notify(o.userId, "Order cancelled", "The seller could not fulfil order " + o.orderNo + ". Any payment is refunded to your wallet.");
        return o;
    }

    private Seller seller(Long uid) { return sellers.findByUserId(uid).orElseThrow(() -> new ResourceNotFoundException("Create a seller account first")); }

    private Product ownProduct(Long uid, Long id) { Seller s = seller(uid); return products.findById(id).filter(p -> s.id.equals(p.sellerId)).orElseThrow(() -> new ResourceNotFoundException("Product not found")); }

    private Order ownOrder(Long uid, Long orderId) {
        Seller s = seller(uid);
        if (items.findByOrderId(orderId).stream().noneMatch(i -> s.id.equals(i.sellerId))) throw new ResourceNotFoundException("Order not found");
        return orders.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }
}
