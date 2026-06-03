<template>
  <el-card>
    <h3>📜 操作日志</h3>
    <el-table :data="list" border>
      <el-table-column prop="username" label="操作人" width="120" />
      <el-table-column prop="module" label="模块" width="120" />
      <el-table-column prop="action" label="操作" width="120" />
      <el-table-column prop="requestUrl" label="请求路径" width="240" />
      <el-table-column prop="ip" label="IP" width="120" />
      <el-table-column prop="costTime" label="耗时(ms)" width="100" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '成功' : '失败' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="时间" width="170" />
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
const list = ref<any[]>([])
onMounted(async () => {
  const res: any = await request.get('/system/log/page', { params: { pageNum: 1, pageSize: 50 } })
  list.value = res.data.list
})
</script>
