import request from '@/utils/request'

// 分页查询套餐
export function pageQuery(params) {
  return request.get('/admin/setmeal/page', { params })
}

// 新增套餐（含菜品关系）
export function addSetmeal(data) {
  return request.post('/admin/setmeal/save', data)
}

// 编辑套餐（含菜品关系）
export function updateSetmeal(data) {
  return request.put('/admin/setmeal', data)
}

// 批量删除套餐（ids 用逗号分隔）
export function deleteSetmeal(ids) {
  return request.delete('/admin/setmeal', { params: { ids } })
}

// 根据 id 查询套餐（含菜品关系）
export function getSetmealById(id) {
  return request.get(`/admin/setmeal/id/${id}`)
}

// 起售 / 停售套餐（status: 1 起售，0 停售）
export function setSetmealStatus(status, id) {
  return request.post(`/admin/setmeal/status/${status}`, null, { params: { id } })
}
