import { Link } from 'react-router-dom';
import Navbar from '../components/common/Navbar.jsx';

export default function ResellerLayout({ title, children }) {
  return (
    <>
      <Navbar />
      <main className="max-w-4xl mx-auto p-4">
        <nav className="flex gap-4 text-sm text-brand mb-4 overflow-x-auto">
          <Link to="/reseller">Catalogue</Link><Link to="/reseller/place-order">Place order</Link><Link to="/reseller/orders">Orders</Link>
          <Link to="/reseller/customers">Customers</Link><Link to="/reseller/earnings">Earnings</Link><Link to="/reseller/leaderboard">Level and leaderboard</Link>
        </nav>
        <h1 className="text-2xl font-bold mb-3">{title}</h1>
        {children}
      </main>
    </>
  );
}
