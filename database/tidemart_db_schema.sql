-- Tidemart database (MySQL 8). Run: mysql -u root -p < tidemart_db_schema.sql
CREATE DATABASE IF NOT EXISTS tidemart_db CHARACTER SET utf8mb4;
USE tidemart_db;

CREATE TABLE `users` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `phone` VARCHAR(15),
  `email` VARCHAR(120),
  `password_hash` VARCHAR(100),
  `name` VARCHAR(80),
  `photo_url` VARCHAR(255),
  `role` ENUM('CUSTOMER','RESELLER','SELLER','ADMIN') NOT NULL DEFAULT 'CUSTOMER',
  `status` ENUM('ACTIVE','BLOCKED','DELETED') NOT NULL DEFAULT 'ACTIVE',
  `language` VARCHAR(5),
  `referral_code` VARCHAR(12),
  `referred_by` BIGINT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (`phone`),
  UNIQUE (`email`),
  UNIQUE (`referral_code`),
  FOREIGN KEY (`referred_by`) REFERENCES `users`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `otp_tokens` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `phone` VARCHAR(15),
  `code` VARCHAR(6),
  `expires_at` DATETIME NULL,
  `used` BOOLEAN DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `refresh_tokens` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `token` VARCHAR(255),
  `expires_at` DATETIME NULL,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `addresses` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `name` VARCHAR(80),
  `phone` VARCHAR(15),
  `line1` VARCHAR(200),
  `city` VARCHAR(60),
  `state` VARCHAR(60),
  `pincode` VARCHAR(6),
  `is_default` BOOLEAN DEFAULT FALSE,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `categories` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `parent_id` BIGINT NULL,
  `name` VARCHAR(80),
  `slug` VARCHAR(100),
  `image_url` VARCHAR(255),
  `active` BOOLEAN DEFAULT TRUE,
  FOREIGN KEY (`parent_id`) REFERENCES `categories`(`id`),
  UNIQUE (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `category_attributes` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `category_id` BIGINT NOT NULL,
  `name` VARCHAR(60),
  FOREIGN KEY (`category_id`) REFERENCES `categories`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `sellers` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `business_name` VARCHAR(120),
  `gst_no` VARCHAR(20),
  `pan_no` VARCHAR(12),
  `bank_account` VARCHAR(30),
  `ifsc` VARCHAR(15),
  `status` ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
  `rating` DECIMAL(10,2) DEFAULT 0,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `pickup_addresses` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `seller_id` BIGINT NOT NULL,
  `line1` VARCHAR(200),
  `city` VARCHAR(60),
  `state` VARCHAR(60),
  `pincode` VARCHAR(6),
  FOREIGN KEY (`seller_id`) REFERENCES `sellers`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `resellers` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `upi_id` VARCHAR(60),
  `bank_account` VARCHAR(30),
  `ifsc` VARCHAR(15),
  `level` INT DEFAULT 0,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
  UNIQUE (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `products` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `seller_id` BIGINT NOT NULL,
  `category_id` BIGINT NOT NULL,
  `name` VARCHAR(200),
  `description` TEXT,
  `price` DECIMAL(10,2) DEFAULT 0,
  `mrp` DECIMAL(10,2) DEFAULT 0,
  `status` ENUM('PENDING','APPROVED','REJECTED','HIDDEN') NOT NULL DEFAULT 'PENDING',
  `rating_avg` DECIMAL(10,2) DEFAULT 0,
  `rating_count` INT DEFAULT 0,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`seller_id`) REFERENCES `sellers`(`id`),
  FOREIGN KEY (`category_id`) REFERENCES `categories`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `product_variants` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `product_id` BIGINT NOT NULL,
  `size` VARCHAR(20),
  `color` VARCHAR(30),
  `stock` INT DEFAULT 0,
  `sku` VARCHAR(40),
  FOREIGN KEY (`product_id`) REFERENCES `products`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `product_media` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `product_id` BIGINT NOT NULL,
  `url` VARCHAR(255),
  `type` ENUM('IMAGE','VIDEO') NOT NULL DEFAULT 'IMAGE',
  `sort_order` INT DEFAULT 0,
  FOREIGN KEY (`product_id`) REFERENCES `products`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `size_charts` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `product_id` BIGINT NOT NULL,
  `chart_json` TEXT,
  FOREIGN KEY (`product_id`) REFERENCES `products`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `stock_alerts` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `product_id` BIGINT NOT NULL,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
  FOREIGN KEY (`product_id`) REFERENCES `products`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `recently_viewed` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `product_id` BIGINT NOT NULL,
  `viewed_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
  FOREIGN KEY (`product_id`) REFERENCES `products`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `product_questions` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `product_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `question` TEXT,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`product_id`) REFERENCES `products`(`id`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `product_answers` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `question_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `answer` TEXT,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`question_id`) REFERENCES `product_questions`(`id`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `search_history` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `term` VARCHAR(100),
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `banners` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `image_url` VARCHAR(255),
  `title` VARCHAR(120),
  `link` VARCHAR(255),
  `sort_order` INT DEFAULT 0,
  `active` BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `home_sections` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(120),
  `type` VARCHAR(30),
  `config_json` TEXT,
  `sort_order` INT DEFAULT 0,
  `active` BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `flash_sales` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `product_id` BIGINT NOT NULL,
  `sale_price` DECIMAL(10,2) DEFAULT 0,
  `starts_at` DATETIME NULL,
  `ends_at` DATETIME NULL,
  FOREIGN KEY (`product_id`) REFERENCES `products`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `price_stores` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(60),
  `max_price` DECIMAL(10,2) DEFAULT 0,
  `active` BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `carts` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
  UNIQUE (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `cart_items` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `cart_id` BIGINT NOT NULL,
  `product_id` BIGINT NOT NULL,
  `variant_id` BIGINT NULL,
  `qty` INT DEFAULT 0,
  `saved_for_later` BOOLEAN DEFAULT FALSE,
  FOREIGN KEY (`cart_id`) REFERENCES `carts`(`id`),
  FOREIGN KEY (`product_id`) REFERENCES `products`(`id`),
  FOREIGN KEY (`variant_id`) REFERENCES `product_variants`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `wishlist_items` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `product_id` BIGINT NOT NULL,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
  FOREIGN KEY (`product_id`) REFERENCES `products`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `coupons` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `code` VARCHAR(30),
  `type` ENUM('PERCENT','FLAT') NOT NULL DEFAULT 'PERCENT',
  `value` DECIMAL(10,2) DEFAULT 0,
  `max_discount` DECIMAL(10,2) DEFAULT 0,
  `min_order` DECIMAL(10,2) DEFAULT 0,
  `valid_from` DATETIME NULL,
  `valid_till` DATETIME NULL,
  `usage_limit` INT DEFAULT 0,
  `active` BOOLEAN DEFAULT TRUE,
  UNIQUE (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `orders` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `order_no` VARCHAR(20),
  `user_id` BIGINT NOT NULL,
  `reseller_id` BIGINT NULL,
  `ship_name` VARCHAR(80),
  `ship_phone` VARCHAR(15),
  `ship_line1` VARCHAR(200),
  `ship_city` VARCHAR(60),
  `ship_state` VARCHAR(60),
  `ship_pincode` VARCHAR(6),
  `subtotal` DECIMAL(10,2) DEFAULT 0,
  `discount` DECIMAL(10,2) DEFAULT 0,
  `delivery_fee` DECIMAL(10,2) DEFAULT 0,
  `cod_fee` DECIMAL(10,2) DEFAULT 0,
  `total` DECIMAL(10,2) DEFAULT 0,
  `payment_method` ENUM('UPI','CARD','COD','WALLET') NOT NULL DEFAULT 'UPI',
  `payment_status` ENUM('PENDING','SUCCESS','FAILED','REFUNDED') NOT NULL DEFAULT 'PENDING',
  `status` ENUM('PLACED','PACKED','SHIPPED','OUT_FOR_DELIVERY','DELIVERED','CANCELLED') NOT NULL DEFAULT 'PLACED',
  `coupon_id` BIGINT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (`order_no`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
  FOREIGN KEY (`reseller_id`) REFERENCES `resellers`(`id`),
  FOREIGN KEY (`coupon_id`) REFERENCES `coupons`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `order_items` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `order_id` BIGINT NOT NULL,
  `product_id` BIGINT NOT NULL,
  `variant_id` BIGINT NULL,
  `seller_id` BIGINT NOT NULL,
  `qty` INT DEFAULT 0,
  `unit_price` DECIMAL(10,2) DEFAULT 0,
  `reseller_margin` DECIMAL(10,2) DEFAULT 0,
  `commission` DECIMAL(10,2) DEFAULT 0,
  FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`),
  FOREIGN KEY (`product_id`) REFERENCES `products`(`id`),
  FOREIGN KEY (`variant_id`) REFERENCES `product_variants`(`id`),
  FOREIGN KEY (`seller_id`) REFERENCES `sellers`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `order_status_history` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `order_id` BIGINT NOT NULL,
  `status` VARCHAR(30),
  `note` VARCHAR(200),
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `coupon_usage` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `coupon_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `order_id` BIGINT NOT NULL,
  FOREIGN KEY (`coupon_id`) REFERENCES `coupons`(`id`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
  FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `return_requests` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `order_id` BIGINT NOT NULL,
  `order_item_id` BIGINT NOT NULL,
  `type` ENUM('RETURN','EXCHANGE') NOT NULL DEFAULT 'RETURN',
  `reason` VARCHAR(120),
  `photo_urls` TEXT,
  `status` ENUM('REQUESTED','APPROVED','PICKED_UP','REFUNDED','REJECTED') NOT NULL DEFAULT 'REQUESTED',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`),
  FOREIGN KEY (`order_item_id`) REFERENCES `order_items`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `refunds` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `order_id` BIGINT NOT NULL,
  `return_id` BIGINT NULL,
  `amount` DECIMAL(10,2) DEFAULT 0,
  `method` VARCHAR(30),
  `status` VARCHAR(20),
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`),
  FOREIGN KEY (`return_id`) REFERENCES `return_requests`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `payments` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `order_id` BIGINT NOT NULL,
  `method` VARCHAR(10),
  `amount` DECIMAL(10,2) DEFAULT 0,
  `status` VARCHAR(20),
  `txn_ref` VARCHAR(60),
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `wallets` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `balance` DECIMAL(10,2) DEFAULT 0,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
  UNIQUE (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `wallet_transactions` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `wallet_id` BIGINT NOT NULL,
  `type` ENUM('CREDIT','DEBIT') NOT NULL DEFAULT 'CREDIT',
  `amount` DECIMAL(10,2) DEFAULT 0,
  `reason` VARCHAR(120),
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`wallet_id`) REFERENCES `wallets`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `reviews` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `product_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `order_id` BIGINT NULL,
  `rating` INT DEFAULT 0,
  `comment` TEXT,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`product_id`) REFERENCES `products`(`id`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
  FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `review_images` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `review_id` BIGINT NOT NULL,
  `url` VARCHAR(255),
  FOREIGN KEY (`review_id`) REFERENCES `reviews`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `referrals` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `referrer_id` BIGINT NOT NULL,
  `referred_id` BIGINT NOT NULL,
  `reward` DECIMAL(10,2) DEFAULT 0,
  `status` VARCHAR(20),
  FOREIGN KEY (`referrer_id`) REFERENCES `users`(`id`),
  FOREIGN KEY (`referred_id`) REFERENCES `users`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `notifications` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `title` VARCHAR(120),
  `body` TEXT,
  `type` VARCHAR(30),
  `is_read` BOOLEAN DEFAULT FALSE,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `support_tickets` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `order_id` BIGINT NULL,
  `subject` VARCHAR(200),
  `status` ENUM('OPEN','IN_PROGRESS','CLOSED') NOT NULL DEFAULT 'OPEN',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
  FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `ticket_messages` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `ticket_id` BIGINT NOT NULL,
  `sender_id` BIGINT NOT NULL,
  `message` TEXT,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`ticket_id`) REFERENCES `support_tickets`(`id`),
  FOREIGN KEY (`sender_id`) REFERENCES `users`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `faqs` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `question` VARCHAR(250),
  `answer` TEXT,
  `sort_order` INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `chat_messages` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `sender_role` VARCHAR(20),
  `message` TEXT,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `reseller_products` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `reseller_id` BIGINT NOT NULL,
  `product_id` BIGINT NOT NULL,
  `margin` DECIMAL(10,2) DEFAULT 0,
  FOREIGN KEY (`reseller_id`) REFERENCES `resellers`(`id`),
  FOREIGN KEY (`product_id`) REFERENCES `products`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `reseller_customers` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `reseller_id` BIGINT NOT NULL,
  `name` VARCHAR(80),
  `phone` VARCHAR(15),
  `address` VARCHAR(250),
  FOREIGN KEY (`reseller_id`) REFERENCES `resellers`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `reseller_earnings` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `reseller_id` BIGINT NOT NULL,
  `order_item_id` BIGINT NOT NULL,
  `amount` DECIMAL(10,2) DEFAULT 0,
  `status` ENUM('PENDING','PAID') NOT NULL DEFAULT 'PENDING',
  FOREIGN KEY (`reseller_id`) REFERENCES `resellers`(`id`),
  FOREIGN KEY (`order_item_id`) REFERENCES `order_items`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `poster_templates` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(60),
  `config_json` TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `commission_rules` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `category_id` BIGINT NOT NULL,
  `percent` DECIMAL(10,2) DEFAULT 0,
  FOREIGN KEY (`category_id`) REFERENCES `categories`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `ledger_entries` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `order_item_id` BIGINT NOT NULL,
  `party_type` ENUM('SELLER','RESELLER','PLATFORM') NOT NULL DEFAULT 'SELLER',
  `party_id` INT DEFAULT 0,
  `amount` DECIMAL(10,2) DEFAULT 0,
  `type` ENUM('CREDIT','DEBIT') NOT NULL DEFAULT 'CREDIT',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`order_item_id`) REFERENCES `order_items`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `payouts` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `party_type` ENUM('SELLER','RESELLER') NOT NULL DEFAULT 'SELLER',
  `party_id` INT DEFAULT 0,
  `amount` DECIMAL(10,2) DEFAULT 0,
  `method` VARCHAR(20),
  `status` ENUM('PENDING','PAID') NOT NULL DEFAULT 'PENDING',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `tax_settings` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(40),
  `percent` DECIMAL(10,2) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `serviceable_pincodes` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `pincode` VARCHAR(6),
  `city` VARCHAR(60),
  `state` VARCHAR(60),
  `cod_available` BOOLEAN DEFAULT TRUE,
  UNIQUE (`pincode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `shipping_rules` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `min_order` DECIMAL(10,2) DEFAULT 0,
  `max_order` DECIMAL(10,2) DEFAULT 0,
  `fee` DECIMAL(10,2) DEFAULT 0,
  `active` BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `delivery_partners` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(80),
  `phone` VARCHAR(15),
  `status` VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `delivery_attempts` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `order_id` BIGINT NOT NULL,
  `partner_id` BIGINT NULL,
  `status` VARCHAR(30),
  `note` VARCHAR(200),
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`),
  FOREIGN KEY (`partner_id`) REFERENCES `delivery_partners`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `cod_collections` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `order_id` BIGINT NOT NULL,
  `partner_id` BIGINT NULL,
  `amount` DECIMAL(10,2) DEFAULT 0,
  `status` ENUM('PENDING','COLLECTED','SETTLED') NOT NULL DEFAULT 'PENDING',
  `collected_at` DATETIME NULL,
  `settled_at` DATETIME NULL,
  FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`),
  FOREIGN KEY (`partner_id`) REFERENCES `delivery_partners`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `return_pickups` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `return_id` BIGINT NOT NULL,
  `partner_id` BIGINT NULL,
  `scheduled_on` DATETIME NULL,
  `status` VARCHAR(20),
  FOREIGN KEY (`return_id`) REFERENCES `return_requests`(`id`),
  FOREIGN KEY (`partner_id`) REFERENCES `delivery_partners`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `disputes` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `order_item_id` BIGINT NOT NULL,
  `seller_id` BIGINT NOT NULL,
  `reason` VARCHAR(250),
  `status` VARCHAR(20),
  FOREIGN KEY (`order_item_id`) REFERENCES `order_items`(`id`),
  FOREIGN KEY (`seller_id`) REFERENCES `sellers`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `policy_pages` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `slug` VARCHAR(60),
  `title` VARCHAR(120),
  `content` TEXT,
  UNIQUE (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE `audit_log` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `admin_id` BIGINT NULL,
  `action` VARCHAR(60),
  `detail` VARCHAR(255),
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE `product_promotions` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `product_id` BIGINT NOT NULL,
  `seller_id` BIGINT NOT NULL,
  `ends_at` DATETIME NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
