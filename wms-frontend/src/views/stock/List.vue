<template>
  <el-card>
    <h3>📦 实时库存</h3>
    <el-form :inline="true">
      <el-form-item><el-input v-model="query.goodsName" placeholder="商品名称" clearable /></el-form-item>
      <el-form-item><el-button type="primary" @click="loadData">查询</el-button></el-form-item>
    </el-form>
    <el-table :data="list" border stripe>
      <el-table-column prop="goodsId" label="商品ID" width="80" />
      <el-table-column prop="locationId" label="库位ID" width="80" />
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

const query = reactive({ pageNum: 1, pageSize: 10, goodsId: null, locationId: null })
const list = ref<any[]>([])
const total = ref(0)

async function loadData() {
  const res: any = await pageStock(query)
  list.value = res.data.list
  total.value = res.data.total
}

onMounted(loadData)
</script>
