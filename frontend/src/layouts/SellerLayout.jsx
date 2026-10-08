import { Link } from 'react-router-dom';
import Navbar from '../components/common/Navbar.jsx';

export default function SellerLayout({ title, children }) {
  return (
    <>
      <Navbar />
      <main className="max-w-4xl mx-auto p-4">
        <nav className="flex gap-4 text-sm text-brand mb-4 overflow-x-auto">
          <Link to="/seller">Dashboard</Link><Link to="/seller/products">Products</Link><Link to="/seller/add-product">Add product</Link>
          <Link to="/seller/orders">Orders</Link><Link to="/seller/pickup">Pickup address</Link><Link to="/seller/bulk">Bulk upload</Link><Link to="/seller/analytics">Analytics</Link>
        </nav>
        <h1 className="text-2xl font-bold mb-3">{title}</h1>
        {children}
      </main>
    </>
  );
}
