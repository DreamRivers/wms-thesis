<template>
  <el-card>
    <h3>🏭 仓库管理</h3>
    <el-table :data="list" border>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="warehouseCode" label="编码" />
      <el-table-column prop="warehouseName" label="名称" />
      <el-table-column prop="address" label="地址" />
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
const list = ref<any[]>([])
onMounted(async () => {
  const res: any = await request.get('/basic/warehouse/listAll')
  list.value = res.data
})
</script>
