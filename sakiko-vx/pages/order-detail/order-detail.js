// pages/order-detail/order-detail.js
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
    id: null,
    order: null
  },

  onLoad(options) {
    this.setData({ id: options.id })
  },

  onShow() {
    if (this.data.id) this.fetch()
  },

  fetch() {
    api.getOrderDetail(this.data.id).then((res) => {
      const o = res.data || {}
      const s = STATUS[o.status] || { text: '未知', cls: '' }
      const items = (o.orderDetailList || []).map((it) => Object.assign({}, it, {
        amount: fmt(it.amount)
      }))
      this.setData({
        order: Object.assign({}, o, {
          statusText: s.text,
          statusCls: s.cls,
          amount: fmt(o.amount),
          packAmount: fmt(o.packAmount),
          orderDetailList: items,
          canCancel: o.status === 1 || o.status === 2,
          canRepetition: o.status === 5 || o.status === 6
        })
      })
    })
  },

  cancelOrder() {
    wx.showModal({
      title: '取消订单',
      content: '确定要取消这笔订单吗？',
      success: (r) => {
        if (r.confirm) {
          api.cancelOrder(this.data.id).then(() => {
            wx.showToast({ title: '已取消', icon: 'success' })
            this.fetch()
          })
        }
      }
    })
  },

  repetition() {
    api.repetitionOrder(this.data.id).then(() => {
      wx.showToast({ title: '已加入购物车', icon: 'success' })
      setTimeout(() => wx.reLaunch({ url: '/pages/index/index' }), 600)
    })
  }
})
