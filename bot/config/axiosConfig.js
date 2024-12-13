import axios from 'axios';
import dotenv from 'dotenv';

dotenv.config();
const BASE_URL = process.env.BASE_URL;
const axiosConfig = axios.create({
    baseURL: BASE_URL,
});
export default axiosConfig;
