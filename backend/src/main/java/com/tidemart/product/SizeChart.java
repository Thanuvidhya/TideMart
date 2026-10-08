package com.tidemart.product;

import jakarta.persistence.*;

@Entity
@Table(name = "size_charts")
public class SizeChart {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long productId;
    public String chartJson;
}
