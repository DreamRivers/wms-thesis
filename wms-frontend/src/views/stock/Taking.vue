<template>
  <el-card>
    <div class="page-toolbar">
      <h3>盘点管理</h3>
      <el-button type="primary" @click="onCreate">新建盘点单</el-button>
    </div>
    <el-table :data="list" border>
      <el-table-column prop="takeNo" label="盘点单号" width="200" />
      <el-table-column label="仓库" width="120">
        <template #default="{ row }">{{ getWarehouseName(row.warehouseId) }}</template>
      </el-table-column>
      <el-table-column prop="takeType" label="类型" width="100">
        <template #default="{ row }">{{ ['', '全盘', '抽盘', '动态'][row.takeType] }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'FINISHED' ? 'success' : 'warning'">
            {{ { DRAFT: '草稿', EXECUTING: '执行中', FINISHED: '已完成' }[row.status] || row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button size="small" @click="onDetail(row)">详情</el-button>
          <el-button v-if="row.status === 'DRAFT'" size="small" type="warning" @click="onAdjust(row)">完成并调整</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="新建盘点单" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="仓库">
          <el-select v-model="form.warehouseId" style="width:100%">
            <el-option v-for="w in warehouses" :key="w.id" :label="w.warehouseName" :value="w.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-radio-group v-model="form.takeType">
            <el-radio :value="1">全盘</el-radio>
            <el-radio :value="2">抽盘</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="onSubmit">创建</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="盘点明细" width="1000px">
      <el-table :data="items" border>
        <el-table-column prop="goodsId" label="商品ID" width="80" />
        <el-table-column label="商品名称" width="160">
          <template #default="{ row }">{{ getGoodsName(row.goodsId) }}</template>
        </el-table-column>
        <el-table-column prop="locationId" label="库位ID" width="80" />
        <el-table-column label="库位编码" width="160">
          <template #default="{ row }">{{ getLocationCode(row.locationId) }}</template>
        </el-table-column>
        <el-table-column prop="batchNo" label="批次" width="120" />
        <el-table-column prop="systemQty" label="系统数量" width="100" />
        <el-table-column label="实盘数量" width="120">
          <template #default="{ row }">
            <el-input-number v-model="row.actualQty" :min="0" />
          </template>
        </el-table-column>
        <el-table-column label="差异" width="80">
          <template #default="{ row }">
            <span :style="{ color: ((row.actualQty || 0) - (row.systemQty || 0)) === 0 ? '#67c23a' : '#f56c6c' }">
              {{ (row.actualQty || 0) - (row.systemQty || 0) }}
            </span>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top:16px;text-align:right">
        <el-button type="warning" @click="onSaveDetail">保存实盘数</el-button>
      </div>
    </el-dialog>
  </el-card>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageTaking, createTaking, getTaking, recordTaking, adjustTaking } from '@/api/stock/stock'
import { listAllGoods, listAllWarehouses } from '@/api/basic/goods'
import { pageLocations } from '@/api/basic/warehouse'

const list = ref<any[]>([])
const dialogVisible = ref(false)
const detailVisible = ref(false)
const items = ref<any[]>([])
const currentTakeId = ref<number>()
const form = reactive<any>({ warehouseId: null, takeType: 1, remark: '' })
const warehouses = ref<any[]>([])
const goods = ref<any[]>([])
const locations = ref<any[]>([])

function getWarehouseName(id: number): string {
  if (!id) return '-'
  const w = warehouses.value.find(w => w.id === id)
  return w ? w.warehouseName : `#${id}`
}
function getGoodsName(id: number): string {
  if (!id) return '-'
  const g = goods.value.find(g => g.id === id)
  return g ? g.goodsName : `#${id}`
}
function getLocationCode(id: number): string {
  if (!id) return '-'
  const l = locations.value.find(l => l.id === id)
  return l ? (l.locationCode || l.code) : `#${id}`
}

async function loadAllDict() {
  const [g, w, l] = await Promise.all([
    listAllGoods(),
    listAllWarehouses(),
    pageLocations({ pageNum: 1, pageSize: 1000 })
  ])
  goods.value = g.data || []
  warehouses.value = w.data || []
  locations.value = l.data?.list || []
}

async function loadData() {
  const res: any = await pageTaking({ pageNum: 1, pageSize: 20 })
  list.value = res.data.list
}

async function onCreate() {
  form.warehouseId = null
  form.takeType = 1
  form.remark = ''
  dialogVisible.value = true
  await loadAllDict()
}

async function onSubmit() {
  await createTaking(form, null)
  ElMessage.success('创建成功')
  dialogVisible.value = false
  loadData()
}

async function onDetail(row: any) {
  const res: any = await getTaking(row.id)
  items.value = res.data.items
  currentTakeId.value = row.id
  detailVisible.value = true
  await loadAllDict()
}

async function onSaveDetail() {
  await recordTaking(currentTakeId.value!, items.value)
  ElMessage.success('已保存实盘数')
}

async function onAdjust(row: any) {
  await ElMessageBox.confirm(`确认完成盘点 ${row.takeNo}?将根据差异调整库存`, '提示', { type: 'warning' })
  await adjustTaking(row.id)
  ElMessage.success('已调整')
  loadData()
}

onMounted(async () => {
  await loadData()
  await loadAllDict()
})
</script>
