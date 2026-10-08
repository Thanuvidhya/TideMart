import api from './axiosClient';

const unwrap = (p) => p.then((r) => r.data.data);

export const sellerSignup = (body) => unwrap(api.post('/sellers/signup', body));
export const getSellerMe = () => unwrap(api.get('/seller/me'));
export const savePickup = (body) => unwrap(api.put('/seller/pickup', body));
export const sellerProducts = () => unwrap(api.get('/seller/products'));
export const createProduct = (body) => unwrap(api.post('/seller/products', body));
export const setStock = (id, stock) => unwrap(api.put(`/seller/variants/${id}/stock`, null, { params: { stock } }));
export const sellerOrders = () => unwrap(api.get('/seller/orders'));
export const setOrderStatus = (id, to) => unwrap(api.post(`/seller/orders/${id}/status`, null, { params: { to } }));
export const getLabel = (id) => unwrap(api.get(`/seller/orders/${id}/label`));
export const sellerEarnings = () => unwrap(api.get('/seller/earnings'));
export const updatePrice = (id, price, mrp) => unwrap(api.put(`/seller/products/${id}`, null, { params: { price, mrp } }));
export const sellerAnalytics = () => unwrap(api.get('/seller/analytics'));
export const bulkUpload = (csv) => unwrap(api.post('/seller/products/bulk', csv, { headers: { 'Content-Type': 'text/plain' } }));
export const rejectOrder = (id, reason) => unwrap(api.post(`/seller/orders/${id}/reject`, null, { params: { reason } }));
export const saveSizeChart = (id, chart) => unwrap(api.put(`/seller/products/${id}/size-chart`, { chart }));
export const addMedia = (id, url, type) => unwrap(api.post(`/seller/products/${id}/media`, { url, type }));
export const promoteProduct = (id, days) => unwrap(api.post(`/seller/products/${id}/promote`, null, { params: { days } }));
