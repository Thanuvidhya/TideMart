package com.tidemart.cart;

import com.tidemart.common.exception.BadRequestException;
import com.tidemart.coupon.CouponService;
import com.tidemart.delivery.ShippingRuleRepository;
import com.tidemart.product.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {
    public record Line(Long itemId, Long productId, Long variantId, String name, String size, double price, double mrp, int discountPercent, int qty, int stock, String imageUrl, String sellerName, double sellerRating) {}
    public record Price(double subtotal, double discount, double deliveryFee, double codFee, double total, String couponMessage) {}
    public record View(List<Line> items, Price price, List<Line> saved) {}
    @org.springframework.beans.factory.annotation.Autowired
    private com.tidemart.product.ProductService productService;
    private final CartRepository carts;
    private final CartItemRepository items;
    private final ProductRepository products;
    private final ProductVariantRepository variants;
    private final CouponService coupons;
    private final ShippingRuleRepository rules;
    private final com.tidemart.seller.SellerRepository sellers;

    public CartService(CartRepository carts, CartItemRepository items, ProductRepository products, ProductVariantRepository variants, CouponService coupons, ShippingRuleRepository rules, com.tidemart.seller.SellerRepository sellers) {
        this.carts = carts; this.items = items; this.products = products; this.variants = variants; this.coupons = coupons; this.rules = rules; this.sellers = sellers;
    }

    public Cart cartOf(Long uid) { return carts.findByUserId(uid).orElseGet(() -> { Cart c = new Cart(); c.userId = uid; return carts.save(c); }); }

    public View view(Long uid, String coupon, String pay) { List<Line> l = lines(uid); return new View(l, price(l, coupon, pay), saved(uid)); }

    public List<Line> lines(Long uid) {
        return mapLines(items.findByCartId(cartOf(uid).id).stream().filter(i -> !i.savedForLater).toList());
    }

    public List<Line> saved(Long uid) { return mapLines(items.findByCartId(cartOf(uid).id).stream().filter(i -> i.savedForLater).toList()); }

    private List<Line> mapLines(List<CartItem> list) {
        return list.stream().map(i -> {
            Product p = products.findById(i.productId).orElseThrow();
            ProductVariant v = variants.findById(i.variantId).orElseThrow();
            double price = productService.effectivePrice(p);
            int disc = p.mrp > 0 ? (int)Math.round((1 - price / p.mrp) * 100) : 0;
            String image = productService.card(p).imageUrl();
            String sellerName = sellers.findById(p.sellerId).map(x -> x.businessName).orElse("TideMart Store"); double sellerRating = sellers.findById(p.sellerId).map(x -> x.rating).orElse(0.0);
            return new Line(i.id, p.id, v.id, p.name, v.size, price, p.mrp, disc, i.qty, v.stock, image, sellerName, sellerRating);
        }).toList();
    }

    public Price price(List<Line> l, String coupon, String pay) {
        double sub = l.stream().mapToDouble(x -> x.price() * x.qty()).sum();
        var r = coupons.apply(coupon, sub);
        double after = sub - r.discount(), fee = sub == 0 ? 0 : deliveryFee(sub), cod = ("COD".equals(pay) && sub > 0 && after < 999) ? 29 : 0;
        return new Price(sub, r.discount(), fee, cod, after + fee + cod, r.message());
    }

    private double deliveryFee(double sub) {
        return rules.findByActiveTrue().stream().filter(r -> sub >= r.minOrder && sub <= r.maxOrder).findFirst().map(r -> r.fee).orElse(sub >= 499 ? 0.0 : 49.0);
    }

    public void add(Long uid, Long productId, Long variantId, int qty) {
        ProductVariant v = variants.findById(variantId).filter(x -> x.productId.equals(productId)).orElseThrow(() -> new BadRequestException("Choose a size first"));
        Cart c = cartOf(uid);
        CartItem it = items.findByCartId(c.id).stream().filter(x -> x.variantId.equals(variantId)).findFirst().orElseGet(() -> { CartItem n = new CartItem(); n.cartId = c.id; n.productId = productId; n.variantId = variantId; return n; });
        it.qty += Math.max(1, qty);
        if (it.qty > v.stock) throw new BadRequestException("Only " + v.stock + " left in stock");
        items.save(it);
    }

    public void setQty(Long uid, Long itemId, int qty) {
        CartItem it = items.findById(itemId).filter(x -> x.cartId.equals(cartOf(uid).id)).orElseThrow(() -> new BadRequestException("Item not in cart"));
        if (qty <= 0) items.delete(it); else { it.qty = qty; items.save(it); }
    }

    public void clear(Long uid) { items.deleteAll(items.findByCartId(cartOf(uid).id).stream().filter(i -> !i.savedForLater).toList()); }

    public void setSaved(Long uid, Long itemId, boolean saved) {
        CartItem it = items.findById(itemId).filter(x -> x.cartId.equals(cartOf(uid).id)).orElseThrow(() -> new BadRequestException("Item not in cart"));
        it.savedForLater = saved;
        items.save(it);
    }
}
