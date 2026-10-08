import { useEffect, useState } from 'react';
import { addReview, getReviews } from '../../api/supportApi.js';

export default function ReviewList({ productId }) {
  const [list, setList] = useState([]);
  const [rating, setRating] = useState(5);
  const [comment, setComment] = useState('');
  const [msg, setMsg] = useState('');
  const load = () => getReviews(productId).then(setList);
  useEffect(() => { load(); }, [productId]);
  const submit = () => addReview({ productId, rating, comment })
    .then(() => { setComment(''); setMsg('Thank you for your review'); load(); })
    .catch((e) => setMsg(e.response?.data?.message || 'Log in to write a review'));
  return (
    <section className="mt-8">
      <h2 className="font-bold mb-3">Ratings and reviews</h2>
      {!list.length && <p className="text-sm text-slate-500 mb-3">No reviews yet.</p>}
      {list.map((r) => <div key={r.id} className="bg-white border rounded-xl p-3 mb-2 text-sm"><b>{r.user}</b> {'*'.repeat(r.rating)} <span className="text-slate-400">{r.createdAt}</span><br />{r.comment}</div>)}
      <div className="flex gap-2 mt-3 flex-wrap">
        <select className="border rounded-lg px-2 py-2" value={rating} onChange={(e) => setRating(+e.target.value)}>{[5, 4, 3, 2, 1].map((n) => <option key={n} value={n}>{n} stars</option>)}</select>
        <input className="flex-1 min-w-48 border rounded-lg px-3 py-2" placeholder="Write a review (after delivery)" value={comment} onChange={(e) => setComment(e.target.value)} />
        <button className="px-4 bg-brand text-white rounded-lg" onClick={submit}>Submit</button>
      </div>
      {msg && <p className="text-sm mt-2">{msg}</p>}
    </section>
  );
}
