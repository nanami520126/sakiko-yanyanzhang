// app.js
App({
  globalData: {
    // 后端地址：本地用 localhost，真机/上线换成云服务器 https 地址
    baseUrl: 'http://localhost:8080',
    token: '',
    userInfo: null
  },

  onLaunch() {
    const token = wx.getStorageSync('token')
    const userInfo = wx.getStorageSync('userInfo')
    if (token) {
      this.globalData.token = token
      this.globalData.userInfo = userInfo || null
    }
  },

  isLoggedIn() {
    return !!this.globalData.token
  },

  // 登录成功后写入全局与缓存
  setLogin(data) {
    this.globalData.token = data.token
    this.globalData.userInfo = data
    wx.setStorageSync('token', data.token)
    wx.setStorageSync('userInfo', data)
  },

  logout() {
    this.globalData.token = ''
    this.globalData.userInfo = null
    wx.removeStorageSync('token')
    wx.removeStorageSync('userInfo')
  },

  // 页面注册「登录失效」回调
  onLoginRequired(handler) {
    this._loginHandler = handler
  },

  // 由 request 层在 401 时调用，通知页面弹登录
  notifyLoginRequired() {
    if (this._loginHandler) this._loginHandler()
  }
})
