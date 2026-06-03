<template>
  <el-card>
    <h3>✅ 入库审核</h3>
    <el-table :data="list" border>
      <el-table-column prop="orderNo" label="单号" width="220" />
      <el-table-column prop="supplierId" label="供应商" width="100" />
      <el-table-column prop="warehouseId" label="仓库" width="100" />
      <el-table-column prop="totalQty" label="数量" width="80" />
      <el-table-column prop="totalAmount" label="金额" width="120" />
      <el-table-column prop="createTime" label="时间" width="170" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" type="success" @click="onAudit(row, true)">通过</el-button>
          <el-button size="small" type="danger" @click="onAudit(row, false)">驳回</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageInbound, auditInbound } from '@/api/inbound/order'
const list = ref<any[]>([])
async function loadData() {
  const res: any = await pageInbound({ pageNum: 1, pageSize: 20, status: 'PENDING' })
  list.value = res.data.list
}
async function onAudit(row: any, pass: boolean) {
  const { value } = await ElMessageBox.prompt('请输入审核意见', '审核')
  await auditInbound({ id: row.id, pass, remark: value || '' })
  ElMessage.success('已审核')
  loadData()
}
onMounted(loadData)
</script>
