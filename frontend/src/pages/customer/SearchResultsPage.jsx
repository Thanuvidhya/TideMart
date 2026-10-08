import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { getCategories, listProducts } from '../../api/productApi.js';
import Navbar from '../../components/common/Navbar.jsx';
import ProductGrid from '../../components/product/ProductGrid.jsx';

export default function SearchResultsPage() {
  const [sp, setSp] = useSearchParams();
  const [cats, setCats] = useState([]);
  const [res, setRes] = useState({ content: [], totalPages: 0, totalElements: 0 });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const q = sp.get('q') || '', category = sp.get('category') || '0', sort = sp.get('sort') || 'popular', max = sp.get('max') || '999999', page = +(sp.get('page') || 0);
  useEffect(() => { getCategories().then(setCats); }, []);
  useEffect(() => { setLoading(true); setError(''); listProducts({ q, category, sort, maxPrice: max, page, size: 20 }).then(setRes).catch((e) => setError(e.response?.data?.message || 'Could not load products.')).finally(() => setLoading(false)); }, [q, category, sort, max, page]);
  const set = (k, v) => { const n = new URLSearchParams(sp); n.set(k, v); if (k !== 'page') n.set('page', 0); setSp(n); };
  const sel = 'border rounded-lg px-2 py-1 bg-white';
  return (
    <>
      <Navbar />
      <main className="max-w-5xl mx-auto p-4">
        <div className="flex flex-wrap gap-2 items-center mb-4">
          <select className={sel} value={category} onChange={(e) => set('category', e.target.value)}>
            <option value="0">All categories</option>{cats.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
          </select>
          <select className={sel} value={sort} onChange={(e) => set('sort', e.target.value)}>
            <option value="popular">Popular</option><option value="price_asc">Price: low to high</option><option value="price_desc">Price: high to low</option>
            <option value="newest">Newest</option><option value="rating">Top rated</option>
          </select>
          <select className={sel} value={max} onChange={(e) => set('max', e.target.value)}>
            <option value="999999">Any price</option><option value="299">Under 299</option><option value="499">Under 499</option><option value="999">Under 999</option>
          </select>
          <span className="ml-auto text-sm text-slate-500">{res.totalElements} products {q && `for "${q}"`}</span>
        </div>
        {error && <div className="mb-4 rounded-xl bg-red-50 border border-red-100 px-4 py-3 text-sm text-red-700">{error}</div>}
        <ProductGrid items={res.content} loading={loading} emptyText={q ? `No products found for “${q}”` : 'No products found in this category.'} />
        <div className="flex gap-2 justify-center mt-6">
          <button disabled={page === 0} className="px-3 py-1 border rounded disabled:opacity-40" onClick={() => set('page', page - 1)}>Previous</button>
          <button disabled={page + 1 >= res.totalPages} className="px-3 py-1 border rounded disabled:opacity-40" onClick={() => set('page', page + 1)}>Next</button>
        </div>
      </main>
    </>
  );
}
