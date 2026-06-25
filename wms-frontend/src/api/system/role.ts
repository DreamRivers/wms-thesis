import request from '@/utils/request'

export const pageRole = (params: any) => request.get('/system/role/page', { params })
export const listAllRoles = () => request.get('/system/role/listAll')
export const addRole = (data: any) => request.post('/system/role', data)
export const updateRole = (data: any) => request.put('/system/role', data)
export const deleteRole = (ids: number[]) => request.delete(`/system/role/${ids.join(',')}`)

export const getRoleMenus = (roleId: number) => request.get(`/system/role/menus/${roleId}`)
export const assignRoleMenus = (data: { roleId: number, menuIds: number[] }) => request.post('/system/role/assignMenus', data)
