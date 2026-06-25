import request from '@/utils/request'

export const pageWarehouse = (params: any) => request.get('/basic/warehouse/page', { params })
export const listAllWarehouses = () => request.get('/basic/warehouse/listAll')
export const addWarehouse = (data: any) => request.post('/basic/warehouse', data)
export const updateWarehouse = (data: any) => request.put('/basic/warehouse', data)
export const deleteWarehouse = (ids: number[]) => request.delete(`/basic/warehouse/${ids.join(',')}`)

// Location
export const pageLocations = (params: any) => request.get('/basic/location/page', { params })
export const listLocationsByWarehouse = (id: number) => request.get(`/basic/location/listByWarehouse/${id}`)
export const addLocation = (data: any) => request.post('/basic/location', data)
export const updateLocation = (data: any) => request.put('/basic/location', data)
export const deleteLocation = (ids: number[]) => request.delete(`/basic/location/${ids.join(',')}`)
