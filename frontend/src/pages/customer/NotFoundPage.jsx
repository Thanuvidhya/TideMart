import { Link } from 'react-router-dom';
import Navbar from '../../components/common/Navbar.jsx';
import Footer from '../../components/common/Footer.jsx';
export default function NotFoundPage(){ return <><Navbar/><main className="min-h-[55vh] grid place-items-center px-4"><div className="text-center"><div className="text-7xl font-black text-brand">404</div><h1 className="text-2xl font-extrabold mt-2">Page not found</h1><p className="text-slate-500 mt-2">The page you requested does not exist.</p><Link to="/" className="inline-block mt-5 bg-brand text-white px-5 py-3 rounded-xl font-bold">Back to shopping</Link></div></main><Footer/></>; }
