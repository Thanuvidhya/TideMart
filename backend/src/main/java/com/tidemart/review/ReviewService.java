package com.tidemart.review;

import com.tidemart.common.exception.BadRequestException;
import com.tidemart.order.*;
import com.tidemart.product.Product;
import com.tidemart.product.ProductRepository;
import com.tidemart.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {
    public record Request(Long productId, int rating, String comment) {}
    public record View(Long id, String user, int rating, String comment, String createdAt) {}
    private final ReviewRepository reviews;
    private final ProductRepository products;
    private final UserRepository users;
    private final OrderRepository orders;
    private final OrderItemRepository items;

    public ReviewService(ReviewRepository reviews, ProductRepository products, UserRepository users, OrderRepository orders, OrderItemRepository items) {
        this.reviews = reviews; this.products = products; this.users = users; this.orders = orders; this.items = items;
    }

    public List<View> list(Long productId) {
        return reviews.findByProductIdOrderByIdDesc(productId).stream().map(r -> new View(r.id,
                users.findById(r.userId).map(u -> u.name == null ? "Customer" : u.name).orElse("Customer"), r.rating, r.comment, String.valueOf(r.createdAt).substring(0, 10))).toList();
    }

    @Transactional
    public Review add(Long uid, Request q) {
        if (q.rating() < 1 || q.rating() > 5) throw new BadRequestException("Choose 1 to 5 stars");
        Order bought = orders.findByUserIdOrderByIdDesc(uid).stream().filter(o -> "DELIVERED".equals(o.status))
                .filter(o -> items.findByOrderId(o.id).stream().anyMatch(i -> i.productId.equals(q.productId()))).findFirst()
                .orElseThrow(() -> new BadRequestException("You can review a product after it is delivered to you"));
        if (reviews.findByProductIdAndUserId(q.productId(), uid).isPresent()) throw new BadRequestException("You already reviewed this product");
        Review r = new Review();
        r.productId = q.productId(); r.userId = uid; r.orderId = bought.id; r.rating = q.rating(); r.comment = q.comment();
        r = reviews.save(r);
        Product p = products.findById(q.productId()).orElseThrow();
        p.ratingAvg = (p.ratingAvg * p.ratingCount + q.rating()) / (p.ratingCount + 1);
        p.ratingCount += 1;
        products.save(p);
        return r;
    }
}
