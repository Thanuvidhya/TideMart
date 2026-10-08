import { Link } from 'react-router-dom';
import { photoForProduct } from '../../data/photoAssets.js';

export default function ProductCard({ p }) {
  const hue = ((p.id || 1) * 47) % 360;
  const rating = Number(p.ratingAvg || 0).toFixed(1);
  const count = Number(p.ratingCount || 0);
  const price = Number(p.price || 0);
  const mrp = Number(p.mrp || price);
  const discount = Number(p.discountPercent || (mrp > price ? Math.round((1 - price / mrp) * 100) : 0));
  const fallback = photoForProduct(p);
  return <Link to={`/product/${p.id}`} className="group bg-white border border-slate-100 rounded-2xl overflow-hidden hover:shadow-lg hover:-translate-y-1 transition block">
    <div className="relative aspect-square bg-slate-50 overflow-hidden">
      {p.imageUrl || photoForProduct(p) ? <img src={p.imageUrl || fallback} alt={p.name} onError={(e) => { if (e.currentTarget.src !== fallback) e.currentTarget.src = fallback; }} className="w-full h-full object-cover group-hover:scale-[1.03] transition duration-300" /> : <div className="w-full h-full grid place-items-center text-5xl font-extrabold" style={{ background: `hsl(${hue} 70% 92%)`, color: `hsl(${hue} 50% 30%)` }}>{p.name?.[0] || 'T'}</div>}
      <span className="absolute top-2 left-2 bg-white/95 text-[10px] font-bold text-slate-600 px-2 py-1 rounded-full shadow-sm">Free delivery</span>
      {discount > 0 && <span className="absolute bottom-2 left-2 bg-emerald-600 text-white text-[10px] font-bold px-2 py-1 rounded-full">{discount}% OFF</span>}
    </div>
    <div className="p-3">
      {p.promoted && <div className="text-[10px] text-slate-400 mb-1">Sponsored</div>}
      <div className="truncate text-sm text-slate-700">{p.name}</div>
      <div className="flex items-baseline gap-2 mt-1"><b className="text-lg text-slate-900">₹{price}</b>{mrp > price && <s className="text-xs text-slate-400">₹{mrp}</s>}</div>
      <div className="text-[11px] text-slate-400 mt-2 truncate">Sold by {p.sellerName || 'TideMart Store'}</div><div className="flex items-center gap-1.5 mt-1.5"><span className="inline-flex items-center gap-1 bg-emerald-50 text-emerald-700 rounded-full px-2 py-0.5 text-xs font-bold">★ {rating}</span><span className="text-[11px] text-slate-400">{count} ratings</span></div>
      {p.stock > 0 && p.stock <= 5 && <div className="text-[11px] text-red-600 font-semibold mt-2">Only {p.stock} left</div>}
    </div>
  </Link>;
}
