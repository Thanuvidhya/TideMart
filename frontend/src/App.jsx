import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext.jsx';
import ProtectedRoute from './routes/ProtectedRoute.jsx';
import LoginPage from './pages/auth/LoginPage.jsx';
import OtpVerifyPage from './pages/auth/OtpVerifyPage.jsx';
import HomePage from './pages/customer/HomePage.jsx';
import SearchResultsPage from './pages/customer/SearchResultsPage.jsx';
import ProductDetailPage from './pages/customer/ProductDetailPage.jsx';
import ProfilePage from './pages/customer/ProfilePage.jsx';
import CartPage from './pages/customer/CartPage.jsx';
import CheckoutPage from './pages/customer/CheckoutPage.jsx';
import OrdersPage from './pages/customer/OrdersPage.jsx';
import OrderDetailPage from './pages/customer/OrderDetailPage.jsx';
import WishlistPage from './pages/customer/WishlistPage.jsx';
import WalletPage from './pages/customer/WalletPage.jsx';
import ReturnsPage from './pages/customer/ReturnsPage.jsx';
import SellerSignupPage from './pages/seller/SellerSignupPage.jsx';
import SellerDashboardPage from './pages/seller/SellerDashboardPage.jsx';
import SellerProductsPage from './pages/seller/SellerProductsPage.jsx';
import SellerAddProductPage from './pages/seller/SellerAddProductPage.jsx';
import SellerOrdersPage from './pages/seller/SellerOrdersPage.jsx';
import SellerPickupAddressPage from './pages/seller/SellerPickupAddressPage.jsx';
import ResellerSignupPage from './pages/reseller/ResellerSignupPage.jsx';
import ResellerCatalogPage from './pages/reseller/ResellerCatalogPage.jsx';
import ResellerPlaceOrderPage from './pages/reseller/ResellerPlaceOrderPage.jsx';
import ResellerOrdersPage from './pages/reseller/ResellerOrdersPage.jsx';
import ResellerCustomersPage from './pages/reseller/ResellerCustomersPage.jsx';
import ResellerEarningsPage from './pages/reseller/ResellerEarningsPage.jsx';
import ReferralPage from './pages/customer/ReferralPage.jsx';
import AdminDashboardPage from './pages/admin/AdminDashboardPage.jsx';
import HelpCentrePage from './pages/customer/HelpCentrePage.jsx';
import NotificationsPage from './pages/customer/NotificationsPage.jsx';
import InvoicePage from './pages/customer/InvoicePage.jsx';
import PolicyPage from './pages/customer/PolicyPage.jsx';
import SellerBulkUploadPage from './pages/seller/SellerBulkUploadPage.jsx';
import SellerAnalyticsPage from './pages/seller/SellerAnalyticsPage.jsx';
import ResellerLeaderboardPage from './pages/reseller/ResellerLeaderboardPage.jsx';
import AddressesPage from './pages/customer/AddressesPage.jsx';
import CategoryPage from './pages/customer/CategoryPage.jsx';
import SupportTicketsPage from './pages/customer/SupportTicketsPage.jsx';
import NotFoundPage from './pages/customer/NotFoundPage.jsx';
import SellerEarningsPage from './pages/seller/SellerEarningsPage.jsx';
import SellerHelpPage from './pages/seller/SellerHelpPage.jsx';
import SellerReturnsPage from './pages/seller/SellerReturnsPage.jsx';
import ResellerWalletPage from './pages/reseller/ResellerWalletPage.jsx';
import ResellerTeamPage from './pages/reseller/ResellerTeamPage.jsx';
import ResellerProductPage from './pages/reseller/ResellerProductPage.jsx';
import ResellerShareLibraryPage from './pages/reseller/ResellerShareLibraryPage.jsx';
import AdminUsersPage from './pages/admin/AdminUsersPage.jsx';
import AdminSellersPage from './pages/admin/AdminSellersPage.jsx';
import AdminProductsPage from './pages/admin/AdminProductsPage.jsx';
import AdminOrdersPage from './pages/admin/AdminOrdersPage.jsx';
import AdminReturnsPage from './pages/admin/AdminReturnsPage.jsx';
import AdminCategoriesPage from './pages/admin/AdminCategoriesPage.jsx';
import AdminCouponsPage from './pages/admin/AdminCouponsPage.jsx';
import AdminCommissionPage from './pages/admin/AdminCommissionPage.jsx';
import AdminShippingPage from './pages/admin/AdminShippingPage.jsx';
import AdminTaxPage from './pages/admin/AdminTaxPage.jsx';
import AdminPayoutsPage from './pages/admin/AdminPayoutsPage.jsx';
import AdminBannersPage from './pages/admin/AdminBannersPage.jsx';
import AdminPoliciesPage from './pages/admin/AdminPoliciesPage.jsx';
import AdminDeliveryPartnersPage from './pages/admin/AdminDeliveryPartnersPage.jsx';
import AdminReviewsPage from './pages/admin/AdminReviewsPage.jsx';
import AdminLoginPage from './pages/admin/AdminLoginPage.jsx';
import DeliveryLoginPage from './pages/delivery/DeliveryLoginPage.jsx';
import DeliveryDashboardPage from './pages/delivery/DeliveryDashboardPage.jsx';
import RoleRoute from './routes/RoleRoute.jsx';



const guard = (el) => <ProtectedRoute>{el}</ProtectedRoute>;

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/verify" element={<OtpVerifyPage />} />
          <Route path="/" element={<HomePage />} />
          <Route path="/search" element={<SearchResultsPage />} />
          <Route path="/category" element={<CategoryPage />} />
          <Route path="/product/:id" element={<ProductDetailPage />} />
          <Route path="/cart" element={guard(<CartPage />)} />
          <Route path="/checkout" element={guard(<CheckoutPage />)} />
          <Route path="/orders" element={guard(<OrdersPage />)} />
          <Route path="/orders/:id/invoice" element={guard(<InvoicePage />)} />
          <Route path="/orders/:id" element={guard(<OrderDetailPage />)} />
          <Route path="/wishlist" element={guard(<WishlistPage />)} />
          <Route path="/wallet" element={guard(<WalletPage />)} />
          <Route path="/returns" element={guard(<ReturnsPage />)} />
          <Route path="/seller/signup" element={guard(<SellerSignupPage />)} />
          <Route path="/seller" element={guard(<SellerDashboardPage />)} />
          <Route path="/seller/products" element={guard(<SellerProductsPage />)} />
          <Route path="/seller/add-product" element={guard(<SellerAddProductPage />)} />
          <Route path="/seller/orders" element={guard(<SellerOrdersPage />)} />
          <Route path="/seller/pickup" element={guard(<SellerPickupAddressPage />)} />
          <Route path="/reseller/signup" element={guard(<ResellerSignupPage />)} />
          <Route path="/reseller" element={guard(<ResellerCatalogPage />)} />
          <Route path="/reseller/place-order" element={guard(<ResellerPlaceOrderPage />)} />
          <Route path="/reseller/orders" element={guard(<ResellerOrdersPage />)} />
          <Route path="/reseller/customers" element={guard(<ResellerCustomersPage />)} />
          <Route path="/reseller/earnings" element={guard(<ResellerEarningsPage />)} />
          <Route path="/referral" element={guard(<ReferralPage />)} />
          <Route path="/admin" element={<RoleRoute roles={['ADMIN']}><AdminDashboardPage /></RoleRoute>} />
          <Route path="/admin/login" element={<AdminLoginPage />} />
          <Route path="/delivery/login" element={<DeliveryLoginPage />} />
          <Route path="/delivery" element={<RoleRoute roles={['DELIVERY']}><DeliveryDashboardPage /></RoleRoute>} />
          <Route path="/admin/users" element={<RoleRoute roles={['ADMIN']}><AdminUsersPage /></RoleRoute>} />
          <Route path="/admin/sellers" element={<RoleRoute roles={['ADMIN']}><AdminSellersPage /></RoleRoute>} />
          <Route path="/admin/products" element={<RoleRoute roles={['ADMIN']}><AdminProductsPage /></RoleRoute>} />
          <Route path="/admin/orders" element={<RoleRoute roles={['ADMIN']}><AdminOrdersPage /></RoleRoute>} />
          <Route path="/admin/returns" element={<RoleRoute roles={['ADMIN']}><AdminReturnsPage /></RoleRoute>} />
          <Route path="/admin/categories" element={<RoleRoute roles={['ADMIN']}><AdminCategoriesPage /></RoleRoute>} />
          <Route path="/admin/coupons" element={<RoleRoute roles={['ADMIN']}><AdminCouponsPage /></RoleRoute>} />
          <Route path="/admin/commission" element={<RoleRoute roles={['ADMIN']}><AdminCommissionPage /></RoleRoute>} />
          <Route path="/admin/shipping" element={<RoleRoute roles={['ADMIN']}><AdminShippingPage /></RoleRoute>} />
          <Route path="/admin/tax" element={<RoleRoute roles={['ADMIN']}><AdminTaxPage /></RoleRoute>} />
          <Route path="/admin/payouts" element={<RoleRoute roles={['ADMIN']}><AdminPayoutsPage /></RoleRoute>} />
          <Route path="/admin/banners" element={<RoleRoute roles={['ADMIN']}><AdminBannersPage /></RoleRoute>} />
          <Route path="/admin/policies" element={<RoleRoute roles={['ADMIN']}><AdminPoliciesPage /></RoleRoute>} />
          <Route path="/admin/delivery-partners" element={<RoleRoute roles={['ADMIN']}><AdminDeliveryPartnersPage /></RoleRoute>} />
          <Route path="/admin/reviews" element={<RoleRoute roles={['ADMIN']}><AdminReviewsPage /></RoleRoute>} />

          <Route path="/help" element={guard(<HelpCentrePage />)} />
          <Route path="/support/tickets" element={guard(<SupportTicketsPage />)} />
          <Route path="/notifications" element={guard(<NotificationsPage />)} />
          <Route path="/policy/:slug" element={<PolicyPage />} />
          <Route path="/seller/bulk" element={guard(<SellerBulkUploadPage />)} />
          <Route path="/seller/analytics" element={guard(<SellerAnalyticsPage />)} />
          <Route path="/seller/earnings" element={guard(<SellerEarningsPage />)} />
          <Route path="/seller/help" element={guard(<SellerHelpPage />)} />
          <Route path="/seller/returns" element={guard(<SellerReturnsPage />)} />
          <Route path="/reseller/leaderboard" element={guard(<ResellerLeaderboardPage />)} />
          <Route path="/reseller/wallet" element={guard(<ResellerWalletPage />)} />
          <Route path="/reseller/team" element={guard(<ResellerTeamPage />)} />
          <Route path="/reseller/product/:id" element={guard(<ResellerProductPage />)} />
          <Route path="/reseller/share" element={guard(<ResellerShareLibraryPage />)} />
          <Route path="/profile" element={guard(<ProfilePage />)} />
          <Route path="/addresses" element={guard(<AddressesPage />)} />
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
