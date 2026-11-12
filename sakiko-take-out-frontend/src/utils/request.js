import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 统一的 axios 实例
const request = axios.create({
  baseURL: '',
  timeout: 10000
})

// 请求拦截器：自动携带 token（后端约定的 header 名是 token）
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['token'] = token
  }
  return config
})

// 响应拦截器：统一解包后端 Result{code, msg, data}
request.interceptors.response.use(
  response => {
    const res = response.data
    // code === 1 表示业务成功
    if (res.code === 1) {
      return res
    }
    // 业务失败
    ElMessage.error(res.msg || '操作失败')
    return Promise.reject(new Error(res.msg || '操作失败'))
  },
  error => {
    // HTTP 层错误，例如后端 jwt 拦截器返回的 401
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token')
      ElMessage.error('登录已过期，请重新登录')
      router.push('/login')
    } else {
      ElMessage.error(error.message || '请求失败')
    }
    return Promise.reject(error)
  }
)

export default request
