import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getCategories } from '../../api/productApi.js';
import { uploadFile } from '../../api/fileApi.js';
import { createProduct } from '../../api/sellerApi.js';
import SellerLayout from '../../layouts/SellerLayout.jsx';

export default function SellerAddProductPage() {
  const nav = useNavigate();
  const [cats, setCats] = useState([]);
  const [f, setF] = useState({ name: '', description: '', price: '', mrp: '', categoryId: '', imageUrl: '', sizes: '', stock: 10 });
  const [err, setErr] = useState('');
  useEffect(() => { getCategories().then(setCats); }, []);
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value });
  const save = async () => {
    const sizes = f.sizes.split(',').map((s) => s.trim()).filter(Boolean);
    try {
      await createProduct({ name: f.name, description: f.description, price: +f.price, mrp: +f.mrp, categoryId: +f.categoryId || null, imageUrl: f.imageUrl,
        variants: (sizes.length ? sizes : ['Free']).map((size) => ({ size, stock: +f.stock })) });
      nav('/seller/products');
    } catch (e) { setErr(e.response?.data?.message || 'Could not save product'); }
  };
  const field = 'w-full border rounded-lg px-3 py-2 mb-2';
  return (
    <SellerLayout title="Add a product">
      <input className={field} placeholder="Product name" value={f.name} onChange={set('name')} />
      <textarea className={field} rows="3" placeholder="Description" value={f.description} onChange={set('description')} />
      <div className="flex gap-2"><input className={field} placeholder="Selling price" value={f.price} onChange={set('price')} /><input className={field} placeholder="MRP" value={f.mrp} onChange={set('mrp')} /></div>
      <select className={field} value={f.categoryId} onChange={set('categoryId')}><option value="">Choose category</option>{cats.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}</select>
      <input className={field} placeholder="Image link (optional)" value={f.imageUrl} onChange={set('imageUrl')} />
      <label className="block text-sm mb-2">Or upload an image: <input type="file" accept="image/*" onChange={(e) => e.target.files[0] && uploadFile(e.target.files[0]).then((u) => setF({ ...f, imageUrl: u })).catch((x) => setErr(x.response?.data?.message || 'Could not upload the image'))} /></label>
      {f.imageUrl && <img src={f.imageUrl} alt="Preview" className="h-24 rounded mb-2" />}
      <div className="flex gap-2"><input className={field} placeholder="Sizes, comma separated (blank = free size)" value={f.sizes} onChange={set('sizes')} /><input className={field} type="number" placeholder="Stock per size" value={f.stock} onChange={set('stock')} /></div>
      <button className="w-full bg-brand-accent text-white py-2 rounded-lg font-semibold" onClick={save}>Save product</button>
      {err && <p className="text-red-600 mt-2 text-sm">{err}</p>}
    </SellerLayout>
  );
}
