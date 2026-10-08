import ProductCard from './ProductCard.jsx';

export default function ProductGrid({ items, loading = false, emptyText = 'No products found.' }) {
  if (loading) return <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-4">{Array.from({ length: 8 }).map((_, i) => <div key={i} className="bg-white border border-slate-100 rounded-2xl overflow-hidden animate-pulse"><div className="aspect-square bg-slate-200" /><div className="p-3 space-y-2"><div className="h-3 bg-slate-200 rounded w-4/5" /><div className="h-4 bg-slate-200 rounded w-2/5" /><div className="h-3 bg-slate-200 rounded w-3/5" /></div></div>)}</div>;
  if (!items?.length) return <div className="bg-white border border-dashed border-slate-200 rounded-2xl py-12 text-center"><div className="text-4xl mb-2">🛍️</div><p className="font-bold text-slate-700">{emptyText}</p><p className="text-sm text-slate-400 mt-1">Try another category or search term.</p></div>;
  return <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4">{items.map((p) => <ProductCard key={p.id} p={p} />)}</div>;
}
