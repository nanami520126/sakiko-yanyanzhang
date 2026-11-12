import request from '@/utils/request'

// 分页查询订单
export function pageQuery(params) {
  return request.get('/admin/order/page', { params })
}

// 各状态订单数量统计
export function orderStatistics() {
  return request.get('/admin/order/statistics')
}

// 订单详情
export function getOrderDetail(id) {
  return request.get(`/admin/order/details/${id}`)
}

// 接单
export function confirmOrder(data) {
  return request.put('/admin/order/confirm', data)
}

// 拒单
export function rejectionOrder(data) {
  return request.put('/admin/order/rejection', data)
}

// 取消订单
export function cancelOrder(data) {
  return request.put('/admin/order/cancel', data)
}

// 完成订单
export function completeOrder(id) {
  return request.put(`/admin/order/complete/${id}`)
}
