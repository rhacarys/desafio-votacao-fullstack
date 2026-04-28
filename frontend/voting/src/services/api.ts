import axios from 'axios';
import { getErrorMessage } from '../utils/errorMapper';

export const api = axios.create({
  baseURL: '/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Error:', error.response?.data || error.message);
    let customMessage = getErrorMessage("UNKNOWN_ERROR");

    if (!error.response) {
      customMessage = getErrorMessage("NETWORK_ERROR");
    } else if (error.response.data?.code) {
      customMessage = getErrorMessage(error.response.data.code);
    } else if (error.response.status === 404) {
      customMessage = getErrorMessage("AGENDA_NOT_FOUND");
    }
    error.uiMessage = customMessage;
    
    return Promise.reject(error);
  }
);