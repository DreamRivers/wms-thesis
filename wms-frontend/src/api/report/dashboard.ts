import request from '@/utils/request'

export const getDashboard = () => request.get('/report/dashboard')
export const getInboundTrend = (params?: any) => request.get('/report/inbound/trend', { params })
export const getOutboundTrend = (params?: any) => request.get('/report/outbound/trend', { params })
export const getInventoryValue = () => request.get('/report/inventory/value')
export const getTopGoods = (params?: any) => request.get('/report/topGoods', { params })
