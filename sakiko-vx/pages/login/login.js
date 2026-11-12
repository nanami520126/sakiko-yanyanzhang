// pages/login/login.js
Page({
  data: {
    loading: false
  },

  // 微信一键登录
  handleLogin() {
    if (this.data.loading) return
    this.setData({ loading: true })

    // 1. 获取临时登录凭证 code
    wx.login({
      success: (res) => {
        if (!res.code) {
          this.setData({ loading: false })
          wx.showToast({ title: '获取微信凭证失败', icon: 'none' })
          return
        }
        this.doLogin(res.code)
      },
      fail: () => {
        this.setData({ loading: false })
        wx.showToast({ title: '微信登录失败', icon: 'none' })
      }
    })
  },

  // 2. 把 code 发给后端，换取 token 和用户信息
  doLogin(code) {
    const app = getApp()
    wx.request({
      url: app.globalData.baseUrl + '/user/user/login',
      method: 'POST',
      data: { code },
      header: { 'Content-Type': 'application/json' },
      success: (res) => {
        if (res.statusCode === 200 && res.data && res.data.code === 1) {
          const data = res.data.data
          app.globalData.token = data.token
          app.globalData.userInfo = data
          wx.setStorageSync('token', data.token)
          wx.setStorageSync('userInfo', data)
          wx.showToast({ title: '登录成功', icon: 'success' })
          setTimeout(() => {
            wx.reLaunch({ url: '/pages/index/index' })
          }, 500)
        } else {
          wx.showToast({ title: (res.data && res.data.msg) || '登录失败', icon: 'none' })
        }
      },
      fail: () => {
        wx.showToast({ title: '网络错误', icon: 'none' })
      },
      complete: () => {
        this.setData({ loading: false })
      }
    })
  }
})
