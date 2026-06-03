<template>
  <el-card>
    <h3>🤝 供应商管理</h3>
    <el-table :data="list" border>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="supplierCode" label="编码" />
      <el-table-column prop="supplierName" label="名称" />
      <el-table-column prop="contact" label="联系人" width="100" />
      <el-table-column prop="phone" label="电话" width="140" />
      <el-table-column prop="creditLevel" label="信用" width="80" />
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { listAllSuppliers } from '@/api/basic/goods'
const list = ref<any[]>([])
onMounted(async () => {
  const res: any = await listAllSuppliers()
  list.value = res.data
})
</script>
