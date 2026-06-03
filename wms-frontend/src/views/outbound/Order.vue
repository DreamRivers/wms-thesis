<template>
  <el-card>
    <h3>📋 出库单列表</h3>
    <el-form :inline="true">
      <el-form-item><el-input v-model="query.orderNo" placeholder="单号" clearable /></el-form-item>
      <el-form-item>
        <el-select v-model="query.status" placeholder="状态" clearable>
          <el-option label="已申请" value="APPLY" />
          <el-option label="审批中" value="APPROVING" />
          <el-option label="已审核" value="APPROVED" />
          <el-option label="已发货" value="SHIPPED" />
          <el-option label="已完成" value="FINISHED" />
          <el-option label="已驳回" value="REJECTED" />
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" @click="loadData">查询</el-button></el-form-item>
    </el-form>
    <el-table :data="list" border>
      <el-table-column prop="orderNo" label="单号" width="220" />
      <el-table-column prop="applicantId" label="申请人" width="80" />
      <el-table-column prop="warehouseId" label="仓库" width="80" />
      <el-table-column prop="totalQty" label="数量" width="80" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusTag(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="applyReason" label="出库原因" />
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button v-if="row.status === 'APPROVED'" size="small" type="success" @click="onShip(row)">发货出库</el-button>
          <el-button v-if="row.status === 'SHIPPED'" size="small" type="primary" @click="onComplete(row)">完成</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total"
      layout="total,prev,pager,next" @current-change="loadData" style="margin-top:16px" />
  </el-card>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageOutbound, shipOutbound, completeOutbound } from '@/api/outbound/order'

const query = reactive({ pageNum: 1, pageSize: 10, orderNo: '', status: '' })
const list = ref<any[]>([])
const total = ref(0)

const MAP: Record<string, [string, string]> = {
  APPLY: ['已申请', 'info'], APPROVING: ['审批中', 'warning'], APPROVED: ['已审核', 'primary'],
  SHIPPED: ['已发货', 'success'], FINISHED: ['已完成', 'success'], REJECTED: ['已驳回', 'danger']
}
const statusText = (s: string) => MAP[s]?.[0] || s
const statusTag = (s: string): any => MAP[s]?.[1] || 'info'

async function loadData() {
  const res: any = await pageOutbound(query)
  list.value = res.data.list
  total.value = res.data.total
}

async function onShip(row: any) {
  await ElMessageBox.confirm(`确认发货出库 ${row.orderNo}?将扣减库存`, '提示', { type: 'warning' })
  await shipOutbound(row.id)
  ElMessage.success('已发货,库存已扣减')
  loadData()
}

async function onComplete(row: any) {
  await completeOutbound(row.id)
  ElMessage.success('已完成')
  loadData()
}

onMounted(loadData)
</script>
