import request from '@/utils/request'

export const pageOutbound = (params: any) => request.get('/outbound/order/page', { params })
export const getOutbound = (id: number) => request.get(`/outbound/order/${id}`)
export const applyOutbound = (data: any) => request.post('/outbound/order/apply', data)
export const handleApproval = (data: any) => request.post('/outbound/order/approval/handle', data)
export const shipOutbound = (id: number) => request.post(`/outbound/order/ship/${id}`)
export const completeOutbound = (id: number) => request.post(`/outbound/order/complete/${id}`)
export const cancelOutbound = (id: number) => request.post(`/outbound/order/cancel/${id}`)
