# TideMart Functional + UI Update

## Implemented
- Preserved the existing catalogue and added seeded catalogue generation to 200+ products across 8 categories.
- Added multiple real photographic Unsplash image URLs per product category and product-detail gallery seeding.
- Kept different demo sellers/shops and seller labels on product cards/cart.
- Product grid now supports responsive 5-column desktop layout, skeleton loading and useful empty states.
- Home page now has resilient loading/error behavior, Products for You, Popular Stores, budget shopping shortcuts and category product sections.
- Cart upgraded to marketplace-style layout with images, seller, size, MRP, discount, quantity controls, save-for-later, coupon, price details, retry/error state and empty state.
- Search page now loads 20 items per page, shows loading skeletons, useful empty messages and API error state.
- Product cards include image fallback behavior if a remote image fails.
- Navbar has a visible Sell on TideMart CTA and the mobile menu button now opens the account/menu panel.
- Seller signup remains a first-class flow and the backend supports seller + reseller roles on the same account.

## Important runtime note
The source was inspected and updated, but a complete Spring Boot + MySQL runtime test could not be executed in this environment because Maven is not installed. Frontend dependency installation also timed out in the sandbox, so a production Vite build could not be completed here.

After extraction, start MySQL and the Spring Boot backend once so DataInitializer/ImageSeeder can populate the catalogue and images.
