package com.tidemart.delivery;

import com.tidemart.auth.OtpToken;
import com.tidemart.auth.OtpTokenRepository;
import com.tidemart.common.exception.BadRequestException;
import com.tidemart.common.exception.ResourceNotFoundException;
import com.tidemart.notification.NotificationService;
import com.tidemart.order.*;
import com.tidemart.product.ProductVariantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

/** Delivery steps run by the admin panel for now. A delivery partner app can call the same methods later. */
@Service
public class DeliveryService {
    static final int MAX_FAILED_ATTEMPTS = 3;
    private final OrderRepository orders;
    private final OrderItemRepository items;
    private final OrderStatusHistoryRepository hist;
    private final ProductVariantRepository variants;
    private final DeliveryPartnerRepository partners;
    private final DeliveryAttemptRepository attempts;
    private final CodCollectionRepository cod;
    private final OtpTokenRepository otps;
    private final NotificationService notifier;

    public DeliveryService(OrderRepository orders, OrderItemRepository items, OrderStatusHistoryRepository hist, ProductVariantRepository variants, DeliveryPartnerRepository partners,
                           DeliveryAttemptRepository attempts, CodCollectionRepository cod, OtpTokenRepository otps, NotificationService notifier) {
        this.orders = orders; this.items = items; this.hist = hist; this.variants = variants; this.partners = partners; this.attempts = attempts; this.cod = cod; this.otps = otps; this.notifier = notifier;
    }


    public List<java.util.Map<String, Object>> assigned(Long partnerId) {
        return attempts.findByPartnerIdOrderByIdDesc(partnerId).stream().map(a -> {
            Order o = order(a.orderId);
            java.util.Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("orderId", o.id); m.put("orderNo", o.orderNo); m.put("status", o.status); m.put("customer", o.shipName);
            m.put("phone", o.shipPhone); m.put("address", String.join(", ", java.util.Objects.toString(o.shipLine1, ""), java.util.Objects.toString(o.shipCity, ""), java.util.Objects.toString(o.shipPincode, "")));
            m.put("amount", o.total); m.put("paymentMethod", o.paymentMethod);
            return m;
        }).distinct().toList();
    }

    public Long partnerIdForPhone(String phone) {
        return partners.findByPhone(phone).map(p -> p.id).orElseThrow(() -> new ResourceNotFoundException("Delivery partner not found"));
    }

    /** Assigns a partner, moves the order to Out for delivery and sends the customer a delivery OTP. */
    @Transactional
    public String assign(Long orderId, Long partnerId) {
        Order o = order(orderId);
        if (!List.of("SHIPPED", "OUT_FOR_DELIVERY").contains(o.status)) throw new BadRequestException("The seller must ship the order first");
        DeliveryPartner p = partners.findById(partnerId).orElseThrow(() -> new ResourceNotFoundException("Delivery partner not found"));
        if ("INACTIVE".equalsIgnoreCase(p.status)) throw new BadRequestException("This delivery partner is not active");
        String otp = String.format("%06d", new SecureRandom().nextInt(1_000_000));
        OtpToken t = new OtpToken();
        t.phone = "DLV-" + o.orderNo; t.code = otp; t.expiresAt = LocalDateTime.now().plusDays(3);
        otps.save(t);
        o.status = "OUT_FOR_DELIVERY";
        orders.save(o);
        log(o.id, "OUT_FOR_DELIVERY", "Assigned to " + p.name);
        attempt(o.id, p.id, "ASSIGNED", "Assigned to " + p.name);
        notifier.notify(o.userId, "Out for delivery", "Order " + o.orderNo + " is out for delivery. Share OTP " + otp + " with the delivery partner.");
        return otp;
    }

    /** Delivered only when the customer's OTP is entered. Cash on delivery is recorded as collected. */
    @Transactional
    public void deliver(Long orderId, String otp) {
        deliver(orderId, otp, null);
    }

    @Transactional
    public void deliver(Long orderId, String otp, Long partnerId) {
        Order o = order(orderId);
        if (partnerId != null && !partnerId.equals(partnerOf(orderId))) throw new BadRequestException("This order is assigned to another delivery partner");
        if (!"OUT_FOR_DELIVERY".equals(o.status)) throw new BadRequestException("The order is not out for delivery");
        OtpToken t = otps.findTopByPhoneAndUsedFalseOrderByIdDesc("DLV-" + o.orderNo).orElseThrow(() -> new BadRequestException("No delivery OTP found"));
        if (t.expiresAt.isBefore(LocalDateTime.now()) || !t.code.equals(otp)) throw new BadRequestException("Wrong or expired OTP");
        t.used = true;
        otps.save(t);
        Long assignedPartnerId = partnerOf(orderId);
        o.status = "DELIVERED";
        if ("COD".equals(o.paymentMethod)) {
            o.paymentStatus = "SUCCESS";
            CodCollection c = new CodCollection();
            c.orderId = o.id; c.partnerId = assignedPartnerId; c.amount = o.total; c.status = "COLLECTED"; c.collectedAt = LocalDateTime.now();
            cod.save(c);
        }
        orders.save(o);
        log(o.id, "DELIVERED", "Delivered with OTP");
        attempt(o.id, assignedPartnerId, "DELIVERED", "OTP confirmed");
        notifier.notify(o.userId, "Delivered", "Order " + o.orderNo + " was delivered. Rate your items on their product pages.");
    }

    /** After 3 failed attempts the order is cancelled and the stock goes back. */
    @Transactional
    public String failed(Long orderId, String note) {
        return failed(orderId, note, null);
    }

    @Transactional
    public String failed(Long orderId, String note, Long partnerId) {
        Order o = order(orderId);
        if (partnerId != null && !partnerId.equals(partnerOf(orderId))) throw new BadRequestException("This order is assigned to another delivery partner");
        if (!"OUT_FOR_DELIVERY".equals(o.status)) throw new BadRequestException("The order is not out for delivery");
        attempt(o.id, partnerOf(orderId), "FAILED", note == null ? "Customer not available" : note);
        long fails = attempts.findByOrderIdOrderByIdAsc(orderId).stream().filter(a -> "FAILED".equals(a.status)).count();
        if (fails >= MAX_FAILED_ATTEMPTS) {
            for (OrderItem i : items.findByOrderId(orderId)) variants.findById(i.variantId).ifPresent(v -> { v.stock += i.qty; variants.save(v); });
            o.status = "CANCELLED";
            if ("SUCCESS".equals(o.paymentStatus)) o.paymentStatus = "REFUNDED";
            orders.save(o);
            log(o.id, "CANCELLED", "Returned to seller after 3 failed attempts");
            notifier.notify(o.userId, "Order cancelled", "Order " + o.orderNo + " could not be delivered after 3 attempts.");
            return "Cancelled after " + fails + " failed attempts";
        }
        notifier.notify(o.userId, "Delivery attempt failed", "We could not deliver order " + o.orderNo + ". We will try again.");
        return "Attempt " + fails + " of " + MAX_FAILED_ATTEMPTS + " recorded";
    }

    private Long partnerOf(Long orderId) {
        return attempts.findByOrderIdOrderByIdAsc(orderId).stream().map(a -> a.partnerId).filter(x -> x != null).reduce((a, b) -> b).orElse(null);
    }

    private void attempt(Long orderId, Long partnerId, String status, String note) {
        DeliveryAttempt a = new DeliveryAttempt();
        a.orderId = orderId; a.partnerId = partnerId; a.status = status; a.note = note;
        attempts.save(a);
    }

    private void log(Long orderId, String status, String note) { OrderStatusHistory h = new OrderStatusHistory(); h.orderId = orderId; h.status = status; h.note = note; hist.save(h); }

    private Order order(Long id) { return orders.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found")); }
}
