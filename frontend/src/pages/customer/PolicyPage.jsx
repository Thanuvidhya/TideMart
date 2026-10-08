import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { getPolicy } from '../../api/productApi.js';
import Navbar from '../../components/common/Navbar.jsx';

export default function PolicyPage() {
  const { slug } = useParams();
  const [p, setP] = useState(null);
  const [missing, setMissing] = useState(false);
  useEffect(() => { setMissing(false); getPolicy(slug).then(setP).catch(() => setMissing(true)); }, [slug]);
  return (
    <>
      <Navbar />
      <main className="max-w-2xl mx-auto p-4">
        {missing && <p className="text-slate-500">This page is not available.</p>}
        {p && (<><h1 className="text-2xl font-bold mb-3">{p.title}</h1><p className="whitespace-pre-line">{p.content}</p></>)}
      </main>
    </>
  );
}
