package com.tidemart.config;

import com.tidemart.common.Role;
import com.tidemart.user.User;
import com.tidemart.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@org.springframework.core.annotation.Order(1)
public class DataInitializer implements CommandLineRunner {
    private static final String[] CATS = {"Women", "Men", "Kids", "Home", "Beauty", "Footwear", "Jewellery", "Electronics"};

    /** Demo catalogue: four distinct products per shopping category. */
    private static final String[][] DEMO = {
        {"Floral Cotton Kurti", "women", "399", "899", "4.3", "S,M,L,XL"},
        {"Printed Anarkali Kurta Set", "women", "649", "1499", "4.5", "S,M,L,XL"},
        {"Soft Rayon Daily Saree", "women", "549", "1299", "4.4", "Free"},
        {"Party Wear Chiffon Dress", "women", "799", "1799", "4.2", "S,M,L,XL"},

        {"Striped Casual Shirt", "men", "449", "999", "4.1", "S,M,L,XL"},
        {"Slim Fit Stretch Jeans", "men", "599", "1299", "4.2", "28,30,32,34,36"},
        {"Cotton Polo T-Shirt", "men", "329", "799", "4.4", "S,M,L,XL"},
        {"Printed Casual Kurta", "men", "499", "1099", "4.3", "S,M,L,XL,XXL"},

        {"Kids Dino Tee Set", "kids", "299", "699", "4.4", "2-3Y,4-5Y,6-7Y"},
        {"Girls Floral Frock", "kids", "399", "899", "4.5", "2-3Y,4-5Y,6-7Y,8-9Y"},
        {"Kids Denim Dungaree", "kids", "449", "999", "4.4", "2-3Y,4-5Y,6-7Y,8-9Y"},
        {"Boys Cotton Hoodie", "kids", "429", "999", "4.2", "4-5Y,6-7Y,8-9Y,10-11Y"},

        {"Ceramic Coffee Mug Set", "home", "249", "599", "4.2", "Free"},
        {"Cotton Bedsheet Double", "home", "499", "1199", "4.3", "Double"},
        {"Non Stick Kitchen Pan", "home", "699", "1399", "4.4", "Free"},
        {"Decorative Cushion Cover Set", "home", "299", "699", "4.1", "Free"},

        {"Matte Lipstick Combo", "beauty", "199", "499", "4.0", "Free"},
        {"Vitamin C Face Serum", "beauty", "279", "699", "4.3", "30ml"},
        {"Hydrating Face Wash Duo", "beauty", "179", "399", "4.1", "Free"},
        {"Makeup Brush Set", "beauty", "249", "599", "4.4", "Free"},

        {"Everyday Sneakers", "footwear", "699", "1599", "4.5", "6,7,8,9,10"},
        {"Block Heel Sandals", "footwear", "549", "1199", "4.0", "5,6,7,8"},
        {"Classic Casual Loafers", "footwear", "649", "1399", "4.3", "6,7,8,9,10"},
        {"Comfort Slippers", "footwear", "249", "499", "4.2", "6,7,8,9,10"},

        {"Oxidised Jhumka Earrings", "jewellery", "299", "799", "4.5", "Free"},
        {"Minimal Gold Tone Necklace", "jewellery", "399", "999", "4.3", "Free"},
        {"Kundan Festive Jewellery Set", "jewellery", "699", "1599", "4.4", "Free"},
        {"Stackable Fashion Bangles", "jewellery", "249", "599", "4.2", "2.4,2.6,2.8"},

        {"Wireless Bluetooth Earbuds", "electronics", "799", "1999", "4.3", "Free"},
        {"Smart Fitness Band", "electronics", "699", "1499", "4.1", "Free"},
        {"Portable Mini Speaker", "electronics", "599", "1299", "4.4", "Free"},
        {"Slim Wireless Keyboard", "electronics", "899", "1799", "4.2", "Free"}
    };

    private final UserRepository users;
    private final PasswordEncoder enc;
    private final JdbcTemplate jdbc;

    public DataInitializer(UserRepository users, PasswordEncoder enc, JdbcTemplate jdbc) {
        this.users = users;
        this.enc = enc;
        this.jdbc = jdbc;
    }

    @Override
    public void run(String... args) {
        ensureAdmin();
        ensureDeliveryPartner();
        ensureCategories();
        java.util.List<Long> sellerIds = ensureDemoSellers();
        ensureProducts(sellerIds);
    }

    private void ensureAdmin() {
        User u = users.findByEmail("admin@tidemart.com").orElseGet(User::new);
        u.name = "Admin";
        u.email = "admin@tidemart.com";
        u.passwordHash = enc.encode("Admin@123");
        u.role = Role.ADMIN;
        u.status = "ACTIVE";
        u.referralCode = "ADMIN001";
        users.save(u);
    }

    private void ensureDeliveryPartner() {
        String phone = "9000000001";
        User u = users.findByEmail("delivery@tidemart.com").orElseGet(User::new);
        u.name = "TideMart Delivery";
        u.email = "delivery@tidemart.com";
        u.phone = phone;
        u.passwordHash = enc.encode("Delivery@123");
        u.role = Role.DELIVERY;
        u.status = "ACTIVE";
        u.referralCode = "DELIVERY01";
        u = users.save(u);
        Integer count = jdbc.queryForObject("select count(*) from delivery_partners where phone = ?", Integer.class, phone);
        if (count == null || count == 0) jdbc.update("insert into delivery_partners (name, phone, status) values (?, ?, 'ACTIVE')", "TideMart Delivery", phone);
    }

    private void ensureCategories() {
        for (String c : CATS) {
            Integer count = jdbc.queryForObject("select count(*) from categories where slug = ?", Integer.class, c.toLowerCase());
            if (count == null || count == 0) {
                jdbc.update("insert into categories (name, slug) values (?, ?)", c, c.toLowerCase());
            }
        }
    }

    private java.util.List<Long> ensureDemoSellers() {
        String[] names = {"Urban Loom", "StyleNest", "KidsJoy", "HomeAura", "GlowHub", "FootStreet", "JewelBox", "TrendKart"};
        java.util.List<Long> ids = new java.util.ArrayList<>();
        for (int i=0;i<names.length;i++) {
            final int idx=i; final String storeName=names[i];
            String email = "store"+(i+1)+"@tidemart.com";
            User seller = users.findByEmail(email).orElseGet(() -> { User u=new User(); u.name=storeName+" Owner"; u.email=email; u.passwordHash=enc.encode("Store@123"); u.role=Role.SELLER; u.referralCode=("STORE"+(idx+1)); return users.save(u); });
            Integer count=jdbc.queryForObject("select count(*) from sellers where user_id = ?",Integer.class,seller.id);
            if(count==null||count==0) jdbc.update("insert into sellers (user_id,business_name,status,rating) values (?,?,'APPROVED',?)",seller.id,names[i],4.0+(i%5)*0.2);
            ids.add(jdbc.queryForObject("select id from sellers where user_id = ?",Long.class,seller.id));
        }
        return ids;
    }

    private void ensureProducts(java.util.List<Long> sellerIds) {
        Map<String, Long> categoryIds = new LinkedHashMap<>();
        for (String c : CATS) {
            Long id = jdbc.queryForObject("select id from categories where slug = ?", Long.class, c.toLowerCase());
            categoryIds.put(c.toLowerCase(), id);
        }

        // Keep the original hand-picked catalogue.
        for (String[] p : DEMO) insertProductIfMissing(categoryIds, sellerIds, p[0], p[1],
                Double.parseDouble(p[2]), Double.parseDouble(p[3]), Double.parseDouble(p[4]), p[5]);

        // Add a large, realistic catalogue: 21 new products per category = 168 more.
        // Together with the original 32 products this gives at least 200 products.
        Map<String, String[]> types = new LinkedHashMap<>();
        types.put("women", new String[]{"Kurti", "Saree", "Kurta Set", "Maxi Dress", "Top", "Palazzo", "Handbag"});
        types.put("men", new String[]{"Casual Shirt", "T-Shirt", "Jeans", "Kurta", "Cargo Pant", "Hoodie", "Wallet"});
        types.put("kids", new String[]{"T-Shirt Set", "Frock", "Hoodie", "Dungaree", "Party Dress", "Shorts Set", "Ethnic Set"});
        types.put("home", new String[]{"Bedsheet", "Cushion Cover", "Coffee Mug", "Storage Basket", "Wall Decor", "Kitchen Pan", "Table Runner"});
        types.put("beauty", new String[]{"Face Serum", "Face Wash", "Lipstick", "Makeup Kit", "Moisturizer", "Hair Care Combo", "Beauty Tool"});
        types.put("footwear", new String[]{"Sneakers", "Sandals", "Loafers", "Slippers", "Heels", "Sports Shoes", "Casual Shoes"});
        types.put("jewellery", new String[]{"Jhumka Earrings", "Necklace", "Bangle Set", "Bracelet", "Ring", "Jewellery Set", "Hair Accessories"});
        types.put("electronics", new String[]{"Bluetooth Earbuds", "Smart Band", "Mini Speaker", "Wireless Keyboard", "Power Bank", "Desk Lamp", "Phone Stand"});

        String[] descriptors = {"Classic", "Printed", "Premium"};
        for (String cat : CATS) {
            String slug = cat.toLowerCase();
            String[] productTypes = types.get(slug);
            for (int d = 0; d < descriptors.length; d++) {
                for (int t = 0; t < productTypes.length; t++) {
                    String name = descriptors[d] + " " + productTypes[t] + " - " + cat + " Edit " + (d * productTypes.length + t + 1);
                    double base = basePrice(slug, t);
                    double price = Math.round((base + d * base * 0.13) / 10.0) * 10.0;
                    double mrp = Math.round((price * (1.65 + (t % 3) * 0.15)) / 10.0) * 10.0;
                    double rating = 4.0 + ((t + d) % 10) / 10.0;
                    insertProductIfMissing(categoryIds, sellerIds, name, slug, price, mrp, rating, variantsFor(slug));
                }
            }
        }
    }

    private void insertProductIfMissing(Map<String, Long> categoryIds, java.util.List<Long> sellerIds,
                                        String name, String slug, double price, double mrp,
                                        double rating, String sizes) {
        Long categoryId = categoryIds.get(slug);
        Integer exists = jdbc.queryForObject("select count(*) from products where name = ?", Integer.class, name);
        int shopIndex = Math.floorMod(name.hashCode(), sellerIds.size());
        Long sellerId = sellerIds.get(shopIndex);
        if (exists != null && exists > 0) {
            jdbc.update("update products set seller_id=?, category_id=?, status='APPROVED' where name=?", sellerId, categoryId, name);
            Long pid = jdbc.queryForObject("select id from products where name=? order by id desc limit 1", Long.class, name);
            ensureVariants(pid, sizes);
            return;
        }
        String shop = jdbc.queryForObject("select business_name from sellers where id=?", String.class, sellerId);
        jdbc.update("insert into products (seller_id, category_id, name, description, price, mrp, status, rating_avg, rating_count) values (?, ?, ?, ?, ?, ?, 'APPROVED', ?, ?)",
                sellerId, categoryId, name,
                "Everyday value pick from " + shop + ". Carefully selected for TideMart shoppers.",
                price, mrp, rating, 45 + Math.floorMod(name.hashCode(), 180));
        Long pid = jdbc.queryForObject("select id from products where name=? order by id desc limit 1", Long.class, name);
        ensureVariants(pid, sizes);
    }

    private void ensureVariants(Long productId, String sizes) {
        Integer count = jdbc.queryForObject("select count(*) from product_variants where product_id=?", Integer.class, productId);
        if (count != null && count > 0) return;
        for (String size : sizes.split(",")) {
            jdbc.update("insert into product_variants (product_id, size, stock) values (?, ?, ?)", productId, size, 30);
        }
    }

    private double basePrice(String slug, int typeIndex) {
        double[] bases = switch (slug) {
            case "women" -> new double[]{399, 549, 699, 799, 299, 449, 599};
            case "men" -> new double[]{449, 299, 599, 499, 549, 699, 249};
            case "kids" -> new double[]{299, 399, 449, 499, 699, 349, 549};
            case "home" -> new double[]{499, 299, 249, 349, 399, 699, 299};
            case "beauty" -> new double[]{279, 199, 249, 399, 299, 349, 229};
            case "footwear" -> new double[]{699, 549, 649, 249, 899, 999, 599};
            case "jewellery" -> new double[]{299, 399, 249, 349, 199, 699, 179};
            default -> new double[]{799, 699, 599, 899, 749, 499, 299};
        };
        return bases[typeIndex];
    }

    private String variantsFor(String slug) {
        return switch (slug) {
            case "women" -> "S,M,L,XL";
            case "men" -> "S,M,L,XL,XXL";
            case "kids" -> "2-3Y,4-5Y,6-7Y,8-9Y";
            case "footwear" -> "6,7,8,9,10";
            case "jewellery", "beauty", "home", "electronics" -> "Free";
            default -> "Free";
        };
    }

}
