import api from './axiosClient';

export const uploadFile = (file) => {
  const form = new FormData();
  form.append('file', file);
  return api.post('/files', form).then((r) => r.data.data.url);
};
