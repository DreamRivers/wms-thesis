<template>
  <el-card>
    <h3>📜 库存流水</h3>
    <el-form :inline="true">
      <el-form-item>
        <el-select v-model="query.businessType" placeholder="类型" clearable>
          <el-option label="入库" value="INBOUND" />
          <el-option label="出库" value="OUTBOUND" />
          <el-option label="盘点调整" value="TAKE_ADJ" />
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" @click="loadData">查询</el-button></el-form-item>
    </el-form>
    <el-table :data="list" border>
      <el-table-column prop="recordNo" label="流水号" width="220" />
      <el-table-column prop="businessType" label="类型" width="100" />
      <el-table-column prop="businessNo" label="关联单号" width="200" />
      <el-table-column prop="goodsId" label="商品ID" width="80" />
      <el-table-column prop="changeType" label="变动" width="80">
        <template #default="{ row }">
          <el-tag :type="row.changeType === 1 ? 'success' : 'danger'">
            {{ row.changeType === 1 ? '+' : '-' }}{{ row.changeQty }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="beforeQty" label="变动前" width="100" />
      <el-table-column prop="afterQty" label="变动后" width="100" />
      <el-table-column prop="operateTime" label="时间" width="170" />
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { pageRecord } from '@/api/stock/stock'

const query = reactive({ pageNum: 1, pageSize: 20, goodsId: null, businessType: '' })
const list = ref<any[]>([])

async function loadData() {
  const res: any = await pageRecord(query)
  list.value = res.data.list
}

onMounted(loadData)
</script>
