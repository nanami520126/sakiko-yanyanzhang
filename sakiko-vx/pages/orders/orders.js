// pages/orders/orders.js
const app = getApp()
const api = require('../../utils/api')

const STATUS = {
  1: { text: '待付款', cls: 'wait' },
  2: { text: '待接单', cls: 'process' },
  3: { text: '已接单', cls: 'process' },
  4: { text: '派送中', cls: 'process' },
  5: { text: '已完成', cls: 'done' },
  6: { text: '已取消', cls: 'cancelled' }
}

function fmt(n) {
  return n == null ? '0.00' : Number(n).toFixed(2)
}

Page({
  data: {
    statusBarHeight: 20,
    tab: 'orders',
    tabs: [
      { label: '全部', status: null },
      { label: '待付款', status: 1 },
      { label: '待接单', status: 2 },
      { label: '已接单', status: 3 },
      { label: '派送中', status: 4 },
      { label: '已完成', status: 5 },
      { label: '已取消', status: 6 }
    ],
    activeTab: 0,
    orders: [],
    page: 1,
    pageSize: 10,
    total: 0,
    hasMore: false,
    loading: false,
    needLogin: false,
    loggingIn: false
  },

  onLoad() {
    const info = wx.getWindowInfo ? wx.getWindowInfo() : wx.getSystemInfoSync()
    this.setData({ statusBarHeight: info.statusBarHeight || 20 })
  },

  onTabTap(e) {
    const path = e.currentTarget.dataset.path
    const pages = getCurrentPages()
    const current = pages[pages.length - 1]
    if (current && current.route === path.replace(/^\//, '')) return
    wx.reLaunch({ url: path })
  },

  onShow() {
    if (!app.isLoggedIn()) {
      this.setData({ needLogin: true, orders: [] })
      return
    }
    this.setData({ needLogin: false })
    this.reload()
  },

  switchTab(e) {
    const index = e.currentTarget.dataset.index
    if (index === this.data.activeTab) return
    this.setData({ activeTab: index, orders: [], page: 1, total: 0, hasMore: false })
    this.reload()
  },

  reload() {
    this.setData({ page: 1 })
    this.fetch()
  },

  fetch() {
    if (this.data.loading) return
    const status = this.data.tabs[this.data.activeTab].status
    this.setData({ loading: true })
    api.listOrders(this.data.page, this.data.pageSize, status).then((res) => {
      const data = res.data || {}
      const records = (data.records || []).map((o) => this.decorate(o))
      const orders = this.data.page === 1 ? records : this.data.orders.concat(records)
      const total = data.total || 0
      this.setData({
        orders,
        total,
        hasMore: orders.length < total,
        loading: false
      })
    }).catch(() => {
      this.setData({ loading: false })
      if (!app.isLoggedIn()) this.setData({ needLogin: true })
    })
  },

  decorate(o) {
    const s = STATUS[o.status] || { text: '未知', cls: '' }
    const orderDishes = (o.orderDishes || '')
      .split(';')
      .filter(Boolean)
      .join('  ·  ')
    return Object.assign({}, o, {
      statusText: s.text,
      statusCls: s.cls,
      orderDishes,
      amount: fmt(o.amount),
      canCancel: o.status === 1 || o.status === 2,
      canRepetition: o.status === 5 || o.status === 6
    })
  },

  loadMore() {
    if (this.data.loading || !this.data.hasMore) return
    this.setData({ page: this.data.page + 1 })
    this.fetch()
  },

  openDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/order-detail/order-detail?id=' + id })
  },

  cancelOrder(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '取消订单',
      content: '确定要取消这笔订单吗？',
      success: (r) => {
        if (r.confirm) {
          api.cancelOrder(id).then(() => {
            wx.showToast({ title: '已取消', icon: 'success' })
            this.reload()
          })
        }
      }
    })
  },

  repetition(e) {
    const id = e.currentTarget.dataset.id
    api.repetitionOrder(id).then(() => {
      wx.showToast({ title: '已加入购物车', icon: 'success' })
      setTimeout(() => wx.reLaunch({ url: '/pages/index/index' }), 600)
    })
  },

  doLogin() {
    if (this.data.loggingIn) return
    this.setData({ loggingIn: true })
    wx.login({
      success: (res) => {
        if (!res.code) {
          this.setData({ loggingIn: false })
          wx.showToast({ title: '获取凭证失败', icon: 'none' })
          return
        }
        api.login(res.code).then((r) => {
          app.setLogin(r.data)
          this.setData({ loggingIn: false, needLogin: false })
          wx.showToast({ title: '登录成功', icon: 'success' })
          this.reload()
        }).catch(() => this.setData({ loggingIn: false }))
      },
      fail: () => {
        this.setData({ loggingIn: false })
        wx.showToast({ title: '微信登录失败', icon: 'none' })
      }
    })
  }
})
