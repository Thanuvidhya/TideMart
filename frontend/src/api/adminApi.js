import api from './axiosClient';

const unwrap = (p) => p.then((r) => r.data.data);

export const adminStats = () => unwrap(api.get('/admin/stats'));
export const adminList = (t) => unwrap(api.get(`/admin/table/${t}`));
export const adminCreate = (t, body) => unwrap(api.post(`/admin/table/${t}`, body));
export const adminUpdate = (t, id, body) => unwrap(api.put(`/admin/table/${t}/${id}`, body));
export const sellerDecision = (id, approve) => unwrap(api.post(`/admin/sellers/${id}/decision`, null, { params: { approve } }));
export const returnStatus = (id, to) => unwrap(api.post(`/admin/returns/${id}/status`, null, { params: { to } }));
export const assignDelivery = (orderId, partnerId) => unwrap(api.post(`/admin/delivery/${orderId}/assign`, null, { params: { partnerId } }));
export const deliverOrder = (orderId, otp) => unwrap(api.post(`/admin/delivery/${orderId}/deliver`, null, { params: { otp } }));
export const failDelivery = (orderId, note) => unwrap(api.post(`/admin/delivery/${orderId}/failed`, null, { params: { note } }));
export const adminFraud = () => unwrap(api.get('/admin/fraud'));
export const ticketView = (id) => unwrap(api.get(`/admin/tickets/${id}`));
export const ticketReply = (id, message) => unwrap(api.post(`/admin/tickets/${id}/reply`, { message }));
