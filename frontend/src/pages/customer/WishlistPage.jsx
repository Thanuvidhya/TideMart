import { useEffect, useState } from 'react';
import { getWishlist } from '../../api/wishlistApi.js';
import Navbar from '../../components/common/Navbar.jsx';
import ProductGrid from '../../components/product/ProductGrid.jsx';

export default function WishlistPage() {
  const [items, setItems] = useState([]);
  useEffect(() => { getWishlist().then(setItems); }, []);
  return (
    <>
      <Navbar />
      <main className="max-w-5xl mx-auto p-4">
        <h1 className="text-2xl font-bold mb-3">My wishlist</h1>
        <ProductGrid items={items} />
      </main>
    </>
  );
}
