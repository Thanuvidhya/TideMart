import { Link } from 'react-router-dom';
export default function Footer() {
  return <footer className="mt-14 border-t bg-slate-950 text-slate-300">
    <div className="max-w-7xl mx-auto px-4 lg:px-6 py-10 grid grid-cols-2 md:grid-cols-4 gap-8">
      <div className="col-span-2 md:col-span-1"><img src="/tidemart-logo.svg" alt="TideMart" className="h-9 brightness-0 invert opacity-90" /><p className="text-sm mt-4 text-slate-400 leading-6">Affordable fashion, beauty, home essentials and everyday finds in one simple marketplace.</p></div>
      <div><h3 className="font-bold text-white mb-3">Shop</h3><div className="space-y-2 text-sm"><Link className="block hover:text-white" to="/search?q=women">Women</Link><Link className="block hover:text-white" to="/search?q=men">Men</Link><Link className="block hover:text-white" to="/search?q=kids">Kids</Link><Link className="block hover:text-white" to="/search?q=beauty">Beauty</Link></div></div>
      <div><h3 className="font-bold text-white mb-3">Help</h3><div className="space-y-2 text-sm"><Link className="block hover:text-white" to="/help">Help Centre</Link><Link className="block hover:text-white" to="/policy/returns">Returns</Link><Link className="block hover:text-white" to="/policy/privacy">Privacy</Link><Link className="block hover:text-white" to="/policy/terms">Terms</Link></div></div>
      <div><h3 className="font-bold text-white mb-3">Grow with us</h3><div className="space-y-2 text-sm"><Link className="block hover:text-white" to="/seller/signup">Become a Seller</Link><Link className="block hover:text-white" to="/reseller/signup">Become a Reseller</Link><p className="text-slate-500 pt-2">Built for shoppers and small businesses.</p></div></div>
    </div>
    <div className="border-t border-slate-800 text-xs text-slate-500 text-center py-5">© {new Date().getFullYear()} TideMart. All rights reserved.</div>
  </footer>;
}
