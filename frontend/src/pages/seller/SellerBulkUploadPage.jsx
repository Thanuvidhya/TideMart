import { useState } from 'react';
import { bulkUpload } from '../../api/sellerApi.js';
import SellerLayout from '../../layouts/SellerLayout.jsx';

const SAMPLE = 'name,description,price,mrp,categoryId,sizes,stock\nCotton Kurti,Soft cotton kurti,399,899,1,S|M|L,10\nCoffee Mug,Ceramic mug,149,399,4,,25';

export default function SellerBulkUploadPage() {
  const [csv, setCsv] = useState(SAMPLE);
  const [res, setRes] = useState(null);
  const [err, setErr] = useState('');
  const go = () => bulkUpload(csv).then(setRes).catch((e) => setErr(e.response?.data?.message || 'Upload failed'));
  return (
    <SellerLayout title="Bulk upload (CSV)">
      <p className="text-sm text-slate-500 mb-2">One product per line: name, description, price, mrp, categoryId, sizes separated by |, stock per size. Category IDs: 1 Women, 2 Men, 3 Kids, 4 Home, 5 Beauty, 6 Footwear. Leave sizes empty for a free size. Do not use commas inside a field.</p>
      <textarea className="w-full border rounded-lg p-3 font-mono text-sm" rows="8" value={csv} onChange={(e) => setCsv(e.target.value)} />
      <button className="mt-2 px-4 py-2 bg-brand-accent text-white rounded-lg" onClick={go}>Upload products</button>
      {err && <p className="text-red-600 text-sm mt-2">{err}</p>}
      {res && (<div className="mt-3 text-sm"><b>{res.created} products created.</b>{res.errors.map((e) => <div key={e} className="text-red-600">{e}</div>)}</div>)}
    </SellerLayout>
  );
}
