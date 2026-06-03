import request from '@/utils/request'

export const getCaptcha = () => request.get('/auth/captcha')

export const login = (data: any) => request.post('/auth/login', data)

export const logout = () => request.post('/auth/logout')

export const getInfo = () => request.get('/auth/info')

export const getRoutes = () => request.get('/auth/routes')
