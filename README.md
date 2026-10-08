# Tidemart

Meesho-style marketplace. Spring Boot 3 (Java 17) + MySQL (tidemart_db) + React (Vite, Tailwind).

## Run
1. Database: `mysql -u root -p < database/tidemart_db_schema.sql` then `mysql -u root -p < database/tidemart_db_seed_data.sql`
2. Backend: set DB_USER and DB_PASSWORD (see backend/.env.example), then `cd backend && mvn spring-boot:run` (http://localhost:8080)
3. Frontend: `cd frontend && npm install && npm run dev` (http://localhost:5173)
4. UI preview with demo data: open `preview/tidemart-app.html` in a browser.

## Status
| Phase | Scope | State |
|---|---|---|
| 0 | Folder structure, database schema, configs, UI preview | Done |
| 1 | Login (OTP demo and email), JWT, roles, profile, addresses | Code written, not yet run |
| 2 | Categories, products, search, home | Core written, not yet run (more Phase 2 extras pending) |
| 3 | Cart, wishlist, coupons, checkout, orders, delivery, wallet | Cart, coupons, pincode, checkout, orders, tracking wishlist, returns, refunds to wallet written, not yet run (GST invoice pending) |
| 4 | Seller panel, finance | Sign-up, products, stock, orders, labels, earnings written, not yet run (more pending) |
| 5 | Reseller, referrals | Sign-up, catalogue and margins, WhatsApp share, orders for customers, earnings, referrals written, not yet run (more pending) |
| 6 | Admin panel | Dashboard, users, seller and product approval, orders, returns, categories, coupons, commission, shipping, tax, payouts, banners, policies, delivery partners written, not yet run |
| 7 | Reviews, notifications, support and chat, polish | Reviews, in-app alerts, FAQ, tickets, demo chat, languages, voice search written, not yet run (image search and several small items pending) |

Files marked `TODO: Phase N` are empty stubs for that phase. Demo OTP mode prints the OTP in the console (no SMS provider).

## Try Phase 1 (Postman)
- POST /api/auth/send-otp  {"phone":"9876543210"} returns the demo OTP, then POST /api/auth/verify-otp {"phone","code"}
- POST /api/auth/register and /api/auth/login (email and password)
- Admin account created on first start: admin@tidemart.com / Admin@123
- Send the returned token as `Authorization: Bearer <token>` to GET /api/users/me and /api/addresses

## Seller panel (Phase 4)
Any logged-in user can open Sell on Tidemart. With `tidemart.auto-approve: true` (default) the account is approved at once and products go live; log out and in again to get the seller role. Or log in as seller@tidemart.com.
Sellers can mark orders Packed and Shipped. Delivery (Phase 3 demo button) finishes the order, then earnings appear (default commission 10%).

## Reseller (Phase 5)
Any logged-in customer can open Become a reseller (role changes at once; log out and in again). Customers pay supplier price plus your margin (default Rs 50). Margin is earned when the order is delivered; sellers earn on the supplier price only. Referral codes: each user has one; applying a code credits Rs 50 to both wallets.

## Admin panel (Phase 6)
Log in as admin@tidemart.com / Admin@123 and open Admin in the top bar. Each tab lists a table; use the buttons on a row, or the form above the table to add one. Setting a return to Refunded credits the customer's wallet. With auto-approve on (default) sellers and products are approved at once, so the approval tabs matter when you set `tidemart.auto-approve: false`.

## Phase 8 (leftover features)
Banners from the admin panel on the home page, recently viewed, search history and trending searches, product questions and answers, reorder, and a printable invoice.

## Delivery run (Phase 9)
Test flow: customer orders, seller marks Packed and Shipped, then in Admin open Delivery run and press Assign partner (use ID 1, Demo Rider). The customer gets the delivery OTP as an alert. Press Deliver with OTP and enter it. Cash on delivery is then recorded under COD cash, where you can Mark settled. Failed attempt records a try; 3 failures cancel the order and restore stock. Return pickups can be scheduled in their own tab. Customers can tap Notify me on a sold-out size and get an alert when the seller restocks.

## Phase 10
Image upload for sellers (saved in the backend `uploads/` folder), wallet payment at checkout (wallet is also refunded when a paid order is cancelled), new-order alerts for sellers, delivery check and seller name on the product page, a seller price-edit screen, policy pages on the site, and login rate limiting (30 requests per minute per address).

## Phase 11
Dark mode, save for later in the cart, seller bulk upload (CSV), seller analytics, seller reject order, reseller poster download, reseller level and leaderboard, admin sales analytics, fraud flags, audit log, and admin replies to tickets.

If you created the database before this phase, add the audit table once:
`CREATE TABLE audit_log (id BIGINT AUTO_INCREMENT PRIMARY KEY, admin_id BIGINT NULL, action VARCHAR(60), detail VARCHAR(255), created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);`

## Phase 12
Size chart (seller adds, customers open it on the product page), product video and extra photos (upload up to 10 MB mp4 or webm), flash sales (Admin, Flash sales tab: product ID, sale price, start and end like `2026-12-31 18:00:00`; the sale price shows on cards, the product page and the cart), and promoted listings (sellers press Promote; promoted products are listed first in search with a Sponsored label, free in this demo). Reseller orders still use the normal price during a flash sale.

If your database already exists, add the promotions table once:
`CREATE TABLE product_promotions (id BIGINT AUTO_INCREMENT PRIMARY KEY, product_id BIGINT NOT NULL, seller_id BIGINT NOT NULL, ends_at DATETIME NOT NULL, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);`

## Phase 13
New top bar: logo, location picker (saved addresses, add a new one, or just check a pincode; the chosen address is used at checkout), search with a microphone icon, heart, cart and bell icons with counts, and an account menu (orders, returns, wallet, refer, help, profile, sell and earn, language, dark mode, log out). Demo products now have drawn pictures in `frontend/public/products/` (simple illustrations, not photos); sellers can upload real photos. Existing demo products get their pictures the next time the backend starts.

## Phase 14 (Meesho-style layout)
Top bar like Meesho: logo, wide search with a magnifier and microphone, then Sell on Tidemart, Profile and Cart with icons above labels. A category row sits under it. The home page has a banner, a perks strip (7 Days Easy Return, Cash on Delivery, Lowest Prices) and arch-shaped category tiles with pictures. Wishlist and Alerts moved into the Profile menu.

## Phase 2 demo data
On first start the backend adds 6 categories and 12 demo products. Demo seller login: seller@tidemart.com / Seller@123
Browse: GET /api/home, /api/products?q=kurti&sort=price_asc, /api/products/1

## UI Refresh (October 2026)

The customer storefront was refreshed with a cleaner marketplace layout inspired by modern Indian value-commerce UX patterns, while keeping TideMart's own branding and existing business features.

### Updated
- Simplified customer navbar with search, account and cart as primary actions.
- Seller/reseller/admin entry points moved into the Account menu.
- Cleaner category navigation and responsive mobile behavior.
- Added local original SVG hero banners under `frontend/public/banners/`.
- Added local category artwork under `frontend/public/categories/`.
- Implemented `BannerCarousel`, `CategoryStrip`, and a complete customer `Footer`.
- Refreshed homepage sections and product cards.
- Kept existing backend APIs, routes and seller/reseller/admin pages intact.

### Frontend build
Run `npm install` inside `frontend/`, then `npm run dev` or `npm run build`.
