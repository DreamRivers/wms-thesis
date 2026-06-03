import request from '@/utils/request'

export const pageGoods = (params: any) => request.get('/basic/goods/page', { params })
export const listAllGoods = () => request.get('/basic/goods/listAll')
export const addGoods = (data: any) => request.post('/basic/goods', data)
export const updateGoods = (data: any) => request.put('/basic/goods', data)
export const deleteGoods = (ids: number[]) => request.delete(`/basic/goods/${ids.join(',')}`)

export const pageCategory = () => request.get('/basic/category/list')
export const listAllWarehouses = () => request.get('/basic/warehouse/listAll')
export const pageLocations = (params: any) => request.get('/basic/location/page', { params })
export const listLocationsByWarehouse = (id: number) => request.get(`/basic/location/listByWarehouse/${id}`)
export const listAllSuppliers = () => request.get('/basic/supplier/listAll')
