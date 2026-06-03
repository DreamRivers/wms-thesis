import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, logout, getInfo, getRoutes } from '@/api/system/login'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<any>({})
  const roles = ref<string[]>([])
  const routes = ref<any[]>([])

  async function doLogin(data: any) {
    const res: any = await login(data)
    token.value = res.data.token
    localStorage.setItem('token', res.data.token)
    userInfo.value = res.data
    roles.value = res.data.roles || []
    return res
  }

  async function doLogout() {
    await logout()
    token.value = ''
    userInfo.value = {}
    roles.value = []
    localStorage.clear()
  }

  async function loadInfo() {
    const res: any = await getInfo()
    userInfo.value = res.data
    roles.value = res.data.roles || []
  }

  async function loadRoutes() {
    const res: any = await getRoutes()
    routes.value = res.data || []
  }

  return { token, userInfo, roles, routes, doLogin, doLogout, loadInfo, loadRoutes }
})
