import { Link } from 'react-router-dom';
import { PHOTO_ASSETS } from '../../data/photoAssets.js';

const ITEMS = [
  ['Women', 'women', 'Women'], ['Men', 'men', 'Men'], ['Kids', 'kids', 'Kids'], ['Home & Kitchen', 'home', 'Home'],
  ['Beauty', 'beauty', 'Beauty'], ['Footwear', 'footwear', 'Footwear'], ['Jewellery', 'jewellery', 'Jewellery'], ['Electronics', 'electronics', 'Electronics'],
];

export default function CategoryStrip({ categories = [] }) {
  const linkFor = (name) => { const c = (categories || []).find((x) => x.name?.toLowerCase() === name.toLowerCase()); return c ? `/search?category=${c.id}` : `/category?q=${encodeURIComponent(name)}`; };
  return <section className="max-w-7xl mx-auto px-4 lg:px-6 py-6">
    <div className="flex items-end justify-between mb-4"><div><p className="text-xs uppercase tracking-[.18em] text-brand font-bold">Shop by category</p><h2 className="text-2xl font-extrabold text-slate-900 mt-1">Find what you love</h2></div><Link to="/search" className="text-sm font-bold text-brand">View all</Link></div>
    <div className="grid grid-cols-4 sm:grid-cols-4 lg:grid-cols-8 gap-3 md:gap-4">
      {ITEMS.map(([label, slug, apiName]) => <Link key={slug} to={linkFor(apiName)} className="group text-center"><div className="aspect-square rounded-2xl overflow-hidden border border-slate-100 bg-white shadow-sm group-hover:-translate-y-1 group-hover:shadow-md transition"><img src={PHOTO_ASSETS[slug]} alt={label} className="w-full h-full object-cover" /></div><p className="text-xs sm:text-sm font-semibold text-slate-700 mt-2 leading-tight">{label}</p></Link>)}
    </div>
  </section>;
}
