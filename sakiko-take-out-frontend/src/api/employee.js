import request from '@/utils/request'

// 登录
export function login(data) {
  return request.post('/admin/employee/login', data)
}

// 退出登录
export function logout() {
  return request.post('/admin/employee/logout')
}

// 分页查询员工
export function pageQuery(params) {
  return request.get('/admin/employee/page', { params })
}

// 新增员工
export function addEmployee(data) {
  return request.post('/admin/employee', data)
}

// 编辑员工
export function updateEmployee(data) {
  return request.put('/admin/employee', data)
}

// 根据 id 查询员工
export function getEmployeeById(id) {
  return request.get(`/admin/employee/id/${id}`)
}

// 启用 / 禁用员工（status: 1 启用，0 禁用）
export function setEmployeeStatus(status, id) {
  return request.post(`/admin/employee/status/${status}`, null, { params: { id } })
}

// 批量导入员工（Excel 文件）
export function importEmployees(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/admin/employee/import', formData)
}
