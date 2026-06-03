<template>
  <el-card>
    <h3>📍 库位管理</h3>
    <el-table :data="list" border>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="warehouseId" label="仓库ID" width="100" />
      <el-table-column prop="locationCode" label="库位编码" />
      <el-table-column prop="area" label="库区" width="100" />
      <el-table-column prop="shelf" label="货架" width="100" />
      <el-table-column prop="layer" label="层" width="60" />
      <el-table-column prop="locationType" label="类型" width="100">
        <template #default="{ row }">
          {{ ['', '正常', '暂存', '残次品'][row.locationType] }}
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { pageLocations } from '@/api/basic/goods'
const query = reactive({ pageNum: 1, pageSize: 30, warehouseId: null as any, locationCode: '' })
const list = ref<any[]>([])
onMounted(async () => {
  const res: any = await pageLocations(query)
  list.value = res.data.list
})
</script>
