// pages/address/address.js
const api = require('../../utils/api')

Page({
  data: {
    list: [],
    selectMode: false
  },

  onLoad(options) {
    this.setData({ selectMode: options.select === '1' })
  },

  onShow() {
    this.fetchList()
  },

  fetchList() {
    api.listAddress().then((res) => {
      this.setData({ list: res.data || [] })
    })
  },

  // 结算页选择地址
  selectAddress(e) {
    if (!this.data.selectMode) return
    const item = this.data.list[e.currentTarget.dataset.index]
    wx.setStorageSync('checkoutAddressId', item.id)
    wx.navigateBack()
  },

  addAddress() {
    wx.navigateTo({ url: '/pages/address-edit/address-edit' })
  },

  editAddress(e) {
    const item = this.data.list[e.currentTarget.dataset.index]
    wx.navigateTo({ url: '/pages/address-edit/address-edit?id=' + item.id })
  },

  setDefault(e) {
    const item = this.data.list[e.currentTarget.dataset.index]
    api.setDefaultAddress({ id: item.id, userId: item.userId }).then(() => {
      wx.showToast({ title: '已设为默认', icon: 'success' })
      this.fetchList()
    })
  },

  deleteAddress(e) {
    const item = this.data.list[e.currentTarget.dataset.index]
    wx.showModal({
      title: '删除地址',
      content: '确定删除该地址吗？',
      success: (res) => {
        if (res.confirm) {
          api.deleteAddress(item.id).then(() => {
            wx.showToast({ title: '已删除', icon: 'success' })
            this.fetchList()
          })
        }
      }
    })
  }
})
