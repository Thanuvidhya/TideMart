import { useEffect, useState } from 'react';
import { getSellerMe, sellerEarnings } from '../../api/sellerApi.js';
import SellerLayout from '../../layouts/SellerLayout.jsx';

export default function SellerDashboardPage() {
  const [me, setMe] = useState(null);
  const [e, setE] = useState(null);
  useEffect(() => { getSellerMe().then(setMe); sellerEarnings().then(setE); }, []);
  const Card = ({ k, v }) => <div className="bg-white border rounded-2xl p-4"><div className="text-sm text-slate-500">{k}</div><div className="text-2xl font-extrabold">{v}</div></div>;
  return (
    <SellerLayout title={me ? me.seller.businessName : 'Seller dashboard'}>
      {e && (
        <>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-3 mb-5">
            <Card k="Delivered orders" v={e.deliveredOrders} /><Card k="Sales" v={`Rs ${e.gross}`} /><Card k="Commission" v={`Rs ${e.commission}`} /><Card k="You earn" v={`Rs ${e.net}`} />
          </div>
          <h2 className="font-semibold mb-2">Earnings by item</h2>
          {!e.lines.length && <p className="text-slate-500 text-sm">Earnings appear once an order is delivered.</p>}
          {e.lines.map((l, i) => <div key={i} className="bg-white border rounded-xl p-3 mb-2 flex justify-between text-sm"><span>{l.orderNo} | {l.product}</span><span>Rs {l.gross} - Rs {l.commission} = <b>Rs {l.net}</b></span></div>)}
        </>
      )}
    </SellerLayout>
  );
}
