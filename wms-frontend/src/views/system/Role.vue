<template>
  <el-card>
    <h3>🛡️ 角色管理</h3>
    <el-table :data="list" border>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="roleName" label="角色名" />
      <el-table-column prop="roleCode" label="编码" />
      <el-table-column prop="remark" label="备注" />
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const list = ref<any[]>([])

onMounted(async () => {
  const res: any = await request.get('/system/role/listAll')
  list.value = res.data
})
</script>
