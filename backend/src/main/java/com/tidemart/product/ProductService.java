package com.tidemart.product;

import com.tidemart.category.CategoryRepository;
import com.tidemart.common.exception.ResourceNotFoundException;
import com.tidemart.product.dto.ProductCard;
import com.tidemart.product.dto.ProductDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository products;
    private final ProductVariantRepository variants;
    private final ProductMediaRepository media;
    private final CategoryRepository categories;
    @org.springframework.beans.factory.annotation.Autowired
    private com.tidemart.seller.SellerRepository sellers;
    @org.springframework.beans.factory.annotation.Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbc;

    public ProductService(ProductRepository products, ProductVariantRepository variants, ProductMediaRepository media, CategoryRepository categories) {
        this.products = products; this.variants = variants; this.media = media; this.categories = categories;
    }

    public Page<ProductCard> search(String q, long cat, double min, double max, String sort, int page, int size) {
        Sort s = switch (sort) {
            case "price_asc" -> Sort.by("price").ascending();
            case "price_desc" -> Sort.by("price").descending();
            case "newest" -> Sort.by("createdAt").descending();
            case "rating" -> Sort.by("ratingAvg").descending();
            default -> Sort.by("ratingCount").descending();
        };
        Page<ProductCard> pg = products.search(q, cat, min, max, PageRequest.of(page, size, s)).map(this::card);
        List<ProductCard> l = new java.util.ArrayList<>(pg.getContent());
        l.sort((a, b) -> Boolean.compare(b.promoted(), a.promoted()));
        return new org.springframework.data.domain.PageImpl<>(l, pg.getPageable(), pg.getTotalElements());
    }

    public ProductDetail detail(Long id) {
        Product p = products.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        String cat = categories.findById(p.categoryId).map(c -> c.name).orElse("");
        List<ProductCard> similar = products.findTop6ByCategoryIdAndIdNotAndStatusOrderByRatingCountDesc(p.categoryId, id, "APPROVED").stream().map(this::card).toList();
        var sel = sellers.findById(p.sellerId);
        var sale = flash(id);
        return new ProductDetail(p, cat, variants.findByProductId(id), media.findByProductIdOrderBySortOrderAsc(id), similar, sel.map(s -> s.businessName).orElse("Tidemart seller"), sel.map(s -> s.rating).orElse(0.0), sale == null ? null : ((Number) sale.get("sale_price")).doubleValue(), sale == null ? null : String.valueOf(sale.get("ends_at")));
    }

    public List<ProductCard> trending() { return products.findTop24ByStatusOrderByRatingCountDesc("APPROVED").stream().map(this::card).toList(); }

    public List<ProductCard> categoryProducts(Long categoryId) { return products.findTop12ByCategoryIdAndStatusOrderByRatingCountDesc(categoryId, "APPROVED").stream().map(this::card).toList(); }

    public List<String> suggest(String q) { return products.findTop5ByStatusAndNameContainingIgnoreCase("APPROVED", q).stream().map(p -> p.name).toList(); }

    /** The newest flash sale that is running right now, or null. */
    public java.util.Map<String, Object> flash(Long productId) {
        try {
            var l = jdbc.queryForList("select sale_price, ends_at from flash_sales where product_id = ? and now() between starts_at and ends_at order by id desc limit 1", productId);
            return l.isEmpty() ? null : l.get(0);
        } catch (Exception e) { return null; }
    }

    public double effectivePrice(Product p) {
        var f = flash(p.id);
        double sale = f == null ? p.price : ((Number) f.get("sale_price")).doubleValue();
        return sale > 0 && sale < p.price ? sale : p.price;
    }

    private boolean promoted(Long productId) {
        try { return jdbc.queryForObject("select count(*) from product_promotions where product_id = ? and ends_at > now()", Integer.class, productId) > 0; }
        catch (Exception e) { return false; }
    }

    public ProductCard card(Product p) {
        int stock = variants.findByProductId(p.id).stream().mapToInt(v -> v.stock).sum();
        String img = media.findByProductIdOrderBySortOrderAsc(p.id).stream().filter(m -> !"VIDEO".equals(m.type)).findFirst().map(m -> m.url).orElse(null);
        double price = effectivePrice(p);
        int disc = p.mrp > 0 ? (int) Math.round((1 - price / p.mrp) * 100) : 0;
        var seller = sellers.findById(p.sellerId);
        return new ProductCard(p.id, p.name, price, p.mrp, disc, p.ratingAvg, p.ratingCount, stock, img, promoted(p.id), seller.map(x->x.businessName).orElse("TideMart Store"), seller.map(x->x.rating).orElse(0.0));
    }
}
