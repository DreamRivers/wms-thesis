<template>
  <el-card>
    <h3>📤 出库申请</h3>
    <el-form :model="form" label-width="100px">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="出库类型">
            <el-select v-model="form.outboundType" style="width:100%">
              <el-option label="销售出库" :value="1" />
              <el-option label="领用出库" :value="2" />
              <el-option label="调拨出库" :value="3" />
              <el-option label="报损出库" :value="4" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="仓库">
            <el-select v-model="form.warehouseId" style="width:100%">
              <el-option v-for="w in warehouses" :key="w.id" :label="w.warehouseName" :value="w.id" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="出库原因">
        <el-input v-model="form.applyReason" type="textarea" :rows="2" />
      </el-form-item>

      <el-divider>商品明细</el-divider>
      <el-button @click="addItem">添加商品</el-button>
      <el-table :data="form.items" border size="small" style="margin-top:8px">
        <el-table-column label="商品" width="250">
          <template #default="{ row }">
            <el-select v-model="row.goodsId" filterable style="width:100%">
              <el-option v-for="g in goods" :key="g.id" :label="g.goodsName" :value="g.id" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="库位" width="180">
          <template #default="{ row }">
            <el-select v-model="row.locationId" style="width:100%">
              <el-option v-for="l in locations" :key="l.id" :label="l.locationCode" :value="l.id" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="批次" width="120"><template #default="{ row }"><el-input v-model="row.batchNo" /></template></el-table-column>
        <el-table-column label="数量" width="120"><template #default="{ row }"><el-input-number v-model="row.planQty" :min="1" /></template></el-table-column>
        <el-table-column label="操作" width="80"><template #default="{ $index }"><el-button size="small" type="danger" @click="form.items.splice($index,1)">删</el-button></template></el-table-column>
      </el-table>

      <div style="margin-top:24px;text-align:center">
        <el-button type="primary" size="large" @click="onSubmit">提交申请</el-button>
      </div>
    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { applyOutbound } from '@/api/outbound/order'
import { listAllGoods, listAllWarehouses, listLocationsByWarehouse } from '@/api/basic/goods'

const router = useRouter()
const form = reactive<any>({ outboundType: 2, warehouseId: null, applyReason: '', remark: '', items: [] })
const goods = ref<any[]>([])
const warehouses = ref<any[]>([])
const locations = ref<any[]>([])

function addItem() { form.items.push({ goodsId: null, locationId: null, batchNo: '', planQty: 1 }) }

async function onSubmit() {
  if (!form.items.length) { ElMessage.warning('请添加明细'); return }
  await applyOutbound(form)
  ElMessage.success('申请已提交,等待审批')
  form.items = []
  router.push('/outbound/order')
}

onMounted(async () => {
  const [g, w] = await Promise.all([listAllGoods(), listAllWarehouses()])
  goods.value = g.data
  warehouses.value = w.data
  if (warehouses.value.length) {
    form.warehouseId = warehouses.value[0].id
    const l: any = await listLocationsByWarehouse(form.warehouseId)
    locations.value = l.data
  }
})
</script>
