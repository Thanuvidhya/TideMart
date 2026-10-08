package com.tidemart.order;

import com.tidemart.common.exception.BadRequestException;
import com.tidemart.common.exception.ResourceNotFoundException;
import com.tidemart.wallet.WalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReturnService {
    public record Request(Long orderId, Long orderItemId, String type, String reason) {}
    static final List<String> FLOW = List.of("REQUESTED", "APPROVED", "PICKED_UP", "REFUNDED");
    private final ReturnRepository returns;
    private final RefundRepository refunds;
    private final OrderRepository orders;
    private final OrderItemRepository items;
    private final WalletService wallet;

    public ReturnService(ReturnRepository returns, RefundRepository refunds, OrderRepository orders, OrderItemRepository items, WalletService wallet) {
        this.returns = returns; this.refunds = refunds; this.orders = orders; this.items = items; this.wallet = wallet;
    }

    public ReturnRequest request(Long uid, Request r) {
        Order o = orders.findById(r.orderId()).filter(x -> x.userId.equals(uid)).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!"DELIVERED".equals(o.status)) throw new BadRequestException("You can return an item after it is delivered");
        OrderItem i = items.findById(r.orderItemId()).filter(x -> x.orderId.equals(o.id)).orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        if (returns.findByOrderItemId(i.id).isPresent()) throw new BadRequestException("A return is already open for this item");
        ReturnRequest q = new ReturnRequest();
        q.orderId = o.id; q.orderItemId = i.id; q.type = "EXCHANGE".equalsIgnoreCase(r.type()) ? "EXCHANGE" : "RETURN"; q.reason = r.reason();
        return returns.save(q);
    }

    public List<ReturnRequest> list(Long uid) { return returns.findByOrderIdInOrderByIdDesc(orders.findByUserIdOrderByIdDesc(uid).stream().map(o -> o.id).toList()); }

    /** Demo helper to walk a return through pickup and refund. Admin and delivery partners will do this in later phases. */
    @Transactional
    public ReturnRequest advance(Long uid, Long id) {
        ReturnRequest q = returns.findById(id).orElseThrow(() -> new ResourceNotFoundException("Return not found"));
        Order o = orders.findById(q.orderId).filter(x -> x.userId.equals(uid)).orElseThrow(() -> new ResourceNotFoundException("Return not found"));
        int k = FLOW.indexOf(q.status);
        if (k < 0 || k == FLOW.size() - 1) throw new BadRequestException("Return cannot move further");
        q.status = FLOW.get(k + 1);
        if ("REFUNDED".equals(q.status) && "RETURN".equals(q.type)) {
            OrderItem i = items.findById(q.orderItemId).orElseThrow();
            Refund f = new Refund();
            f.orderId = o.id; f.returnId = q.id; f.amount = i.unitPrice * i.qty; f.method = "WALLET"; f.status = "DONE";
            refunds.save(f);
            wallet.credit(uid, f.amount, "Refund for order " + o.orderNo);
        }
        return returns.save(q);
    }
}
