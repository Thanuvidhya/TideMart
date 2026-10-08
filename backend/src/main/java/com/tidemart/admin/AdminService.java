package com.tidemart.admin;

import com.tidemart.common.Role;
import com.tidemart.common.exception.BadRequestException;
import com.tidemart.common.exception.ResourceNotFoundException;
import com.tidemart.order.*;
import com.tidemart.seller.Seller;
import com.tidemart.seller.SellerRepository;
import com.tidemart.user.User;
import com.tidemart.user.UserRepository;
import com.tidemart.wallet.WalletService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AdminService {
    private static final Map<String, List<String>> READ = new LinkedHashMap<>(), WRITE = new HashMap<>();
    private static final List<String> RETURN_FLOW = List.of("REQUESTED", "APPROVED", "PICKED_UP", "REFUNDED", "REJECTED");

    private static void def(String table, String read, String write) {
        READ.put(table, List.of(read.split(",")));
        WRITE.put(table, write.isEmpty() ? List.of() : List.of(write.split(",")));
    }

    static {
        def("users", "id,phone,email,name,role,status,created_at", "status");
        def("sellers", "id,user_id,business_name,gst_no,pan_no,status,rating", "");
        def("products", "id,seller_id,category_id,name,price,mrp,status,created_at", "status");
        def("orders", "id,order_no,user_id,reseller_id,total,payment_method,payment_status,status,created_at", "status,payment_status");
        def("return_requests", "id,order_id,order_item_id,type,reason,status,created_at", "");
        def("categories", "id,parent_id,name,slug,image_url,active", "parent_id,name,slug,image_url,active");
        def("coupons", "id,code,type,value,max_discount,min_order,valid_from,valid_till,usage_limit,active", "code,type,value,max_discount,min_order,valid_from,valid_till,usage_limit,active");
        def("commission_rules", "id,category_id,percent", "category_id,percent");
        def("shipping_rules", "id,min_order,max_order,fee,active", "min_order,max_order,fee,active");
        def("tax_settings", "id,name,percent", "name,percent");
        def("payouts", "id,party_type,party_id,amount,method,status,created_at", "party_type,party_id,amount,method,status");
        def("banners", "id,title,image_url,link,sort_order,active", "title,image_url,link,sort_order,active");
        def("policy_pages", "id,slug,title,content", "slug,title,content");
        def("support_tickets", "id,user_id,order_id,subject,status,created_at", "status");
        def("faqs", "id,question,answer,sort_order", "question,answer,sort_order");
        def("cod_collections", "id,order_id,partner_id,amount,status,collected_at,settled_at", "status");
        def("return_pickups", "id,return_id,partner_id,scheduled_on,status", "return_id,partner_id,scheduled_on,status");
        def("delivery_attempts", "id,order_id,partner_id,status,note,created_at", "");
        def("audit_log", "id,admin_id,action,detail,created_at", "");
        def("flash_sales", "id,product_id,sale_price,starts_at,ends_at", "product_id,sale_price,starts_at,ends_at");
        def("product_promotions", "id,product_id,seller_id,ends_at,created_at", "");
        def("delivery_partners", "id,name,phone,status", "name,phone,status");
    }

    private final JdbcTemplate jdbc;
    private final SellerRepository sellers;
    private final UserRepository users;
    private final ReturnRepository returns;
    private final RefundRepository refunds;
    private final OrderRepository orders;
    private final OrderItemRepository items;
    private final WalletService wallet;

    public AdminService(JdbcTemplate jdbc, SellerRepository sellers, UserRepository users, ReturnRepository returns, RefundRepository refunds, OrderRepository orders, OrderItemRepository items, WalletService wallet) {
        this.jdbc = jdbc; this.sellers = sellers; this.users = users; this.returns = returns; this.refunds = refunds; this.orders = orders; this.items = items; this.wallet = wallet;
    }

    public Map<String, Object> stats() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("users", n("select count(*) from users"));
        m.put("sellers", n("select count(*) from sellers"));
        m.put("pendingSellers", n("select count(*) from sellers where status = 'PENDING'"));
        m.put("pendingProducts", n("select count(*) from products where status = 'PENDING'"));
        m.put("orders", n("select count(*) from orders"));
        m.put("openReturns", n("select count(*) from return_requests where status not in ('REFUNDED','REJECTED')"));
        m.put("sales", jdbc.queryForObject("select coalesce(sum(total), 0) from orders where status <> 'CANCELLED'", Double.class));
        m.put("ordersByStatus", jdbc.queryForList("select status, count(*) as count from orders group by status"));
        m.put("dailySales", jdbc.queryForList("select date(created_at) as day, sum(total) as sales, count(*) as orders from orders where status <> 'CANCELLED' group by date(created_at) order by day desc limit 7"));
        m.put("topProducts", jdbc.queryForList("select p.name as name, sum(oi.qty) as units from order_items oi join products p on p.id = oi.product_id join orders o on o.id = oi.order_id where o.status <> 'CANCELLED' group by p.id, p.name order by units desc limit 5"));
        return m;
    }

    public List<Map<String, Object>> list(String table) {
        return jdbc.queryForList("select " + String.join(",", cols(READ, table)) + " from " + table + " order by id desc limit 300");
    }

    public void create(String table, Map<String, Object> body) {
        List<String> c = writable(table, body);
        audit("CREATE " + table, String.valueOf(body));
        jdbc.update("insert into " + table + " (" + String.join(",", c) + ") values (" + "?,".repeat(c.size()).replaceAll(",$", "") + ")", c.stream().map(body::get).toArray());
    }

    public void update(String table, Long id, Map<String, Object> body) {
        List<String> c = writable(table, body);
        audit("UPDATE " + table + " #" + id, String.valueOf(body));
        jdbc.update("update " + table + " set " + String.join(",", c.stream().map(x -> x + " = ?").toList()) + " where id = ?", concat(c.stream().map(body::get).toList(), id));
        if ("orders".equals(table) && body.get("status") != null)
            jdbc.update("insert into order_status_history (order_id, status, note) values (?, ?, 'Updated by admin')", id, body.get("status"));
        if ("cod_collections".equals(table) && "SETTLED".equals(body.get("status"))) jdbc.update("update cod_collections set settled_at = now() where id = ?", id);
    }

    @Transactional
    public void sellerDecision(Long id, boolean approve) {
        Seller s = sellers.findById(id).orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        s.status = approve ? "APPROVED" : "REJECTED";
        audit("SELLER " + s.status, "seller #" + id);
        sellers.save(s);
        if (approve) { User u = users.findById(s.userId).orElseThrow(); u.role = Role.SELLER; users.save(u); }
    }

    @Transactional
    public void returnStatus(Long id, String to) {
        if (!RETURN_FLOW.contains(to)) throw new BadRequestException("Unknown status");
        ReturnRequest q = returns.findById(id).orElseThrow(() -> new ResourceNotFoundException("Return not found"));
        audit("RETURN " + to, "return #" + id);
        if ("REFUNDED".equals(to) && !"REFUNDED".equals(q.status) && "RETURN".equals(q.type)) {
            OrderItem i = items.findById(q.orderItemId).orElseThrow();
            Order o = orders.findById(q.orderId).orElseThrow();
            Refund f = new Refund();
            f.orderId = o.id; f.returnId = q.id; f.amount = i.unitPrice * i.qty; f.method = "WALLET"; f.status = "DONE";
            refunds.save(f);
            wallet.credit(o.userId, f.amount, "Refund for order " + o.orderNo);
        }
        q.status = to;
        returns.save(q);
    }

    /** Needs the audit_log table. Failures are ignored so admin actions never break. */
    private void audit(String action, String detail) {
        try {
            Object p = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            jdbc.update("insert into audit_log (admin_id, action, detail) values (?, ?, ?)", p instanceof Long ? p : null, action, detail.length() > 250 ? detail.substring(0, 250) : detail);
        } catch (Exception ignored) { }
    }

    /** Simple rules to review by hand: many cancellations, big COD orders, one phone on many accounts. */
    public List<Map<String, Object>> fraud() {
        List<Map<String, Object>> out = new ArrayList<>();
        jdbc.queryForList("select u.id as uid, coalesce(u.name, u.phone, u.email) as who, count(*) as n from orders o join users u on u.id = o.user_id where o.status = 'CANCELLED' and o.created_at > now() - interval 7 day group by u.id, u.name, u.phone, u.email having count(*) >= 3")
                .forEach(r -> out.add(flag(out.size(), "Many cancellations", r.get("who") + " cancelled " + r.get("n") + " orders in 7 days")));
        jdbc.queryForList("select order_no, total from orders where payment_method = 'COD' and total >= 10000 and status not in ('DELIVERED','CANCELLED')")
                .forEach(r -> out.add(flag(out.size(), "Large COD order", r.get("order_no") + " for Rs " + r.get("total"))));
        jdbc.queryForList("select ship_phone as phone, count(distinct user_id) as n from orders group by ship_phone having count(distinct user_id) >= 3")
                .forEach(r -> out.add(flag(out.size(), "Phone on many accounts", r.get("phone") + " used by " + r.get("n") + " accounts")));
        return out;
    }

    private Map<String, Object> flag(int i, String type, String detail) { Map<String, Object> m = new LinkedHashMap<>(); m.put("id", i + 1); m.put("type", type); m.put("detail", detail); return m; }

    private List<String> writable(String table, Map<String, Object> body) {
        List<String> c = cols(WRITE, table).stream().filter(body::containsKey).toList();
        if (c.isEmpty()) throw new BadRequestException("Nothing to save for " + table);
        return c;
    }

    private List<String> cols(Map<String, List<String>> m, String table) {
        List<String> c = m.get(table);
        if (c == null) throw new BadRequestException("Unknown table");
        return c;
    }

    private Object[] concat(List<Object> a, Object last) { List<Object> l = new ArrayList<>(a); l.add(last); return l.toArray(); }

    private Long n(String sql) { return jdbc.queryForObject(sql, Long.class); }
}
