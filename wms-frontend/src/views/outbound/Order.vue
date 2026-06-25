<template>
  <el-card>
    <div class="page-toolbar">
      <h3>📋 出库单列表</h3>
      <div>
        <el-button type="primary" @click="applyVisible = true">📤 申请出库</el-button>
      </div>
    </div>
    <el-form :inline="true">
      <el-form-item><el-input v-model="query.orderNo" placeholder="单号" clearable /></el-form-item>
      <el-form-item>
        <el-select v-model="query.status" placeholder="状态" clearable>
          <el-option label="待部门审批" value="APPLY" />
          <el-option label="待仓管审批" value="APPROVING" />
          <el-option label="已审核" value="APPROVED" />
          <el-option label="已发货" value="SHIPPED" />
          <el-option label="已完成" value="FINISHED" />
          <el-option label="已驳回" value="REJECTED" />
          <el-option label="已取消" value="CANCELED" />
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" @click="loadData">查询</el-button></el-form-item>
    </el-form>
    <el-table :data="list" border>
      <el-table-column prop="orderNo" label="单号" width="220" />
      <el-table-column label="申请人" width="100">
        <template #default="{ row }">{{ getUserName(row.applicantId) }}</template>
      </el-table-column>
      <el-table-column label="仓库" width="120">
        <template #default="{ row }">{{ getWarehouseName(row.warehouseId) }}</template>
      </el-table-column>
      <el-table-column prop="totalQty" label="数量" width="80" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusTag(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="applyReason" label="出库原因" />
      <el-table-column label="出库商品" min-width="180">
        <template #default="{ row }">
          <span>共 {{ row.totalQty || 0 }} 件</span>
          <el-button link type="primary" size="small" @click="onDetail(row)">查看明细</el-button>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="340">
        <template #default="{ row }">
          <el-button size="small" @click="onDetail(row)">详情</el-button>
          <el-button v-if="row.status === 'APPLY'" size="small" type="success" @click="onAudit(row, true)">审批通过</el-button>
          <el-button v-if="row.status === 'APPLY'" size="small" type="danger" @click="onAudit(row, false)">驳回</el-button>
          <el-button v-if="row.status === 'APPROVING'" size="small" type="success" @click="onAudit(row, true)">仓管审批</el-button>
          <el-button v-if="row.status === 'APPROVING'" size="small" type="danger" @click="onAudit(row, false)">驳回</el-button>
          <el-button v-if="row.status === 'APPROVED'" size="small" type="success" @click="onShip(row)">发货出库</el-button>
          <el-button v-if="row.status === 'SHIPPED'" size="small" type="primary" @click="onComplete(row)">完成</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total"
      layout="total,prev,pager,next" @current-change="loadData" style="margin-top:16px" />

    <el-dialog v-if="detailVisible" v-model="detailVisible" :title="`出库单详情 ${detail.orderNo || ''}`" width="900px" :close-on-click-modal="true" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="单号">{{ detail.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTag(detail.status)">{{ statusText(detail.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="出库类型">{{ TYPE_MAP[detail.outboundType] || detail.outboundType }}</el-descriptions-item>
        <el-descriptions-item label="仓库">{{ getWarehouseName(detail.warehouseId) }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ getUserName(detail.applicantId) }}</el-descriptions-item>
        <el-descriptions-item label="总数量">{{ detail.totalQty }}</el-descriptions-item>
        <el-descriptions-item label="出库原因" :span="2">{{ detail.applyReason }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="申请时间" :span="2">{{ detail.createTime }}</el-descriptions-item>
      </el-descriptions>
      <h4 style="margin-top:16px">商品明细</h4>
      <el-table :data="detail.items || []" border size="small" :key="detail.id || 'empty'">
        <el-table-column prop="goodsId" label="商品ID" width="80" />
        <el-table-column label="商品名称" width="160">
          <template #default="{ row }">{{ getGoodsName(row.goodsId) }}</template>
        </el-table-column>
        <el-table-column prop="locationId" label="库位ID" width="80" />
        <el-table-column label="库位编码" width="140">
          <template #default="{ row }">{{ getLocationCode(row.locationId) }}</template>
        </el-table-column>
        <el-table-column prop="batchNo" label="批次" width="120" />
        <el-table-column prop="planQty" label="数量" width="80" />
        <el-table-column prop="actualQty" label="实发" width="80" />
      </el-table>
    </el-dialog>

    <el-dialog v-model="applyVisible" title="📤 申请出库" width="800px" @open="onApplyOpen">
      <el-form :model="applyForm" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="出库类型">
              <el-select v-model="applyForm.outboundType" style="width:100%">
                <el-option label="销售出库" :value="1" />
                <el-option label="领用出库" :value="2" />
                <el-option label="调拨出库" :value="3" />
                <el-option label="报损出库" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="仓库">
              <el-select v-model="applyForm.warehouseId" style="width:100%">
                <el-option v-for="w in warehouses" :key="w.id" :label="w.warehouseName" :value="w.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="出库原因">
          <el-input v-model="applyForm.applyReason" type="textarea" :rows="2" />
        </el-form-item>
        <el-divider>商品明细</el-divider>
        <el-button @click="addApplyItem">添加商品</el-button>
        <el-table :data="applyForm.items" border size="small" style="margin-top:8px">
          <el-table-column label="商品" width="240">
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
          <el-table-column label="批次" width="120">
            <template #default="{ row }"><el-input v-model="row.batchNo" /></template>
          </el-table-column>
          <el-table-column label="数量" width="140">
            <template #default="{ row }">
              <el-input
                v-model.number="row.planQty"
                type="number"
                :min="1"
                :max="getMaxQty(row)"
                placeholder="数量"
                style="width:100%"
              />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template #default="{ $index }">
              <el-button size="small" type="danger" @click="applyForm.items.splice($index,1)">删</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <template #footer>
        <el-button @click="applyVisible = false">取消</el-button>
        <el-button type="primary" @click="onApplySubmit">提交申请</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageOutbound, shipOutbound, completeOutbound, getOutbound, handleApproval, applyOutbound } from '@/api/outbound/order'
import { listAllGoods } from '@/api/basic/goods'
import { listAllWarehouses, listLocationsByWarehouse, pageLocations } from '@/api/basic/warehouse'
import { pageStock } from '@/api/stock/stock'
import { pageUser } from '@/api/system/user'

const query = reactive({ pageNum: 1, pageSize: 10, orderNo: '', status: '' })
const list = ref<any[]>([])
const total = ref(0)
const detailVisible = ref(false)
const detail = ref<any>({})

// 申请出库弹窗
const applyVisible = ref(false)
const applyForm = reactive<any>({ outboundType: 2, warehouseId: null, applyReason: '', remark: '', items: [] })
const goods = ref<any[]>([])
const warehouses = ref<any[]>([])
const locations = ref<any[]>([])
const stocks = ref<any[]>([])  // 库存列表
const users = ref<any[]>([])  // 用户列表

// 字典 join 函数
function getUserName(id: number): string {
  if (!id) return '-'
  const u = users.value.find(u => u.id === id || u.userId === id)
  return u ? (u.realName || u.username) : `#${id}`
}
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

// 列表展示: 出库单商品摘要
function getOrderGoodsText(row: any): string {
  if (!row.items || !row.items.length) {
    // 列表接口可能不带 items, 显示概要
    return '-'
  }
  const names = row.items.map((it: any) => `${getGoodsName(it.goodsId)}×${it.planQty || 0}`).slice(0, 2)
  const more = row.items.length > 2 ? ` 等${row.items.length}种` : ''
  return names.join('，') + more
}

// 取某行商品在某库位的最大可出库数
function getMaxQty(row: any): number {
  if (!row.goodsId || !row.locationId) return 999999
  const s = stocks.value.find(s => s.goodsId === row.goodsId && s.locationId === row.locationId)
  return s ? (s.availableQty || s.quantity || 0) : 0
}

// 显示该行商品库存
function getStock(row: any): string {
  if (!row.goodsId || !row.locationId) return '-'
  const s = stocks.value.find(s => s.goodsId === row.goodsId && s.locationId === row.locationId)
  return s ? `${s.availableQty || s.quantity || 0}` : '0'
}

const MAP: Record<string, [string, string]> = {
  APPLY: ['待部门审批', 'warning'], APPROVING: ['待仓管审批', 'warning'],
  APPROVED: ['已审核', 'primary'],
  SHIPPED: ['已发货', 'success'], FINISHED: ['已完成', 'success'],
  REJECTED: ['已驳回', 'danger'], CANCELED: ['已取消', 'info']
}
const statusText = (s: string) => MAP[s]?.[0] || s
const statusTag = (s: string): any => MAP[s]?.[1] || 'info'

const TYPE_MAP: Record<number, string> = {
  1: '销售出库', 2: '领用出库', 3: '调拨出库', 4: '报损出库'
}

async function loadData() {
  const res: any = await pageOutbound(query)
  list.value = res.data.list
  total.value = res.data.total
}

async function onApplyOpen() {
  Object.assign(applyForm, { outboundType: 2, warehouseId: null, applyReason: '', remark: '', items: [] })
  addApplyItem()
  const [g, w, s] = await Promise.all([
    listAllGoods(),
    listAllWarehouses(),
    pageStock({ pageNum: 1, pageSize: 1000 })
  ])
  goods.value = g.data || []
  warehouses.value = w.data || []
  stocks.value = s.data?.list || []
  if (warehouses.value.length) {
    applyForm.warehouseId = warehouses.value[0].id
    const l: any = await listLocationsByWarehouse(applyForm.warehouseId)
    locations.value = l.data || []
  }
}

function addApplyItem() {
  applyForm.items.push({ goodsId: null, locationId: null, batchNo: '', planQty: 1 })
}

// 监听明细的 goodsId/locationId 变化, 自动填库存到 planQty
watch(() => applyForm.items.map((it: any) => `${it.goodsId}_${it.locationId}`).join(','),
  () => {
    applyForm.items.forEach((it: any) => {
      if (it.goodsId && it.locationId) {
        const stock = getMaxQty(it)
        if (stock > 0) {
          it.planQty = stock
        }
      }
    })
  }
)

watch(() => applyForm.warehouseId, async (newId) => {
  if (newId) {
    const l: any = await listLocationsByWarehouse(newId)
    locations.value = l.data || []
  } else {
    locations.value = []
  }
  applyForm.items.forEach((it: any) => { it.locationId = null })
})

async function onApplySubmit() {
  if (!applyForm.warehouseId) { ElMessage.warning('请选择仓库'); return }
  if (!applyForm.items.length) { ElMessage.warning('请添加商品明细'); return }
  for (let i = 0; i < applyForm.items.length; i++) {
    const it = applyForm.items[i]
    if (!it.goodsId) { ElMessage.warning(`第 ${i+1} 行: 请选择商品`); return }
    if (!it.locationId) { ElMessage.warning(`第 ${i+1} 行: 请选择库位`); return }
    if (!it.planQty || it.planQty <= 0) { ElMessage.warning(`第 ${i+1} 行: 数量必须大于 0`); return }
    const max = getMaxQty(it)
    if (it.planQty > max) {
      ElMessage.warning(`第 ${i+1} 行: 数量超过库存 (${max})`)
      return
    }
  }
  await applyOutbound(applyForm)
  ElMessage.success('申请已提交,等待审批')
  applyVisible.value = false
  loadData()
}

async function onDetail(row: any) {
  const res: any = await getOutbound(row.id)
  // 后端返回 {order, items, approvals}, 拆开存
  const data = res.data || {}
  detail.value = { ...(data.order || row), items: data.items || [] }
  detailVisible.value = true
  // 兜底: 每次打开详情都确保字典已加载
  await ensureDictLoaded()
}

async function ensureDictLoaded() {
  if (goods.value.length === 0) {
    const g: any = await listAllGoods()
    goods.value = g.data || []
  }
  if (locations.value.length === 0) {
    const l: any = await pageLocations({ pageNum: 1, pageSize: 1000 })
    locations.value = l.data?.list || []
  }
  if (warehouses.value.length === 0) {
    const w: any = await listAllWarehouses()
    warehouses.value = w.data || []
  }
  if (users.value.length === 0) {
    const u: any = await pageUser({ pageNum: 1, pageSize: 1000 })
    users.value = u.data?.list || []
  }
}

async function onAudit(row: any, pass: boolean) {
  const action = pass ? '通过' : '驳回'
  // step=1 部门负责人审批 APPLY, step=2 仓管审批 APPROVING
  const step = row.status === 'APPLY' ? 1 : 2
  const title = step === 1 ? '部门负责人审批' : '仓管审批'
  const { value } = await ElMessageBox.prompt(`请输入${action}意见`, title, { confirmButtonText: '确定', cancelButtonText: '取消' })
  await handleApproval({ orderId: row.id, step, pass, remark: value || '' })
  ElMessage.success(`已${action}`)
  loadData()
}

async function onShip(row: any) {
  await ElMessageBox.confirm(`确认发货出库 ${row.orderNo}?将扣减库存`, '提示', { type: 'warning' })
  await shipOutbound(row.id)
  ElMessage.success('已发货,库存已扣减')
  loadData()
}

async function onComplete(row: any) {
  await completeOutbound(row.id)
  ElMessage.success('已完成')
  loadData()
}

onMounted(async () => {
  await loadData()
  // 加载字典数据
  try {
    const [w, l, u, g] = await Promise.all([
      listAllWarehouses(),
      pageLocations({ pageNum: 1, pageSize: 1000 }),
      pageUser({ pageNum: 1, pageSize: 1000 }),
      listAllGoods()
    ])
    warehouses.value = w.data || []
    locations.value = l.data?.list || []
    users.value = u.data?.list || []
    goods.value = g.data || []
  } catch (e) {
    console.error('[Order] load dict failed', e)
  }
})
</script>
