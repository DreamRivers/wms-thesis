<template>
  <el-card>
    <div class="page-toolbar">
      <el-form :inline="true" :model="query">
        <el-form-item><el-input v-model="query.orderNo" placeholder="单号" clearable /></el-form-item>
        <el-form-item>
          <el-select v-model="query.status" placeholder="状态" clearable>
            <el-option label="草稿" value="DRAFT" />
            <el-option label="待审核" value="PENDING" />
            <el-option label="已审核" value="APPROVED" />
            <el-option label="执行中" value="EXECUTING" />
            <el-option label="已完成" value="FINISHED" />
            <el-option label="已驳回" value="REJECTED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" @click="onAdd">新建入库单</el-button>
    </div>

    <el-table :data="list" border stripe>
      <el-table-column prop="orderNo" label="入库单号" width="220" />
      <el-table-column prop="supplierId" label="供应商" width="80" />
      <el-table-column prop="warehouseId" label="仓库" width="80" />
      <el-table-column prop="totalQty" label="总数量" width="100" />
      <el-table-column prop="totalAmount" label="总金额" width="120" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusTag(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="280">
        <template #default="{ row }">
          <el-button v-if="row.status === 'DRAFT'" size="small" @click="onEdit(row)">编辑</el-button>
          <el-button v-if="row.status === 'DRAFT'" size="small" type="primary" @click="onSubmit(row)">提交</el-button>
          <el-button v-if="row.status === 'PENDING'" size="small" type="success" @click="onAudit(row, true)">审核通过</el-button>
          <el-button v-if="row.status === 'PENDING'" size="small" type="danger" @click="onAudit(row, false)">驳回</el-button>
          <el-button v-if="row.status === 'APPROVED'" size="small" type="warning" @click="onExecute(row)">执行</el-button>
          <el-button v-if="row.status === 'EXECUTING'" size="small" type="success" @click="onComplete(row)">完成</el-button>
          <el-button size="small" @click="onDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total"
      layout="total,sizes,prev,pager,next,jumper" :page-sizes="[10,20,50]" @size-change="loadData" @current-change="loadData" style="margin-top:16px" />

    <!-- 新建/编辑入库单对话框 -->
    <el-dialog v-model="dialogVisible" :title="editing.id ? '编辑入库单' : '新建入库单'" width="900px">
      <el-form :model="editing" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="供应商">
            <el-select v-model="editing.supplierId" placeholder="请选择" style="width:100%">
              <el-option v-for="s in suppliers" :key="s.id" :label="s.supplierName" :value="s.id" />
            </el-select>
          </el-form-item></el-col>
          <el-col :span="12"><el-form-item label="仓库">
            <el-select v-model="editing.warehouseId" placeholder="请选择" style="width:100%">
              <el-option v-for="w in warehouses" :key="w.id" :label="w.warehouseName" :value="w.id" />
            </el-select>
          </el-form-item></el-col>
        </el-row>
        <el-form-item label="备注"><el-input v-model="editing.remark" /></el-form-item>
        <el-divider>商品明细</el-divider>
        <el-button size="small" @click="addItem">添加行</el-button>
        <el-table :data="editing.items" border size="small" style="margin-top:8px">
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
          <el-table-column label="计划数量" width="120"><template #default="{ row }"><el-input-number v-model="row.planQty" :min="1" /></template></el-table-column>
          <el-table-column label="单价" width="120"><template #default="{ row }"><el-input-number v-model="row.unitPrice" :precision="2" :min="0" /></template></el-table-column>
          <el-table-column label="操作" width="80"><template #default="{ $index }"><el-button size="small" type="danger" @click="editing.items.splice($index,1)">删</el-button></template></el-table-column>
        </el-table>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="onSave">保存草稿</el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="入库单详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="单号">{{ detail.order?.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="状态"><el-tag :type="statusTag(detail.order?.status)">{{ statusText(detail.order?.status) }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="供应商">{{ detail.order?.supplierId }}</el-descriptions-item>
        <el-descriptions-item label="仓库">{{ detail.order?.warehouseId }}</el-descriptions-item>
        <el-descriptions-item label="总数量">{{ detail.order?.totalQty }}</el-descriptions-item>
        <el-descriptions-item label="总金额">{{ detail.order?.totalAmount }}</el-descriptions-item>
      </el-descriptions>
      <el-divider>明细</el-divider>
      <el-table :data="detail.items || []" size="small" border>
        <el-table-column prop="goodsId" label="商品ID" />
        <el-table-column prop="locationId" label="库位ID" />
        <el-table-column prop="batchNo" label="批次" />
        <el-table-column prop="planQty" label="计划" />
        <el-table-column prop="actualQty" label="实收" />
        <el-table-column prop="unitPrice" label="单价" />
      </el-table>
    </el-dialog>
  </el-card>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageInbound, saveInbound, submitInbound, auditInbound, executeInbound, completeInbound, getInbound } from '@/api/inbound/order'
import { listAllGoods, listAllSuppliers, listAllWarehouses, listLocationsByWarehouse } from '@/api/basic/goods'

const query = reactive({ pageNum: 1, pageSize: 10, orderNo: '', status: '' })
const list = ref<any[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const detail = ref<any>({})

const editing = reactive<any>({ id: null, supplierId: null, warehouseId: null, remark: '', items: [] })
const goods = ref<any[]>([])
const suppliers = ref<any[]>([])
const warehouses = ref<any[]>([])
const locations = ref<any[]>([])

const STATUS_MAP: Record<string, [string, string]> = {
  DRAFT: ['草稿', 'info'], PENDING: ['待审核', 'warning'], APPROVED: ['已审核', 'primary'],
  EXECUTING: ['执行中', 'warning'], FINISHED: ['已完成', 'success'], REJECTED: ['已驳回', 'danger']
}
const statusText = (s: string) => STATUS_MAP[s]?.[0] || s
const statusTag = (s: string): any => STATUS_MAP[s]?.[1] || 'info'

async function loadData() {
  const res: any = await pageInbound(query)
  list.value = res.data.list
  total.value = res.data.total
}

function addItem() {
  editing.items.push({ goodsId: null, locationId: null, batchNo: '', planQty: 1, unitPrice: 0 })
}

async function onAdd() {
  Object.assign(editing, { id: null, supplierId: null, warehouseId: null, remark: '', items: [] })
  addItem()
  dialogVisible.value = true
  await loadAuxData()
}

async function onEdit(row: any) {
  const res: any = await getInbound(row.id)
  Object.assign(editing, res.data.order, { items: res.data.items })
  dialogVisible.value = true
  await loadAuxData()
}

async function loadAuxData() {
  const [g, s, w] = await Promise.all([listAllGoods(), listAllSuppliers(), listAllWarehouses()])
  goods.value = g.data
  suppliers.value = s.data
  warehouses.value = w.data
  if (editing.warehouseId) {
    const l: any = await listLocationsByWarehouse(editing.warehouseId)
    locations.value = l.data
  }
}

async function onSave() {
  if (!editing.warehouseId) { ElMessage.warning('请选择仓库'); return }
  if (!editing.items.length) { ElMessage.warning('请添加明细'); return }
  await saveInbound(editing)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadData()
}

async function onSubmit(row: any) {
  await ElMessageBox.confirm(`确定提交单据 ${row.orderNo}?`, '提示')
  await submitInbound(row.id)
  ElMessage.success('已提交')
  loadData()
}

async function onAudit(row: any, pass: boolean) {
  const { value } = await ElMessageBox.prompt('请输入审核意见', '审核', { confirmButtonText: '确定' })
  await auditInbound({ id: row.id, pass, remark: value || '' })
  ElMessage.success('已审核')
  loadData()
}

async function onExecute(row: any) {
  // 简化:直接调用 execute
  const res: any = await getInbound(row.id)
  await executeInbound(row.id, res.data.items)
  ElMessage.success('已执行,等待完成入库')
  loadData()
}

async function onComplete(row: any) {
  await ElMessageBox.confirm(`确认完成入库 ${row.orderNo}?完成后将写入库存`, '提示', { type: 'warning' })
  await completeInbound(row.id)
  ElMessage.success('入库完成,库存已更新')
  loadData()
}

async function onDetail(row: any) {
  const res: any = await getInbound(row.id)
  detail.value = res.data
  detailVisible.value = true
}

onMounted(loadData)
</script>
