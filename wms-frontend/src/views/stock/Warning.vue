<template>
  <el-card>
    <div class="page-toolbar">
      <h3>⚠️ 库存预警</h3>
      <el-button @click="readAll">全部已读</el-button>
    </div>
    <el-tabs v-model="type" @tab-change="loadData">
      <el-tab-pane label="全部" name=""></el-tab-pane>
      <el-tab-pane label="低库存" name="LOW_STOCK"></el-tab-pane>
      <el-tab-pane label="临期预警" name="EXPIRE_SOON"></el-tab-pane>
      <el-tab-pane label="待审批" name="REVIEW"></el-tab-pane>
    </el-tabs>
    <el-table :data="list" border>
      <el-table-column prop="type" label="类型" width="120">
        <template #default="{ row }">
          <el-tag :type="typeTag(row.type)">{{ typeText(row.type) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="content" label="内容" />
      <el-table-column prop="readStatus" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.readStatus === 0 ? 'danger' : 'info'">
            {{ row.readStatus === 0 ? '未读' : '已读' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="时间" width="170" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button v-if="row.readStatus === 0" size="small" @click="onRead(row)">标为已读</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { pageNotice, readNotice, readAll as readAllApi } from '@/api/stock/stock'

const type = ref('')
const list = ref<any[]>([])

const TYPE_MAP: Record<string, [string, string]> = {
  LOW_STOCK: ['低库存', 'danger'], EXPIRE_SOON: ['临期', 'warning'], REVIEW: ['待审', 'primary']
}
const typeText = (t: string) => TYPE_MAP[t]?.[0] || t
const typeTag = (t: string): any => TYPE_MAP[t]?.[1] || 'info'

async function loadData() {
  const res: any = await pageNotice({ pageNum: 1, pageSize: 50, type: type.value })
  list.value = res.data.list
}

async function onRead(row: any) {
  await readNotice(row.id)
  ElMessage.success('已读')
  loadData()
}

async function readAll() {
  await readAllApi()
  ElMessage.success('已全部已读')
  loadData()
}

onMounted(loadData)
</script>
