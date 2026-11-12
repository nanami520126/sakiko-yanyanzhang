import request from '@/utils/request'

// 分页查询菜品
export function pageQuery(params) {
  return request.get('/admin/dish/page', { params })
}

// 新增菜品（含口味）
export function addDish(data) {
  return request.post('/admin/dish', data)
}

// 编辑菜品（含口味）
export function updateDish(data) {
  return request.put('/admin/dish', data)
}

// 批量删除菜品（ids 用逗号分隔）
export function deleteDish(ids) {
  return request.delete('/admin/dish', { params: { ids } })
}

// 根据 id 查询菜品（含口味）
export function getDishById(id) {
  return request.get(`/admin/dish/id/${id}`)
}

// 起售 / 停售菜品（status: 1 起售，0 停售）
export function setDishStatus(status, id) {
  return request.post(`/admin/dish/status/${status}`, null, { params: { id } })
}

// 根据分类id查询菜品（起售中）
export function listDish(categoryId) {
  return request.get('/admin/dish/list', { params: { categoryId } })
}
