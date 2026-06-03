import request from '@/utils/request'

export const pageStock = (params: any) => request.get('/stock/list/page', { params })
export const pageRecord = (params: any) => request.get('/stock/record/page', { params })

export const pageTaking = (params: any) => request.get('/stock/taking/page', { params })
export const getTaking = (id: number) => request.get(`/stock/taking/${id}`)
export const createTaking = (data: any) => request.post('/stock/taking/create', data)
export const recordTaking = (takeId: number, items: any) => request.post(`/stock/taking/record/${takeId}`, items)
export const adjustTaking = (takeId: number) => request.post(`/stock/taking/adjust/${takeId}`)

export const pageNotice = (params: any) => request.get('/stock/notice/page', { params })
export const unreadCount = () => request.get('/stock/notice/unreadCount')
export const readNotice = (id: number) => request.post(`/stock/notice/read/${id}`)
export const readAll = () => request.post('/stock/notice/readAll')
