package com.tidemart.product.dto;

import com.tidemart.product.Product;
import com.tidemart.product.ProductMedia;
import com.tidemart.product.ProductVariant;
import java.util.List;

public record ProductDetail(Product product, String categoryName, List<ProductVariant> variants, List<ProductMedia> media, List<ProductCard> similar, String sellerName, double sellerRating, Double salePrice, String saleEnds) {}
