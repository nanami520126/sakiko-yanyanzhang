// utils/request.js —— 封装 wx.request，自动带 baseUrl 和 token
function request(options) {
  const app = getApp()
  return new Promise((resolve, reject) => {
    wx.request({
      url: app.globalData.baseUrl + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        authentication: app.globalData.token || ''
      },
      success(res) {
        if (res.statusCode === 200 && res.data && res.data.code === 1) {
          resolve(res.data)
        } else if (res.statusCode === 401) {
          app.globalData.token = ''
          wx.removeStorageSync('token')
          wx.removeStorageSync('userInfo')
          wx.showToast({ title: '请先登录', icon: 'none' })
          if (app.notifyLoginRequired) app.notifyLoginRequired()
          reject(res.data)
        } else {
          wx.showToast({ title: (res.data && res.data.msg) || '请求失败', icon: 'none' })
          reject(res.data)
        }
      },
      fail(err) {
        wx.showToast({ title: '网络错误', icon: 'none' })
        reject(err)
      }
    })
  })
}

module.exports = {
  request,
  get(url, data) {
    return request({ url, method: 'GET', data })
  },
  post(url, data) {
    return request({ url, method: 'POST', data })
  },
  put(url, data) {
    return request({ url, method: 'PUT', data })
  },
  del(url, data) {
    return request({ url, method: 'DELETE', data })
  }
}
