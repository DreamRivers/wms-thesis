<template>
  <el-card>
    <h3>📋 菜单管理</h3>
    <el-table :data="list" border>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="parentId" label="父ID" width="80" />
      <el-table-column prop="menuName" label="菜单名" />
      <el-table-column prop="menuType" label="类型" width="80" />
      <el-table-column prop="path" label="路径" />
      <el-table-column prop="perms" label="权限码" />
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
const list = ref<any[]>([])
onMounted(async () => {
  const res: any = await request.get('/system/menu/list')
  list.value = res.data
})
</script>
