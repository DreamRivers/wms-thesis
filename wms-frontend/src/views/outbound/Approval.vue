<template>
  <el-card>
    <h3>✅ 出库审批</h3>
    <el-tabs v-model="active" @tab-change="loadData">
      <el-tab-pane label="待部门审批" name="APPLY"></el-tab-pane>
      <el-tab-pane label="待仓管审批" name="APPROVING"></el-tab-pane>
    </el-tabs>
    <el-table :data="list" border>
      <el-table-column prop="orderNo" label="单号" width="220" />
      <el-table-column prop="applicantId" label="申请人" width="100" />
      <el-table-column prop="outboundType" label="类型" width="100">
        <template #default="{ row }">{{ outboundTypeText(row.outboundType) }}</template>
      </el-table-column>
      <el-table-column prop="totalQty" label="数量" width="80" />
      <el-table-column prop="applyReason" label="原因" />
      <el-table-column prop="createTime" label="申请时间" width="170" />
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button size="small" type="success" @click="onHandle(row, true)">通过</el-button>
          <el-button size="small" type="danger" @click="onHandle(row, false)">驳回</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageOutbound, handleApproval, shipOutbound } from '@/api/outbound/order'

const active = ref('APPLY')
const list = ref<any[]>([])

async function loadData() {
  const res: any = await pageOutbound({ pageNum: 1, pageSize: 50, status: active.value })
  list.value = res.data.list
}

function outboundTypeText(t: number) {
  return ['', '销售', '领用', '调拨', '报损'][t] || '其他'
}

async function onHandle(row: any, pass: boolean) {
  const { value } = await ElMessageBox.prompt('请输入审批意见', '审批', { confirmButtonText: '确定' })
  const step = active.value === 'APPLY' ? 1 : 2
  await handleApproval({ orderId: row.id, step, pass, remark: value || '' })
  ElMessage.success('已处理')
  loadData()
}

onMounted(loadData)
</script>
