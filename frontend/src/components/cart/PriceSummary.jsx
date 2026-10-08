export default function PriceSummary({ price }) {
  if (!price) return null;
  const Row = ({ k, v, cls = '' }) => <div className={`flex justify-between py-1 ${cls}`}><span>{k}</span><span>{v}</span></div>;
  return (
    <div className="border rounded-2xl p-4 bg-white">
      <Row k="Items" v={`Rs ${price.subtotal}`} />
      {price.discount > 0 && <Row k="Coupon" v={`- Rs ${price.discount}`} cls="text-green-700" />}
      <Row k="Delivery" v={price.deliveryFee ? `Rs ${price.deliveryFee}` : 'Free'} />
      {price.codFee > 0 && <Row k="COD fee" v={`Rs ${price.codFee}`} />}
      <Row k="Total" v={`Rs ${price.total}`} cls="font-bold text-lg border-t mt-1 pt-2" />
    </div>
  );
}
