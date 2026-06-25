<template>
  <el-card>
    <h3>📦 实时库存</h3>
    <el-form :inline="true">
      <el-form-item>
        <el-select v-model="query.goodsId" placeholder="选择商品" clearable filterable style="width:200px">
          <el-option v-for="g in goods" :key="g.id" :label="g.goodsName" :value="g.id" />
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" @click="loadData">查询</el-button></el-form-item>
    </el-form>
    <el-table :data="list" border stripe>
      <el-table-column prop="goodsId" label="商品ID" width="80" />
      <el-table-column label="商品名称" width="200">
        <template #default="{ row }">{{ getGoodsName(row.goodsId) }}</template>
      </el-table-column>
      <el-table-column prop="locationId" label="库位ID" width="80" />
      <el-table-column prop="locationCode" label="库位编码" width="140">
        <template #default="{ row }">{{ getLocationCode(row.locationId) }}</template>
      </el-table-column>
      <el-table-column prop="batchNo" label="批次" width="160" />
      <el-table-column prop="quantity" label="在库数量" width="100" />
      <el-table-column prop="lockedQty" label="锁定数量" width="100" />
      <el-table-column prop="availableQty" label="可用数量" width="100" />
      <el-table-column prop="productionDate" label="生产日期" width="120" />
      <el-table-column prop="expireDate" label="到期日期" width="120" />
      <el-table-column prop="lastInTime" label="最后入库" width="170" />
    </el-table>
    <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total"
      layout="total,sizes,prev,pager,next,jumper" :page-sizes="[10,20,50]" @size-change="loadData" @current-change="loadData" style="margin-top:16px" />
  </el-card>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { pageStock } from '@/api/stock/stock'
import { listAllGoods } from '@/api/basic/goods'
import { pageLocations } from '@/api/basic/warehouse'

const query = reactive({ pageNum: 1, pageSize: 10, goodsId: null as any, locationId: null as any })
const list = ref<any[]>([])
const total = ref(0)
const goods = ref<any[]>([])
const locations = ref<any[]>([])

function getGoodsName(id: number): string {
  const g = goods.value.find(g => g.id === id)
  return g ? g.goodsName : `#${id}`
}

function getLocationCode(id: number): string {
  const l = locations.value.find(l => l.id === id)
  return l ? l.locationCode : `#${id}`
}

async function loadData() {
  const res: any = await pageStock(query)
  list.value = res.data.list
  total.value = res.data.total
}

async function loadAux() {
  const [g, l] = await Promise.all([
    listAllGoods(),
    pageLocations({ pageNum: 1, pageSize: 1000 })
  ])
  goods.value = g.data || []
  locations.value = l.data?.list || []
}

onMounted(() => { loadData(); loadAux() })
</script>
