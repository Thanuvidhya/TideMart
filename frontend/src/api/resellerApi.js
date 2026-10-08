import api from './axiosClient';

const unwrap = (p) => p.then((r) => r.data.data);

export const resellerSignup = (body) => unwrap(api.post('/resellers/signup', body));
export const getResellerMe = () => unwrap(api.get('/reseller/me'));
export const savePayoutDetails = (body) => unwrap(api.put('/reseller/payout', body));
export const getCatalog = (params) => unwrap(api.get('/reseller/catalog', { params }));
export const saveMargin = (productId, margin) => unwrap(api.put('/reseller/margin', null, { params: { productId, margin } }));
export const getShare = (ids) => unwrap(api.get('/reseller/share', { params: { ids: ids.join(',') } }));
export const placeCustomerOrder = (body) => unwrap(api.post('/reseller/orders', body));
export const resellerOrders = () => unwrap(api.get('/reseller/orders'));
export const resellerCustomers = () => unwrap(api.get('/reseller/customers'));
export const resellerEarnings = () => unwrap(api.get('/reseller/earnings'));
export const getLeaderboard = () => unwrap(api.get('/reseller/leaderboard'));
