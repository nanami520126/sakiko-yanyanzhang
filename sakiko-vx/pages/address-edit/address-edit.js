// pages/address-edit/address-edit.js
const api = require('../../utils/api')

Page({
  data: {
    id: null,
    form: {
      consignee: '',
      phone: '',
      sex: '1',
      region: ['北京市', '北京市', '东城区'],
      detail: '',
      label: ''
    }
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ id: options.id })
      wx.setNavigationBarTitle({ title: '编辑地址' })
      api.getAddressById(options.id).then((res) => {
        const a = res.data || {}
        this.setData({
          form: {
            consignee: a.consignee || '',
            phone: a.phone || '',
            sex: a.sex || '1',
            region: [a.provinceName, a.cityName, a.districtName].filter(Boolean),
            detail: a.detail || '',
            label: a.label || ''
          }
        })
      })
    }
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field
    this.setData({ [`form.${field}`]: e.detail.value })
  },

  onSex(e) {
    this.setData({ 'form.sex': e.currentTarget.dataset.value })
  },

  onRegion(e) {
    this.setData({ 'form.region': e.detail.value })
  },

  save() {
    const f = this.data.form
    if (!f.consignee.trim()) {
      wx.showToast({ title: '请填写收货人', icon: 'none' })
      return
    }
    if (!/^1\d{10}$/.test(f.phone)) {
      wx.showToast({ title: '手机号格式不正确', icon: 'none' })
      return
    }

    const data = {
      id: this.data.id,
      consignee: f.consignee,
      phone: f.phone,
      sex: f.sex,
      provinceName: f.region[0],
      cityName: f.region[1],
      districtName: f.region[2],
      detail: f.detail,
      label: f.label
    }

    const req = this.data.id ? api.updateAddress(data) : api.addAddress(data)
    req.then(() => {
      wx.showToast({ title: '保存成功', icon: 'success' })
      setTimeout(() => wx.navigateBack(), 500)
    })
  }
})
