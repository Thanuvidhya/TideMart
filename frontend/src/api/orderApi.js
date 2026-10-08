import api from './axiosClient';

const unwrap = (p) => p.then((r) => r.data.data);

export const checkPincode = (pin) => unwrap(api.get(`/pincode/${pin}/check`));
export const placeOrder = (body) => unwrap(api.post('/orders', body));
export const listOrders = () => unwrap(api.get('/orders'));
export const getOrder = (id) => unwrap(api.get(`/orders/${id}`));
export const cancelOrder = (id) => unwrap(api.post(`/orders/${id}/cancel`));
export const advanceOrder = (id) => unwrap(api.post(`/orders/${id}/advance`));
export const requestReturn = (body) => unwrap(api.post('/returns', body));
export const listReturns = () => unwrap(api.get('/returns'));
export const advanceReturn = (id) => unwrap(api.post(`/returns/${id}/advance`));
