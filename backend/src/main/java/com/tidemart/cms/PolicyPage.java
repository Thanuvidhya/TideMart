package com.tidemart.cms;

import jakarta.persistence.*;

@Entity
@Table(name = "policy_pages")
public class PolicyPage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String slug, title, content;
}
