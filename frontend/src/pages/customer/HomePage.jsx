import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getHome, getRecentlyViewed, getTrendingSearches } from '../../api/productApi.js';
import Navbar from '../../components/common/Navbar.jsx';
import BannerCarousel from '../../components/home/BannerCarousel.jsx';
import CategoryStrip from '../../components/home/CategoryStrip.jsx';
import ProductGrid from '../../components/product/ProductGrid.jsx';
import Footer from '../../components/common/Footer.jsx';
import PopularStores from '../../components/home/PopularStores.jsx';
import useAuth from '../../hooks/useAuth.js';

const QUICK = [
  ['7 Days Easy Return', 'Simple returns on eligible products.'],
  ['Cash on Delivery', 'Pay when your order arrives.'],
  ['Value for Money', 'Everyday picks at friendly prices.'],
];
const TRENDING = [
  ['Ethnic Wear', 'women'], ['Casual Shirts', 'men'], ['Kids Fashion', 'kids'], ['Home Essentials', 'home'], ['Beauty Picks', 'beauty'], ['Sneakers', 'footwear'],
];

export default function HomePage() {
  const { auth } = useAuth();
  const [d, setD] = useState({ categories: [], trending: [], banners: [], categoryProducts: {} });
  const [loading, setLoading] = useState(true);
  const [failed, setFailed] = useState(false);
  const [terms, setTerms] = useState([]);
  const [recent, setRecent] = useState([]);
  useEffect(() => { getHome().then(setD).catch(() => { setFailed(true); setD({ categories: [], trending: [], banners: [], categoryProducts: {} }); }).finally(() => setLoading(false)); getTrendingSearches().then(setTerms).catch(() => {}); }, []);
  useEffect(() => { if (auth) getRecentlyViewed().then(setRecent).catch(() => {}); }, [auth]);
  return <>
    <Navbar />
    <main className="bg-slate-50/70 min-h-screen">
      <BannerCarousel banners={d?.banners || []} />
      <section className="max-w-7xl mx-auto px-4 lg:px-6 pt-4">
        <div className="bg-white rounded-2xl border border-slate-100 shadow-sm grid grid-cols-1 sm:grid-cols-3 overflow-hidden">
          {QUICK.map(([title, sub], i) => <div key={title} className={`px-5 py-4 flex items-center gap-3 ${i ? 'border-t sm:border-t-0 sm:border-l border-slate-100' : ''}`}><span className="w-10 h-10 rounded-full bg-brand/10 text-brand grid place-items-center font-bold">✓</span><div><p className="font-bold text-sm text-slate-800">{title}</p><p className="text-xs text-slate-500 mt-0.5">{sub}</p></div></div>)}
        </div>
      </section>
      <CategoryStrip categories={d?.categories || []} />
      {failed && <section className="max-w-7xl mx-auto px-4 lg:px-6 pt-4"><div className="rounded-2xl bg-amber-50 border border-amber-100 px-5 py-4 text-sm text-amber-800 flex justify-between gap-3"><span>Backend server not connected. This is a frontend-only demo. Login and product features require a running backend.</span><button onClick={() => window.location.reload()} className="font-bold underline">Retry</button></div></section>}
      <PopularStores />
      <section className="max-w-7xl mx-auto px-4 lg:px-6 pb-4">
        <div className="flex items-end justify-between mb-4"><div><p className="text-xs uppercase tracking-[.18em] text-brand font-bold">Popular right now</p><h2 className="text-2xl font-extrabold text-slate-900 mt-1">Trending picks</h2></div><Link to="/search" className="text-sm font-bold text-brand">See all</Link></div>
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-3">{TRENDING.map(([label, q]) => <Link key={label} to={`/search?q=${encodeURIComponent(q)}`} className="bg-white border border-slate-100 rounded-xl px-4 py-5 hover:shadow-md hover:-translate-y-0.5 transition"><span className="text-xs text-slate-400">Explore</span><p className="font-bold text-slate-800 mt-1">{label}</p><p className="text-xs text-brand mt-2">Shop now →</p></Link>)}</div>
      </section>
      {terms.length > 0 && <section className="max-w-7xl mx-auto px-4 lg:px-6 py-4"><div className="bg-white rounded-2xl border border-slate-100 px-5 py-4"><span className="font-bold text-sm mr-4">Trending searches</span>{terms.slice(0, 8).map((term) => <Link key={term} to={`/search?q=${encodeURIComponent(term)}`} className="inline-block text-sm text-brand bg-brand/5 rounded-full px-3 py-1.5 mr-2 mb-1.5">{term}</Link>)}</div></section>}
      <section className="max-w-7xl mx-auto px-4 lg:px-6 py-6"><div className="flex items-end justify-between mb-4"><div><p className="text-xs uppercase tracking-[.18em] text-brand font-bold">Picked for you</p><h2 className="text-2xl font-extrabold text-slate-900 mt-1">Products for you</h2></div><Link to="/search" className="text-sm font-bold text-brand">View more</Link></div><ProductGrid items={d?.trending || []} loading={loading} /></section>
      <section className="max-w-7xl mx-auto px-4 lg:px-6 py-6"><div className="flex items-end justify-between mb-4"><div><p className="text-xs uppercase tracking-[.18em] text-brand font-bold">Shop by budget</p><h2 className="text-2xl font-extrabold text-slate-900 mt-1">Picks for every budget</h2></div></div><div className="grid grid-cols-2 md:grid-cols-4 gap-3">{[['Under ₹299',299],['Under ₹499',499],['Under ₹999',999],['Premium picks',1999]].map(([label,max]) => <Link key={label} to={`/search?max=${max}`} className="rounded-2xl bg-white border border-slate-100 p-5 hover:shadow-md transition"><span className="text-xs text-slate-400">TideMart picks</span><p className="font-extrabold text-lg text-slate-900 mt-1">{label}</p><p className="text-xs text-brand font-bold mt-2">Explore now →</p></Link>)}</div></section>
      {Object.entries(d?.categoryProducts || {}).map(([category, items]) => items?.length ? (
        <section key={category} className="max-w-7xl mx-auto px-4 lg:px-6 py-6">
          <div className="flex items-end justify-between mb-4">
            <div><p className="text-xs uppercase tracking-[.18em] text-brand font-bold">Shop {category}</p><h2 className="text-2xl font-extrabold text-slate-900 mt-1">{category} favourites</h2></div>
            <Link to={`/category?q=${encodeURIComponent(category)}`} className="text-sm font-bold text-brand">View all</Link>
          </div>
          <ProductGrid items={items} />
        </section>
      ) : null)}
      {recent.length > 0 && <section className="max-w-7xl mx-auto px-4 lg:px-6 py-6"><div className="flex items-end justify-between mb-4"><h2 className="text-xl font-extrabold text-slate-900">Recently viewed</h2><Link to="/search" className="text-sm font-bold text-brand">Continue shopping</Link></div><ProductGrid items={recent} /></section>}
      <section className="max-w-7xl mx-auto px-4 lg:px-6 py-8"><div className="rounded-2xl bg-gradient-to-r from-brand-dark to-brand text-white px-6 md:px-10 py-8 flex flex-col md:flex-row md:items-center md:justify-between gap-5"><div><p className="text-xs uppercase tracking-[.18em] opacity-70 font-bold">Grow with TideMart</p><h2 className="text-2xl font-extrabold mt-1">Have products to sell?</h2><p className="text-sm opacity-85 mt-1">Start your seller journey without cluttering the shopping experience.</p></div><Link to="/seller/signup" className="bg-white text-brand-dark font-bold px-5 py-3 rounded-xl text-center">Become a Seller</Link></div></section>
    </main>
    <Footer />
  </>;
}
