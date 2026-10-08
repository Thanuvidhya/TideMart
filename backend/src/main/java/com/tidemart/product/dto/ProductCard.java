package com.tidemart.product.dto;
public record ProductCard(Long id, String name, double price, double mrp, int discountPercent, double ratingAvg, int ratingCount, int stock, String imageUrl, boolean promoted, String sellerName, double sellerRating) {}
