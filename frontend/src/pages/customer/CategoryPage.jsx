import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { listProducts } from '../../api/productApi.js';
import Navbar from '../../components/common/Navbar.jsx';
import ProductGrid from '../../components/product/ProductGrid.jsx';
import Footer from '../../components/common/Footer.jsx';

export default function CategoryPage() {
  const [params] = useSearchParams();
  const [data, setData] = useState({ content: [], totalElements: 0 });
  const [loading, setLoading] = useState(true);
  const category = params.get('category');
  const q = params.get('q') || '';
  useEffect(() => { setLoading(true); listProducts({ categoryId: category || undefined, q: q || undefined, size: 24 }).then(setData).catch(() => setData({ content: [] })).finally(() => setLoading(false)); }, [category, q]);
  return <><Navbar /><main className="max-w-7xl mx-auto px-4 lg:px-6 py-7 min-h-[60vh]"><div className="mb-5"><p className="text-xs uppercase tracking-[.18em] text-brand font-bold">TideMart collection</p><h1 className="text-2xl md:text-3xl font-extrabold mt-1">{q ? `Results for “${q}”` : 'Shop this category'}</h1><p className="text-sm text-slate-500 mt-1">{data.totalElements ? `${data.totalElements} products` : 'Browse available products'}</p></div>{loading ? <div className="py-16 text-center text-slate-500">Loading products...</div> : <ProductGrid items={data.content || data.products || []} />}</main><Footer /></>;
}
