import api from './axiosClient';

const unwrap = (p) => p.then((r) => r.data.data);

export const getCart = (coupon, pay) => unwrap(api.get('/cart', { params: { coupon: coupon || undefined, pay } }));
export const addToCart = (productId, variantId, qty = 1) => unwrap(api.post('/cart', { productId, variantId, qty }));
export const setQty = (itemId, qty, coupon) => unwrap(api.put(`/cart/${itemId}`, null, { params: { qty, coupon: coupon || undefined } }));
export const setSaved = (itemId, saved) => unwrap(api.put(`/cart/${itemId}/save`, null, { params: { saved } }));
