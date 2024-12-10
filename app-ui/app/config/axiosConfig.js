import axios from 'axios';

const axiosConfig = axios.create({
  baseURL:  'https://1bc3-176-126-49-56.ngrok-free.app',
  headers: {
    'Content-Type': 'application/json'
  },
  withCredentials: false
});

axiosConfig.interceptors.request.use(
  config => {
    console.log('Request Config:', {
      url: config.url,
      method: config.method,
      headers: config.headers,
      data: config.data
    });
    return config;
  },
  error => {
    console.error('Request Error:', error);
    return Promise.reject(error);
  }
);

axiosConfig.interceptors.response.use(
  response => {
    console.log('Response:', response);
    return response;
  },
  error => {
    console.error('Response Error:', {
      message: error.message,
      config: error.config,
      response: error.response
    });
    return Promise.reject(error);
  }
);

export default axiosConfig;
