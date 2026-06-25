import request from '@/utils/request'

// 供应商列表分页（用于 join 字典）
export const pageSuppliers = (params: any) => request.get('/basic/supplier/page', { params })
