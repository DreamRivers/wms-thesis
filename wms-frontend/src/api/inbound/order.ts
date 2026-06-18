import request from '@/utils/request'

export const pageInbound = (params: any) => request.get('/inbound/order/page', { params })
export const getInbound = (id: number) => request.get(`/inbound/order/${id}`)
export const saveInbound = (data: any) => request.post('/inbound/order/save', data)
export const submitInbound = (id: number) => request.post(`/inbound/order/submit/${id}`)
export const auditInbound = (data: any) => request.post('/inbound/order/audit', data)
export const executeInbound = (id: number, items: any) => request.post(`/inbound/order/execute/${id}`, items)
export const completeInbound = (id: number) => request.post(`/inbound/order/complete/${id}`)
  export const cancelInbound = (id: number) => request.post(`/inbound/order/cancel/${id}`)
  export const deleteInbound = (id: number) => request.delete(`/inbound/order/${id}`)
