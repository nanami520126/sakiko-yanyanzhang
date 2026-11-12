// pages/checkout/checkout.js
const api = require('../../utils/api')

Page({
  data: {
    address: null,
    cartItems: [],
    remark: '',
    payMethod: 1,       // 1 微信支付
    goodsAmount: '0.00',
    packAmount: 0,
    totalAmount: '0.00',
    submitting: false
  },

  onShow() {
    this.fetchAddress()
    this.fetchCart()
  },

  fetchAddress() {
    const selectedId = wx.getStorageSync('checkoutAddressId')
    if (selectedId) {
      api.getAddressById(selectedId).then((res) => {
        this.setData({ address: res.data || null })
      }).catch(() => this.fetchDefault())
    } else {
      this.fetchDefault()
    }
  },

  fetchDefault() {
    api.getDefaultAddress().then((res) => {
      this.setData({ address: res.data || null })
    }).catch(() => {
      this.setData({ address: null })
    })
  },

  fetchCart() {
    api.listShoppingCart().then((res) => {
      const items = res.data || []
      const goods = items.reduce((s, c) => s + c.amount * c.number, 0)
      const pack = items.length
      this.setData({
        cartItems: items,
        goodsAmount: goods.toFixed(2),
        packAmount: pack,
        totalAmount: (goods + pack).toFixed(2)
      })
    })
  },

  selectAddress() {
    wx.navigateTo({ url: '/pages/address/address?select=1' })
  },

  onRemark(e) {
    this.setData({ remark: e.detail.value })
  },

  submit() {
    if (!this.data.address) {
      wx.showToast({ title: '请先选择收货地址', icon: 'none' })
      return
    }
    if (this.data.cartItems.length === 0) {
      wx.showToast({ title: '购物车是空的', icon: 'none' })
      return
    }
    this.setData({ submitting: true })
    api.submitOrder({
      addressBookId: this.data.address.id,
      payMethod: this.data.payMethod,
      remark: this.data.remark,
      deliveryStatus: 1,
      tablewareStatus: 1,
      tablewareNumber: 0,
      packAmount: this.data.packAmount,
      amount: Number(this.data.totalAmount)
    }).then((res) => {
      this.setData({ submitting: false })
      wx.removeStorageSync('checkoutAddressId')
      wx.showModal({
        title: '下单成功',
        content: '订单号：' + res.data.orderNumber,
        showCancel: false,
        success: () => wx.navigateBack()
      })
    }).catch(() => this.setData({ submitting: false }))
  }
})
