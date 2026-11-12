import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    id: null,
    userName: '',
    name: '',
    token: localStorage.getItem('token') || ''
  }),
  actions: {
    setLoginInfo(data) {
      this.id = data.id
      this.userName = data.userName
      this.name = data.name
      this.token = data.token
      localStorage.setItem('token', data.token)
    },
    logout() {
      this.id = null
      this.userName = ''
      this.name = ''
      this.token = ''
      localStorage.removeItem('token')
    }
  }
})
