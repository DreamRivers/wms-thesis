<template>
  <el-card>
    <h3>📂 商品分类</h3>
    <el-table :data="list" border>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="parentId" label="父ID" width="80" />
      <el-table-column prop="categoryName" label="分类名" />
      <el-table-column prop="sort" label="排序" width="80" />
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { pageCategory } from '@/api/basic/goods'
const list = ref<any[]>([])
onMounted(async () => {
  const res: any = await pageCategory()
  list.value = res.data
})
</script>
