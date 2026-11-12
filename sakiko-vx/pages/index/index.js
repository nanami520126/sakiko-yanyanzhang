// pages/index/index.js
const app = getApp()
const api = require('../../utils/api')

// 解析口味值：兼容 JSON 数组字符串、逗号分隔字符串，以及全角/半角的引号、方括号
function parseFlavorValues(raw) {
  if (!raw) return []
  try {
    const arr = JSON.parse(raw)
    return Array.isArray(arr) ? arr : [arr]
  } catch (e) {
    return String(raw)
      .replace(/[\[\]"'“”‘’【】]/g, '')
      .split(/[,，]/)
      .map((s) => s.trim())
      .filter(Boolean)
  }
}

Page({
  data: {
    statusBarHeight: 20,
    tab: 'index',
    shopStatus: 1,
    categories: [],
    activeCategoryId: null,
    items: [],
    loading: false,

    cartCount: 0,
    cartTotal: '0.00',
    cartVisible: false,
    cartItems: [],

    detailVisible: false,
    detail: null,
    flavorGroups: [],
    selectedFlavors: {},
    detailDishes: [],

    loginVisible: false,
    loginLoading: false,
    pendingAction: ''
  },

  onLoad() {
    const info = wx.getWindowInfo ? wx.getWindowInfo() : wx.getSystemInfoSync()
    this.setData({ statusBarHeight: info.statusBarHeight || 20 })
    // 登录失效时自动弹登录
    app.onLoginRequired(() => {
      this.setData({ loginVisible: true, pendingAction: '' })
    })
    this.init()
  },

  onShow() {
    this.refreshCart()
  },

  init() {
    this.fetchShopStatus()
    this.fetchCategories()
  },

  fetchShopStatus() {
    api.getShopStatus().then((res) => {
      this.setData({ shopStatus: res.data === 1 ? 1 : 0 })
    }).catch(() => {})
  },

  fetchCategories() {
    Promise.all([api.getCategoryList(1), api.getCategoryList(2)])
      .then(([dishRes, setmealRes]) => {
        const dishCats = (dishRes.data || []).map((c) => ({ id: c.id, name: c.name, type: 1 }))
        const setmealCats = (setmealRes.data || []).map((c) => ({ id: c.id, name: c.name, type: 2 }))
        const categories = dishCats.concat(setmealCats)
        this.setData({ categories })
        if (categories.length) this.selectCategory(categories[0])
      })
      .catch(() => {})
  },

  selectCategory(e) {
    const cat = this.data.categories[e.currentTarget.dataset.index]
    if (!cat) return
    this.setData({ activeCategoryId: cat.id })
    if (cat.type === 1) this.fetchDishes(cat.id)
    else this.fetchSetmeals(cat.id)
  },

  fetchDishes(categoryId) {
    this.setData({ loading: true })
    api.getDishList(categoryId).then((res) => {
      const items = (res.data || []).map((d) => Object.assign({}, d, { kind: 'dish' }))
      this.setData({ items, loading: false })
    }).catch(() => this.setData({ loading: false }))
  },

  fetchSetmeals(categoryId) {
    this.setData({ loading: true })
    api.getSetmealList(categoryId).then((res) => {
      const items = (res.data || []).map((s) => Object.assign({}, s, { kind: 'setmeal' }))
      this.setData({ items, loading: false })
    }).catch(() => this.setData({ loading: false }))
  },

  openDetail(e) {
    const item = this.data.items[e.currentTarget.dataset.index]
    if (!item) return
    const flavorGroups = (item.kind === 'dish' && item.flavors)
      ? item.flavors.map((f) => ({ name: f.name, values: parseFlavorValues(f.value) }))
      : []
    this.setData({ detailVisible: true, detail: item, flavorGroups, selectedFlavors: {}, detailDishes: [] })
    if (item.kind === 'setmeal') {
      api.getSetmealDishes(item.id).then((res) => {
        this.setData({ detailDishes: res.data || [] })
      }).catch(() => {})
    }
  },

  closeDetail() {
    this.setData({ detailVisible: false, detail: null })
  },

  selectFlavor(e) {
    const { name, value } = e.currentTarget.dataset
    this.setData({ [`selectedFlavors.${name}`]: value })
  },

  // —— 购物车 ——
  addToCart() {
    if (!app.isLoggedIn()) {
      this.setData({ loginVisible: true, pendingAction: 'addToCart' })
      return
    }
    this.doAddToCart()
  },

  doAddToCart() {
    const d = this.data.detail
    const flavor = Object.values(this.data.selectedFlavors).join(',')
    const data = d.kind === 'dish' ? { dishId: d.id, dishFlavor: flavor } : { setmealId: d.id }
    api.addShoppingCart(data).then(() => {
      this.refreshCart()
      this.closeDetail()
      wx.showToast({ title: '已加入', icon: 'success' })
    })
  },

  openCart() {
    if (!app.isLoggedIn()) {
      this.setData({ loginVisible: true, pendingAction: 'openCart' })
      return
    }
    this.doOpenCart()
  },

  doOpenCart() {
    this.refreshCart()
    this.setData({ cartVisible: true })
  },

  closeCart() {
    this.setData({ cartVisible: false })
  },

  incItem(e) {
    const item = this.data.cartItems[e.currentTarget.dataset.index]
    if (!item) return
    const data = item.dishId
      ? { dishId: item.dishId, dishFlavor: item.dishFlavor }
      : { setmealId: item.setmealId }
    api.addShoppingCart(data).then(() => this.refreshCart())
  },

  clearCart() {
    api.cleanShoppingCart().then(() => this.refreshCart())
  },

  checkout() {
    if (!app.isLoggedIn()) {
      this.setData({ loginVisible: true, pendingAction: 'checkout' })
      return
    }
    this.goCheckout()
  },

  goCheckout() {
    wx.navigateTo({ url: '/pages/checkout/checkout' })
  },

  onTabTap(e) {
    const path = e.currentTarget.dataset.path
    const pages = getCurrentPages()
    const current = pages[pages.length - 1]
    if (current && current.route === path.replace(/^\//, '')) return
    wx.reLaunch({ url: path })
  },

  // —— 登录 ——
  closeLogin() {
    this.setData({ loginVisible: false, pendingAction: '' })
  },

  doLogin() {
    if (this.data.loginLoading) return
    this.setData({ loginLoading: true })
    wx.login({
      success: (res) => {
        if (!res.code) {
          this.setData({ loginLoading: false })
          wx.showToast({ title: '获取凭证失败', icon: 'none' })
          return
        }
        api.login(res.code).then((r) => {
          app.setLogin(r.data)
          const action = this.data.pendingAction
          this.setData({ loginVisible: false, loginLoading: false, pendingAction: '' })
          wx.showToast({ title: '登录成功', icon: 'success' })
          if (action === 'addToCart') this.doAddToCart()
          else if (action === 'openCart') this.doOpenCart()
          else if (action === 'checkout') this.goCheckout()
        }).catch(() => this.setData({ loginLoading: false }))
      },
      fail: () => {
        this.setData({ loginLoading: false })
        wx.showToast({ title: '微信登录失败', icon: 'none' })
      }
    })
  },

  refreshCart() {
    if (!app.isLoggedIn()) {
      this.setData({ cartCount: 0, cartTotal: '0.00', cartItems: [] })
      return
    }
    api.listShoppingCart().then((res) => {
      const items = res.data || []
      const count = items.reduce((s, c) => s + c.number, 0)
      const total = items.reduce((s, c) => s + c.amount * c.number, 0)
      this.setData({ cartItems: items, cartCount: count, cartTotal: total.toFixed(2) })
    }).catch(() => {})
  }
})
