import { useEffect, useState } from 'react';
import { uploadFile } from '../../api/fileApi.js';
import { addMedia, promoteProduct, saveSizeChart, sellerProducts, setStock, updatePrice } from '../../api/sellerApi.js';
import SellerLayout from '../../layouts/SellerLayout.jsx';

export default function SellerProductsPage() {
  const [list, setList] = useState([]);
  const load = () => sellerProducts().then(setList);
  useEffect(() => { load(); }, []);
  const change = (v, n) => setStock(v.id, Math.max(0, v.stock + n)).then(load);
  const editPrice = (p) => {
    const price = window.prompt('New selling price', p.price);
    const mrp = price && window.prompt('New MRP', p.mrp);
    if (price && mrp) updatePrice(p.id, +price, +mrp).then(load).catch((e) => alert(e.response?.data?.message || 'Failed'));
  };
  return (
    <SellerLayout title="My products">
      {!list.length && <p className="text-slate-500">No products yet. Add your first one.</p>}
      {list.map(({ product: p, variants }) => (
        <div key={p.id} className="bg-white border rounded-xl p-3 mb-3">
          <div className="flex justify-between"><b>{p.name}</b><span className="text-sm">{p.status}</span></div>
          <div className="text-sm text-slate-500 mb-2">Rs {p.price} (MRP Rs {p.mrp}) <button className="text-brand ml-2" onClick={() => editPrice(p)}>Edit price</button>
            <button className="text-brand ml-2" onClick={() => { const c = window.prompt('Size chart. Rows separated by ; and columns by , e.g. Size,Chest,Waist;S,36,30;M,38,32'); if (c) saveSizeChart(p.id, c).then(() => alert('Size chart saved')); }}>Size chart</button>
            <label className="text-brand ml-2 cursor-pointer">Add video or photo<input type="file" accept="video/mp4,video/webm,image/*" className="hidden" onChange={(e) => { const file = e.target.files[0]; if (file) uploadFile(file).then((u) => addMedia(p.id, u, file.type.startsWith('video') ? 'VIDEO' : 'IMAGE')).then(() => alert('Added')).catch((x) => alert(x.response?.data?.message || 'Upload failed')); }} /></label>
            <button className="text-brand ml-2" onClick={() => { const d = window.prompt('Promote for how many days? (1 to 30, free in this demo)', '7'); if (d) promoteProduct(p.id, +d).then(() => alert('Promoted')).catch((x) => alert(x.response?.data?.message || 'Failed')); }}>Promote</button></div>
          <div className="flex gap-3 flex-wrap">
            {variants.map((v) => (
              <div key={v.id} className="border rounded-lg px-2 py-1 text-sm flex items-center gap-2">
                {v.size}: <button onClick={() => change(v, -1)}>-</button><b className={v.stock <= 5 ? 'text-red-600' : ''}>{v.stock}</b><button onClick={() => change(v, 1)}>+</button>
              </div>
            ))}
          </div>
        </div>
      ))}
    </SellerLayout>
  );
}
