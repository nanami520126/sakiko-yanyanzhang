import request from '@/utils/request'

// 上传图片到 OSS，返回图片 URL
export function uploadImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/admin/common/upload', formData)
}
