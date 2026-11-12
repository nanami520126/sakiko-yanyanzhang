import request from '@/utils/request'

// 新增分类
export function addCategory(data) {
  return request.post('/admin/category', data)
}

// 分页查询分类
export function pageQuery(params) {
  return request.get('/admin/category/page', { params })
}

// 删除分类
export function deleteCategory(id) {
  return request.delete('/admin/category', { params: { id } })
}

// 编辑分类
export function updateCategory(data) {
  return request.put('/admin/category', data)
}

// 启用 / 禁用分类（status: 1 启用，0 禁用）
export function setCategoryStatus(status, id) {
  return request.post(`/admin/category/status/${status}`, null, { params: { id } })
}

// 根据类型查询分类列表
export function listCategory(type) {
  return request.get('/admin/category/list', { params: { type } })
}
