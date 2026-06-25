<template>
  <el-card>
    <h3>库存流水</h3>
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
      <el-table-column prop="recordNo" label="流水号" width="200" />
      <el-table-column label="类型" width="100">
        <template #default="{ row }">
          <el-tag :type="typeTag(row.businessType)">{{ typeText(row.businessType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="businessNo" label="关联单号" width="200" />
      <el-table-column prop="goodsId" label="商品ID" width="80" />
      <el-table-column label="商品名称" width="180">
        <template #default="{ row }">{{ getGoodsName(row.goodsId) }}</template>
      </el-table-column>
      <el-table-column label="变动" width="100">
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
    <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total"
      layout="total,sizes,prev,pager,next,jumper" :page-sizes="[10,20,50]"
      @size-change="loadData" @current-change="loadData" style="margin-top:16px" />
  </el-card>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { pageRecord } from '@/api/stock/stock'
import { listAllGoods } from '@/api/basic/goods'

const query = reactive({ pageNum: 1, pageSize: 20, goodsId: null, businessType: '' })
const list = ref<any[]>([])
const total = ref(0)
const goods = ref<any[]>([])

const TYPE_MAP: Record<string, [string, string]> = {
  INBOUND: ['入库', 'success'], OUTBOUND: ['出库', 'warning'], TAKE_ADJ: ['盘点调整', 'info']
}
const typeText = (t: string) => TYPE_MAP[t]?.[0] || t
const typeTag = (t: string): any => TYPE_MAP[t]?.[1] || 'info'

function getGoodsName(id: number): string {
  if (!id) return '-'
  const g = goods.value.find(g => g.id === id)
  return g ? g.goodsName : `#${id}`
}

async function loadData() {
  const res: any = await pageRecord(query)
  list.value = res.data.list
  total.value = res.data.total
}

onMounted(async () => {
  await loadData()
  const g: any = await listAllGoods()
  goods.value = g.data || []
})
</script>
