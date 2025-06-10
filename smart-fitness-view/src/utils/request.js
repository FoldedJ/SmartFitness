import axios from "axios"
import { getToken } from "@/utils/storage.js";
const URL_API = 'http://localhost:21090/api/smart-fitness/v1.0'
const request = axios.create({
  baseURL: URL_API,
  timeout: 30000  // 将超时时间从8000毫秒增加到30000毫秒（30秒）
});
//全局拦截器
request.interceptors.request.use(config => {
  const token = getToken();
  if (token !== null) {
    config.headers["token"] = token;
  }
  return config;
}, error => {
  return Promise.reject(error);
});
export default request;