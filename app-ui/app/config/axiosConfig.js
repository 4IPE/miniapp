import axios from 'axios';

const axiosConfig = axios.create({
  baseURL: 'https://ruby-tunnel.ru/api',
});
export default axiosConfig;
