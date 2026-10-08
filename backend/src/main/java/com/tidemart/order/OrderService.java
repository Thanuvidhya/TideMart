package com.tidemart.order;

import com.tidemart.address.Address;
import com.tidemart.address.AddressRepository;
import com.tidemart.cart.CartService;
import com.tidemart.common.exception.BadRequestException;
import com.tidemart.common.exception.ResourceNotFoundException;
import com.tidemart.delivery.ServiceablePincodeRepository;
import com.tidemart.payment.Payment;
import com.tidemart.payment.PaymentRepository;
import com.tidemart.product.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
public class OrderService {
    public record ItemLine(Long itemId, Long productId, Long variantId, String name, String size, int qty, double unitPrice, String imageUrl) {}
    public record Detail(Order order, List<ItemLine> items, List<OrderStatusHistory> history) {}
    public record PlaceRequest(Long addressId, String paymentMethod, String coupon, boolean demoPaymentOk) {}
    static final List<String> FLOW = List.of("PLACED", "PACKED", "SHIPPED", "OUT_FOR_DELIVERY", "DELIVERED");
    @org.springframework.beans.factory.annotation.Autowired
    private com.tidemart.notification.NotificationService notifier;
    @org.springframework.beans.factory.annotation.Autowired
    private com.tidemart.wallet.WalletService wallet;
    @org.springframework.beans.factory.annotation.Autowired
    private com.tidemart.seller.SellerRepository sellerRepo;
    private final OrderRepository orders;
    private final OrderItemRepository oitems;
    private final OrderStatusHistoryRepository hist;
    private final PaymentRepository pays;
    private final AddressRepository addrs;
    private final ServiceablePincodeRepository pins;
    private final CartService cart;
    private final ProductRepository products;
    private final ProductVariantRepository variants;
    private final ProductMediaRepository media;

    public OrderService(OrderRepository orders, OrderItemRepository oitems, OrderStatusHistoryRepository hist, PaymentRepository pays, AddressRepository addrs,
                        ServiceablePincodeRepository pins, CartService cart, ProductRepository products, ProductVariantRepository variants, ProductMediaRepository media) {
        this.orders = orders; this.oitems = oitems; this.hist = hist; this.pays = pays; this.addrs = addrs; this.pins = pins; this.cart = cart; this.products = products; this.variants = variants; this.media = media;
    }

    @Transactional
    public Order place(Long uid, PlaceRequest r) {
        Address a = addrs.findByIdAndUserId(r.addressId(), uid).orElseThrow(() -> new BadRequestException("Choose a delivery address"));
        var pin = pins.findByPincode(a.pincode).orElseGet(() -> { if (a.pincode == null || !a.pincode.matches("\\d{6}")) throw new BadRequestException("Enter a valid 6-digit delivery pincode"); var x = new com.tidemart.delivery.ServiceablePincode(); x.pincode = a.pincode; x.codAvailable = true; return x; });
        String pay = r.paymentMethod() == null ? "COD" : r.paymentMethod().toUpperCase();
        if (!List.of("COD", "UPI", "CARD", "WALLET").contains(pay)) throw new BadRequestException("Unknown payment method");
        if ("COD".equals(pay) && !pin.codAvailable) throw new BadRequestException("Cash on delivery is not available here");
        if (("UPI".equals(pay) || "CARD".equals(pay)) && !r.demoPaymentOk()) throw new BadRequestException("Payment failed. No money was taken. Please try again.");
        List<CartService.Line> lines = cart.lines(uid);
        if (lines.isEmpty()) throw new BadRequestException("Your cart is empty");
        for (var l : lines) if (l.qty() > l.stock()) throw new BadRequestException(l.name() + " has only " + l.stock() + " left");
        var p = cart.price(lines, r.coupon(), pay);
        Order o = new Order();
        o.orderNo = "TM" + (100000 + new Random().nextInt(900000)); o.userId = uid;
        o.shipName = a.name; o.shipPhone = a.phone; o.shipLine1 = a.line1; o.shipCity = a.city; o.shipState = a.state; o.shipPincode = a.pincode;
        o.subtotal = p.subtotal(); o.discount = p.discount(); o.deliveryFee = p.deliveryFee(); o.codFee = p.codFee(); o.total = p.total();
        o.paymentMethod = pay; o.paymentStatus = "COD".equals(pay) ? "PENDING" : "SUCCESS";
        o = orders.save(o);
        if ("WALLET".equals(pay)) wallet.debit(uid, o.total, "Paid for order " + o.orderNo);
        for (var l : lines) {
            OrderItem i = new OrderItem();
            i.orderId = o.id; i.productId = l.productId(); i.variantId = l.variantId(); i.qty = l.qty(); i.unitPrice = l.price();
            i.sellerId = products.findById(l.productId()).orElseThrow().sellerId;
            oitems.save(i);
            ProductVariant v = variants.findById(l.variantId()).orElseThrow();
            v.stock -= l.qty(); variants.save(v);
        }
        Payment pm = new Payment();
        pm.orderId = o.id; pm.method = pay; pm.amount = o.total; pm.status = o.paymentStatus; pm.txnRef = "DEMO" + o.orderNo;
        pays.save(pm);
        log(o.id, "PLACED", "Order placed");
        notifier.notify(uid, "Order placed", "Your order " + o.orderNo + " is confirmed.");
        final String no = o.orderNo;
        oitems.findByOrderId(o.id).stream().map(i -> i.sellerId).distinct().forEach(sid -> sellerRepo.findById(sid).ifPresent(s -> notifier.notify(s.userId, "New order", "Order " + no + " is waiting to be packed.")));
        cart.clear(uid);
        return o;
    }

    public List<Summary> list(Long uid) { return orders.findByUserIdOrderByIdDesc(uid).stream().map(o -> new Summary(o, oitems.findByOrderId(o.id).stream().map(i -> new ItemLine(i.id, i.productId, i.variantId, products.findById(i.productId).map(p -> p.name).orElse("Product"), variants.findById(i.variantId).map(v -> v.size).orElse(""), i.qty, i.unitPrice, media.findByProductIdOrderBySortOrderAsc(i.productId).stream().filter(m -> !"VIDEO".equals(m.type)).findFirst().map(m -> m.url).orElse(null))).toList())).toList(); }
    public record Summary(Order order, List<ItemLine> items) {}

    public Detail detail(Long uid, Long id) {
        Order o = mine(uid, id);
        List<ItemLine> items = oitems.findByOrderId(id).stream().map(i -> new ItemLine(i.id, i.productId, i.variantId, products.findById(i.productId).map(p -> p.name).orElse("Product"),
                variants.findById(i.variantId).map(v -> v.size).orElse(""), i.qty, i.unitPrice, media.findByProductIdOrderBySortOrderAsc(i.productId).stream().filter(m -> !"VIDEO".equals(m.type)).findFirst().map(m -> m.url).orElse(null))).toList();
        return new Detail(o, items, hist.findByOrderIdOrderByIdAsc(id));
    }

    @Transactional
    public Detail cancel(Long uid, Long id) {
        Order o = mine(uid, id);
        if (!"PLACED".equals(o.status) && !"PACKED".equals(o.status)) throw new BadRequestException("This order can no longer be cancelled");
        for (OrderItem i : oitems.findByOrderId(id)) variants.findById(i.variantId).ifPresent(v -> { v.stock += i.qty; variants.save(v); });
        o.status = "CANCELLED";
        if ("SUCCESS".equals(o.paymentStatus)) { o.paymentStatus = "REFUNDED"; wallet.credit(uid, o.total, "Refund for cancelled order " + o.orderNo); }
        orders.save(o);
        log(id, "CANCELLED", "Cancelled by customer");
        notifier.notify(uid, "Order cancelled", "Order " + o.orderNo + " was cancelled.");
        return detail(uid, id);
    }

    /** Demo helper so you can see the tracking timeline. Sellers and delivery partners will do this in later phases. */
    @Transactional
    public Detail advance(Long uid, Long id) {
        Order o = mine(uid, id);
        int k = FLOW.indexOf(o.status);
        if (k < 0 || k == FLOW.size() - 1) throw new BadRequestException("Order cannot move further");
        o.status = FLOW.get(k + 1);
        if ("DELIVERED".equals(o.status) && "COD".equals(o.paymentMethod)) o.paymentStatus = "SUCCESS";
        orders.save(o);
        log(id, o.status, "Status updated");
        notifier.notify(uid, "Order update", "Order " + o.orderNo + " is now " + o.status.replace('_', ' ') + ".");
        return detail(uid, id);
    }

    private Order mine(Long uid, Long id) { return orders.findById(id).filter(x -> x.userId.equals(uid)).orElseThrow(() -> new ResourceNotFoundException("Order not found")); }

    private void log(Long orderId, String status, String note) { OrderStatusHistory h = new OrderStatusHistory(); h.orderId = orderId; h.status = status; h.note = note; hist.save(h); }
}
