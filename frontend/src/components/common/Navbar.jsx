import { useEffect, useRef, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { getCart } from '../../api/cartApi.js';
import { getCategories, saveSearch, suggest } from '../../api/productApi.js';
import { getNotifications } from '../../api/supportApi.js';
import useAuth from '../../hooks/useAuth.js';
import useVoiceSearch from '../../hooks/useVoiceSearch.js';
import LanguageSwitcher from './LanguageSwitcher.jsx';
import LocationPicker from './LocationPicker.jsx';

const ICONS = {
  cart: 'M3 4h2l2.5 11h9L19 7H6M10 20h.01M17 20h.01',
  user: 'M20 21a8 8 0 0 0-16 0M12 12a4 4 0 1 0 0-8 4 4 0 0 0 0 8z',
  mic: 'M12 15a3 3 0 0 0 3-3V6a3 3 0 0 0-6 0v6a3 3 0 0 0 3 3zM19 11a7 7 0 0 1-14 0M12 18v3',
  search: 'M21 21l-4.3-4.3M11 18a7 7 0 1 0 0-14 7 7 0 0 0 0 14z',
  heart: 'M20.8 8.7c0 5.5-8.8 10.3-8.8 10.3S3.2 14.2 3.2 8.7A4.7 4.7 0 0 1 12 6.2a4.7 4.7 0 0 1 8.8 2.5z',
  box: 'M4 7.5L12 3l8 4.5v9L12 21l-8-4.5zM4 7.5l8 4.5 8-4.5M12 12v9',
  map: 'M20 10c0 5-8 11-8 11S4 15 4 10a8 8 0 1 1 16 0zM12 10.5a2 2 0 1 0 0-4 2 2 0 0 0 0 4z',
  menu: 'M4 7h16M4 12h16M4 17h16',
};
const Icon = ({ name, className = 'w-5 h-5' }) => <svg viewBox="0 0 24 24" className={className} fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round"><path d={ICONS[name]} /></svg>;
const Badge = ({ n }) => n > 0 ? <span className="absolute -top-1 -right-2 bg-brand-accent text-white text-[10px] font-bold rounded-full min-w-4 h-4 px-1 grid place-items-center">{n}</span> : null;

const CATEGORY_ORDER = ['Women', 'Men', 'Kids', 'Home', 'Beauty', 'Footwear', 'Jewellery', 'Electronics'];

export default function Navbar() {
  const nav = useNavigate();
  const loc = useLocation();
  const { t } = useTranslation();
  const { auth, signOut } = useAuth();
  const voice = useVoiceSearch();
  const menuBox = useRef(null);
  const [q, setQ] = useState('');
  const [sug, setSug] = useState([]);
  const [cats, setCats] = useState([]);
  const [unread, setUnread] = useState(0);
  const [cartCount, setCartCount] = useState(0);
  const [menu, setMenu] = useState(false);
  useEffect(() => { getCategories().then(setCats).catch(() => {}); }, []);
  useEffect(() => { if (q.length < 2) return setSug([]); const id = setTimeout(() => suggest(q).then(setSug).catch(() => setSug([])), 250); return () => clearTimeout(id); }, [q]);
  useEffect(() => { if (!auth) { setUnread(0); setCartCount(0); return; } getNotifications().then((d) => setUnread(d.unread)).catch(() => {}); getCart().then((d) => setCartCount(d.items.reduce((a, i) => a + i.qty, 0))).catch(() => {}); }, [auth, loc.pathname]);
  useEffect(() => { const h = (e) => menuBox.current && !menuBox.current.contains(e.target) && setMenu(false); document.addEventListener('mousedown', h); return () => document.removeEventListener('mousedown', h); }, []);
  const go = (term) => { const value = term.trim(); if (!value) return; if (auth) saveSearch(value).catch(() => {}); setSug([]); nav(`/search?q=${encodeURIComponent(value)}`); };
  const categoryLink = (name) => { const c = cats.find((x) => x.name?.toLowerCase() === name.toLowerCase()); return c ? `/search?category=${c.id}` : `/search?q=${encodeURIComponent(name)}`; };
  const Item = ({ to, children, icon }) => <Link to={to} onClick={() => setMenu(false)} className="flex items-center gap-3 px-4 py-2.5 text-sm hover:bg-slate-50"><Icon name={icon} className="w-4 h-4 text-slate-500" />{children}</Link>;
  return (
    <header className="sticky top-0 z-40 bg-white/95 backdrop-blur border-b border-slate-100 shadow-[0_1px_8px_rgba(15,23,42,.05)]">
      <div className="max-w-7xl mx-auto px-4 lg:px-6 py-3 flex items-center gap-4">
        <button onClick={() => setMenu(!menu)} className="lg:hidden p-2 rounded-lg hover:bg-slate-100" aria-label="Open menu"><Icon name="menu" /></button>
        <Link to="/" className="shrink-0 flex items-center"><img src="/tidemart-logo.svg" alt="TideMart" className="h-10 w-auto" /></Link>
        <div className="hidden lg:flex shrink-0 items-center text-xs text-slate-600"><LocationPicker /></div>
        <div className="relative flex-1 min-w-0 max-w-2xl mx-auto">
          <div className="flex items-center rounded-xl bg-slate-100 border border-transparent focus-within:bg-white focus-within:border-brand/30 focus-within:ring-4 focus-within:ring-brand/5 transition">
            <Icon name="search" className="w-5 h-5 ml-4 text-slate-400" />
            <input aria-label="Search products" className="w-full bg-transparent border-0 outline-none px-3 py-3 text-sm" placeholder="Search for products, brands and more" value={q} onChange={(e) => setQ(e.target.value)} onKeyDown={(e) => e.key === 'Enter' && go(q)} />
            {voice.supported && <button type="button" aria-label="Search by voice" className={`mr-3 ${voice.listening ? 'text-red-600' : 'text-brand'}`} onClick={() => voice.start((txt) => { setQ(txt); go(txt); })}><Icon name="mic" /></button>}
          </div>
          {sug.length > 0 && <div className="absolute left-0 right-0 top-full mt-2 bg-white border border-slate-100 rounded-xl shadow-xl overflow-hidden z-50">{sug.map((s) => <button key={s} className="block w-full text-left px-4 py-3 text-sm hover:bg-slate-50" onClick={() => go(s)}>{s}</button>)}</div>}
        </div>
        <div className="hidden md:flex items-center gap-2 shrink-0"><Link to="/seller/signup" className="px-3 py-2 rounded-xl border border-brand/20 bg-brand/5 text-brand text-sm font-extrabold">Sell on TideMart</Link></div><div className="flex items-center gap-1 sm:gap-2 shrink-0">
          <div className="relative" ref={menuBox}>
            <button aria-label="Account menu" aria-expanded={menu} onClick={() => setMenu(!menu)} className="nav-action"><span className="relative"><Icon name="user" /><Badge n={unread} /></span><span className="hidden sm:block">Account</span></button>
            {menu && <div className="absolute right-0 top-full mt-3 w-72 max-w-[92vw] bg-white border border-slate-100 rounded-2xl shadow-2xl z-50 overflow-hidden">
              {auth ? <>
                <div className="px-4 py-4 bg-slate-50"><p className="font-bold text-slate-900">Hello, {auth.name || 'Shopper'}</p><p className="text-xs text-slate-500 mt-1">Manage your TideMart account</p></div>
                <div className="py-2"><Item to="/orders" icon="box">My Orders</Item><Item to="/wishlist" icon="heart">Wishlist</Item><Item to="/addresses" icon="map">Addresses</Item><Item to="/notifications" icon="user">Notifications{unread ? ` (${unread})` : ''}</Item><Item to="/wallet" icon="box">Wallet</Item><Item to="/help" icon="user">Help Centre</Item></div>
                <div className="border-t px-4 py-4"><p className="text-[11px] uppercase tracking-wider font-bold text-slate-400 mb-3">Grow with TideMart</p><div className="grid gap-2"><Link onClick={() => setMenu(false)} to={(auth.roles?.includes('SELLER') || auth.role === 'SELLER') ? '/seller' : '/seller/signup'} className="rounded-xl border border-brand/15 bg-brand/5 px-3 py-3 hover:bg-brand/10"><p className="text-sm font-extrabold text-slate-900">{(auth.roles?.includes('SELLER') || auth.role === 'SELLER') ? 'Seller Dashboard' : 'Sell on TideMart'}</p><p className="text-xs text-slate-500 mt-0.5">{(auth.roles?.includes('SELLER') || auth.role === 'SELLER') ? 'Manage products and orders' : 'Create your seller account →'}</p></Link><Link onClick={() => setMenu(false)} to={(auth.roles?.includes('RESELLER') || auth.role === 'RESELLER') ? '/reseller' : '/reseller/signup'} className="rounded-xl border border-slate-200 px-3 py-3 hover:bg-slate-50"><p className="text-sm font-extrabold text-slate-900">{(auth.roles?.includes('RESELLER') || auth.role === 'RESELLER') ? 'Reseller Dashboard' : 'Become a Reseller'}</p><p className="text-xs text-slate-500 mt-0.5">{(auth.roles?.includes('RESELLER') || auth.role === 'RESELLER') ? 'Share products and earn margin' : 'Start reselling TideMart products →'}</p></Link></div>{auth.role === 'ADMIN' && <Link onClick={() => setMenu(false)} to="/admin" className="block text-sm font-semibold text-brand py-2">Admin Panel</Link>}</div>
              </> : <div className="p-5"><p className="font-semibold">Welcome to TideMart</p><p className="text-sm text-slate-500 mt-1 mb-4">Login to see orders, wishlist and wallet.</p><Link to="/login" onClick={() => setMenu(false)} className="block text-center py-2.5 rounded-xl bg-brand text-white font-bold">Login / Sign up</Link></div>}
              <div className="border-t px-4 py-3"><div className="mb-2"><LanguageSwitcher /></div>{auth && <button className="text-sm text-red-600 font-medium" onClick={() => { setMenu(false); signOut(); nav('/'); }}>Logout</button>}</div>
            </div>}
          </div>
          <Link to="/cart" className="nav-action"><span className="relative"><Icon name="cart" /><Badge n={cartCount} /></span><span className="hidden sm:block">Cart</span></Link>
        </div>
      </div>
      <div className="hidden lg:block border-t border-slate-100">
        <nav className="max-w-7xl mx-auto px-6 flex items-center gap-7 overflow-x-auto whitespace-nowrap text-sm" aria-label="Shopping categories">
          {CATEGORY_ORDER.map((name) => <Link key={name} className="category-link" to={categoryLink(name)}>{name === 'Home' ? 'Home & Kitchen' : name}</Link>)}
        </nav>
      </div>
    </header>
  );
}
