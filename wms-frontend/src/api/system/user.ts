import request from '@/utils/request'

export const pageUser = (params: any) => request.get('/system/user/page', { params })
export const addUser = (data: any) => request.post('/system/user', data)
export const updateUser = (data: any) => request.put('/system/user', data)
export const deleteUser = (ids: number[]) => request.delete(`/system/user/${ids.join(',')}`)
export const resetPwd = (id: number) => request.post(`/system/user/resetPwd/${id}`)
export const changeStatus = (id: number, status: number) => request.post(`/system/user/changeStatus/${id}`, null, { params: { status } })
export const changeMyPassword = (data: { oldPassword: string, newPassword: string }) => request.post('/system/user/changeMyPassword', data)
