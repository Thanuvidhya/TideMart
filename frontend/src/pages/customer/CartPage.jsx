import { useEffect, useMemo, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getCart, setQty, setSaved } from '../../api/cartApi.js';
import Navbar from '../../components/common/Navbar.jsx';

const money = (v) => `₹${Number(v || 0).toLocaleString('en-IN')}`;

export default function CartPage() {
  const nav = useNavigate();
  const [cart, setCart] = useState(null);
  const [coupon, setCoupon] = useState('');
  const [applied, setApplied] = useState('');
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(null);
  const [error, setError] = useState('');

  const load = async (code = applied) => {
    setLoading(true); setError('');
    try { setCart(await getCart(code)); } catch (e) { setError(e.response?.data?.message || 'We could not load your cart.'); }
    finally { setLoading(false); }
  };
  useEffect(() => { load(); }, []);

  const change = async (item, qty) => {
    if (qty < 0 || (qty > item.stock)) return;
    setBusy(item.itemId);
    try { setCart(await setQty(item.itemId, qty, applied)); } catch (e) { setError(e.response?.data?.message || 'Could not update this item.'); }
    finally { setBusy(null); }
  };
  const toggleSave = async (item, saved) => {
    setBusy(item.itemId);
    try { setCart(await setSaved(item.itemId, saved)); } catch (e) { setError(e.response?.data?.message || 'Could not update saved items.'); }
    finally { setBusy(null); }
  };

  const count = useMemo(() => cart?.items?.reduce((n, i) => n + i.qty, 0) || 0, [cart]);
  const price = cart?.price;

  return <><Navbar /><main className="bg-slate-50 min-h-screen py-6 px-4">
    <div className="max-w-6xl mx-auto">
      <div className="flex items-end justify-between mb-5"><div><p className="text-xs uppercase tracking-[.18em] text-brand font-bold">Your shopping bag</p><h1 className="text-2xl md:text-3xl font-extrabold text-slate-900 mt-1">My Cart <span className="text-slate-400 font-semibold text-base">({count} items)</span></h1></div><Link to="/" className="text-sm font-bold text-brand">← Continue shopping</Link></div>
      {error && <div className="mb-4 bg-red-50 border border-red-100 text-red-700 rounded-xl px-4 py-3 flex items-center justify-between"><span>{error}</span><button className="font-bold" onClick={() => load()}>Retry</button></div>}
      {loading ? <div className="grid lg:grid-cols-[1fr_340px] gap-5"><div className="bg-white rounded-2xl border p-5 animate-pulse space-y-5">{[1,2,3].map(i => <div key={i} className="flex gap-4"><div className="w-28 h-28 bg-slate-200 rounded-xl"/><div className="flex-1 space-y-3"><div className="h-4 bg-slate-200 rounded w-3/5"/><div className="h-3 bg-slate-200 rounded w-2/5"/><div className="h-4 bg-slate-200 rounded w-1/4"/></div></div>)}</div><div className="h-64 bg-white rounded-2xl border animate-pulse"/></div> : !cart?.items?.length ? <div className="bg-white border rounded-3xl p-12 text-center"><div className="text-6xl">🛒</div><h2 className="text-xl font-extrabold mt-4">Your cart is empty</h2><p className="text-slate-500 mt-1">Find something you love and add it to your cart.</p><Link to="/" className="inline-block mt-5 bg-brand text-white px-6 py-3 rounded-xl font-bold">Start Shopping</Link></div> : <div className="grid lg:grid-cols-[1fr_340px] gap-5 items-start">
        <div className="space-y-3">
          <div className="bg-white border rounded-2xl px-5 py-4 flex justify-between"><span className="font-bold">All items</span><span className="text-sm text-slate-500">Free delivery on eligible items</span></div>
          {cart.items.map((i) => { const img = i.imageUrl; return <div key={i.itemId} className="bg-white border rounded-2xl p-4 flex gap-4">
            <Link to={`/product/${i.productId}`} className="w-28 h-32 rounded-xl bg-slate-100 overflow-hidden shrink-0">{img ? <img src={img} alt={i.name} className="w-full h-full object-cover"/> : <div className="h-full grid place-items-center text-3xl">🛍️</div>}</Link>
            <div className="min-w-0 flex-1"><Link to={`/product/${i.productId}`} className="font-bold text-slate-800 hover:text-brand line-clamp-2">{i.name}</Link><p className="text-xs text-slate-500 mt-1">Sold by <b>{i.sellerName || 'TideMart Store'}</b>{i.sellerRating ? ` · ★ ${Number(i.sellerRating).toFixed(1)}` : ''}</p><p className="text-sm text-slate-500 mt-2">Size: <b>{i.size || 'Free'}</b></p><div className="flex items-baseline gap-2 mt-2"><b className="text-lg">{money(i.price)}</b>{i.mrp > i.price && <><s className="text-xs text-slate-400">{money(i.mrp)}</s><span className="text-xs text-emerald-600 font-bold">{i.discountPercent}% OFF</span></>}</div><div className="flex flex-wrap items-center gap-3 mt-4"><div className="flex items-center border rounded-lg overflow-hidden"><button disabled={busy === i.itemId || i.qty <= 1} onClick={() => change(i, i.qty - 1)} className="w-9 h-8 disabled:opacity-40">−</button><span className="w-9 text-center text-sm font-bold">{i.qty}</span><button disabled={busy === i.itemId || i.qty >= i.stock} onClick={() => change(i, i.qty + 1)} className="w-9 h-8 disabled:opacity-40">+</button></div><button disabled={busy === i.itemId} onClick={() => toggleSave(i, true)} className="text-xs font-bold text-slate-600 hover:text-brand">SAVE FOR LATER</button><button disabled={busy === i.itemId} onClick={() => change(i, 0)} className="text-xs font-bold text-red-600">REMOVE</button></div><p className="text-xs text-emerald-600 font-semibold mt-3">✓ Free delivery · Easy returns</p></div>
          </div> })}
          <div className="bg-white border rounded-2xl p-4 flex gap-3"><input value={coupon} onChange={e => setCoupon(e.target.value.toUpperCase())} placeholder="Have a coupon? TIDE10 or FIRST50" className="flex-1 border rounded-xl px-3 py-2 outline-none focus:border-brand"/><button onClick={() => { setApplied(coupon); load(coupon); }} className="px-5 rounded-xl border border-brand text-brand font-bold">Apply</button></div>
          {price?.couponMessage && <div className="text-sm font-semibold text-emerald-700 px-1">{price.couponMessage}</div>}
          {cart.saved?.length > 0 && <section className="bg-white border rounded-2xl p-4"><h2 className="font-extrabold mb-3">Saved for later ({cart.saved.length})</h2>{cart.saved.map(i => <div key={i.itemId} className="flex items-center gap-3 py-3 border-t"><div className="w-16 h-16 rounded-lg bg-slate-100 overflow-hidden">{i.imageUrl && <img src={i.imageUrl} className="w-full h-full object-cover"/>}</div><div className="flex-1"><p className="font-semibold text-sm line-clamp-1">{i.name}</p><p className="text-sm font-bold mt-1">{money(i.price)}</p></div><button className="text-xs font-bold text-brand" onClick={() => toggleSave(i, false)}>MOVE TO CART</button><button className="text-xs font-bold text-red-600" onClick={() => change(i, 0)}>REMOVE</button></div>)}</section>}
        </div>
        <aside className="lg:sticky lg:top-24 space-y-3"><div className="bg-white border rounded-2xl p-5"><h2 className="font-extrabold text-lg">Price Details</h2><div className="space-y-3 text-sm mt-4"><div className="flex justify-between"><span>Price ({count} items)</span><span>{money(price?.subtotal)}</span></div><div className="flex justify-between"><span>Discount</span><span className="text-emerald-600">− {money(price?.discount)}</span></div><div className="flex justify-between"><span>Delivery</span><span className="text-emerald-600 font-semibold">{price?.deliveryFee ? money(price.deliveryFee) : 'FREE'}</span></div>{price?.codFee > 0 && <div className="flex justify-between"><span>COD fee</span><span>{money(price.codFee)}</span></div>}</div><div className="border-t mt-4 pt-4 flex justify-between text-lg font-extrabold"><span>Total Amount</span><span>{money(price?.total)}</span></div><p className="text-xs text-emerald-600 font-semibold mt-3">You are saving {money(price?.discount)} on this order</p><button onClick={() => nav('/checkout', { state: { coupon: applied } })} className="w-full mt-5 bg-brand-accent text-white py-3.5 rounded-xl font-extrabold shadow-sm">Continue</button></div><div className="bg-white border rounded-2xl p-4 text-xs text-slate-500 space-y-2"><p>🔒 Safe & secure payments</p><p>↩ Easy returns on eligible products</p><p>🚚 Reliable delivery tracking</p></div></aside>
      </div>}
    </div>
  </main></>;
}
