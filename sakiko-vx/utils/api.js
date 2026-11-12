// utils/api.js —— C 端接口
const { get, post, put, del } = require('./request')

// 店铺营业状态
const getShopStatus = () => get('/user/shop/status')
// 分类列表（type: 1 菜品分类 / 2 套餐分类）
const getCategoryList = (type) => get('/user/category/list', { type })
// 分类下的菜品（含口味）
const getDishList = (categoryId) => get('/user/dish/list', { categoryId })
// 分类下的套餐
const getSetmealList = (categoryId) => get('/user/setmeal/list', { categoryId })
// 套餐包含的菜品
const getSetmealDishes = (id) => get(`/user/setmeal/dish/${id}`)
// 微信登录
const login = (code) => post('/user/user/login', { code })

// —— 购物车 ——
const addShoppingCart = (data) => post('/user/shoppingCart/add', data)
const listShoppingCart = () => get('/user/shoppingCart/list')
const cleanShoppingCart = () => del('/user/shoppingCart/clean')

// —— 地址簿 ——
const addAddress = (data) => post('/user/addressBook/add', data)
const listAddress = () => get('/user/addressBook/list')
const getDefaultAddress = () => get('/user/addressBook/default')
const updateAddress = (data) => put('/user/addressBook', data)
const deleteAddress = (id) => del(`/user/addressBook?id=${id}`)
const setDefaultAddress = (data) => put('/user/addressBook/default', data)
const getAddressById = (id) => get(`/user/addressBook/${id}`)

// —— 下单 ——
const submitOrder = (data) => post('/user/order/submit', data)

// —— 订单 ——
// 历史订单（status 为空查全部；page/pageSize 走 query 参数）
const listOrders = (page, pageSize, status) => {
  let qs = `page=${page}&pageSize=${pageSize}`
  if (status !== null && status !== undefined) qs += `&status=${status}`
  return post(`/user/order/list?${qs}`)
}
const getOrderDetail = (id) => get(`/user/order/orderDetail/${id}`)
const cancelOrder = (id) => put(`/user/order/cancel/${id}`)
const repetitionOrder = (id) => post(`/user/order/repetition/${id}`)

module.exports = {
  getShopStatus,
  getCategoryList,
  getDishList,
  getSetmealList,
  getSetmealDishes,
  login,
  addShoppingCart,
  listShoppingCart,
  cleanShoppingCart,
  addAddress,
  listAddress,
  getDefaultAddress,
  updateAddress,
  deleteAddress,
  setDefaultAddress,
  getAddressById,
  submitOrder,
  listOrders,
  getOrderDetail,
  cancelOrder,
  repetitionOrder
}
