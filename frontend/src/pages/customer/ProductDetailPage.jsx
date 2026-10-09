import { useEffect, useMemo, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { addToCart } from '../../api/cartApi.js';
import { checkPincode } from '../../api/orderApi.js';
import { toggleWishlist } from '../../api/wishlistApi.js';
import { getProduct, markViewed, notifyMe } from '../../api/productApi.js';
import QuestionsAnswers from '../../components/product/QuestionsAnswers.jsx';
import Navbar from '../../components/common/Navbar.jsx';
import ProductGrid from '../../components/product/ProductGrid.jsx';
import ReviewList from '../../components/product/ReviewList.jsx';
import SizeChartModal from '../../components/product/SizeChartModal.jsx';
import ProductGallery from '../../components/product/ProductGallery.jsx';
import { photoSetForProduct } from '../../data/photoAssets.js';

const highlights = ['Premium everyday quality', 'Comfortable fit and easy styling', 'Packed carefully by the seller', '7-day easy returns on eligible orders'];

export default function ProductDetailPage() {
  const { id } = useParams();
  const nav = useNavigate();
  const [d, setD] = useState(null);
  const [size, setSize] = useState(null);
  const [msg, setMsg] = useState('');
  const [pin, setPin] = useState(localStorage.getItem('tidemart_pin') || '');
  const [pinMsg, setPinMsg] = useState('');

  useEffect(() => {
    setSize(null);
    getProduct(id).then(setD);
    markViewed(id).catch(() => {});
  }, [id]);

  if (!d) return <><Navbar /><div className="max-w-6xl mx-auto p-8">Loading product...</div></>;

  const p = d.product;
  const v = (d.variants || []).find(x => x.size === size);
  const price = d.salePrice ?? p.price;
  const disc = Math.max(0, Math.round((1 - price / p.mrp) * 100));
  const photos = d.media?.length ? d.media : photoSetForProduct(p);

  const add = async () => {
    if (!v) return setMsg('Choose a size first');
    try {
      await addToCart(p.id, v.id, 1);
      nav('/cart');
    } catch (e) {
      if ([401, 403].includes(e.response?.status)) nav('/login');
      else setMsg(e.response?.data?.message || 'Could not add to cart');
    }
  };

  return (
    <>
      <Navbar />
      <main className="max-w-6xl mx-auto p-4 lg:p-6">
        <div className="text-xs text-slate-500 mb-4">
          <Link to="/" className="hover:text-brand">Home</Link> / {d.categoryName} / {p.name}
        </div>
        <div className="grid lg:grid-cols-[1.05fr_.95fr] gap-8">
          <ProductGallery media={photos} alt={p.name} product={p} />
          <div>
            <p className="text-sm text-brand font-semibold">{d.categoryName}</p>
            <h1 className="text-2xl lg:text-3xl font-extrabold text-slate-900 mt-1">{p.name}</h1>
            <div className="flex items-center gap-2 mt-3">
              <span className="bg-emerald-600 text-white px-2 py-1 rounded-md text-sm font-bold">★ {Number(p.ratingAvg || 0).toFixed(1)}</span>
              <span className="text-sm text-slate-500">{p.ratingCount || 0} Ratings & Reviews</span>
            </div>
            <div className="flex items-baseline gap-3 mt-5">
              <b className="text-3xl">₹{Math.round(price)}</b>
              <s className="text-slate-400">₹{Math.round(p.mrp)}</s>
              {disc > 0 && <span className="text-green-600 font-bold">{disc}% off</span>}
            </div>
            <p className="text-xs text-slate-400 mt-1">Inclusive of all taxes</p>
            <div className="border-t border-b py-5 my-5">
              <h2 className="font-bold mb-3">Select Size</h2>
              <div className="flex flex-wrap gap-2">
                {(d.variants || []).map((v) => (
                  <button
                    key={v.id}
                    onClick={() => setSize(v.size)}
                    className={`px-4 py-2 border rounded-lg text-sm ${size === v.size ? 'border-brand bg-brand/5' : 'border-slate-200'}`}
                  >
                    {v.size}
                  </button>
                ))}
              </div>
            </div>
            <button onClick={add} className="w-full bg-brand text-white py-3 rounded-xl font-bold mt-4 hover:bg-brand-dark transition">
              Add to Cart
            </button>
            {msg && <p className="text-sm text-red-500 mt-2">{msg}</p>}
          </div>
        </div>
        <ReviewList productId={id} />
        <ProductGrid items={d.similar || []} title="Similar products" />
      </main>
    </>
  );
}
