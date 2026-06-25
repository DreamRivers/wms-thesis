<template>
  <el-card>
    <div class="page-toolbar">
      <h3>📍 库位管理</h3>
      <el-button type="primary" @click="onAdd">新增库位</el-button>
    </div>
    <el-form :inline="true">
      <el-form-item>
        <el-select v-model="query.warehouseId" placeholder="选择仓库" clearable style="width:160px" @change="loadData">
          <el-option v-for="w in warehouses" :key="w.id" :label="w.warehouseName" :value="w.id" />
        </el-select>
      </el-form-item>
      <el-form-item><el-input v-model="query.locationCode" placeholder="库位编码" clearable /></el-form-item>
      <el-form-item><el-button type="primary" @click="loadData">查询</el-button></el-form-item>
    </el-form>
    <el-table :data="list" border>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="所属仓库" width="120">
        <template #default="{ row }">{{ getWarehouseName(row.warehouseId) }}</template>
      </el-table-column>
      <el-table-column prop="locationCode" label="库位编码" width="140" />
      <el-table-column prop="area" label="库区" width="100" />
      <el-table-column prop="shelf" label="货架" width="100" />
      <el-table-column prop="layer" label="层" width="60" />
      <el-table-column prop="columnNo" label="列" width="60" />
      <el-table-column label="类型" width="100">
        <template #default="{ row }">
          {{ TYPE_MAP[row.locationType] || row.locationType }}
        </template>
      </el-table-column>
      <el-table-column label="容量" width="100">
        <template #default="{ row }">
          <span :style="{ color: row.capacity > 0 ? '#67c23a' : '#909399' }">
            {{ row.capacity || 0 }}<span style="font-size:11px;color:#909399"> 件</span>
          </span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button size="small" @click="onEdit(row)">编辑</el-button>
          <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'" @click="onToggle(row)">
            {{ row.status === 1 ? '停用' : '启用' }}
          </el-button>
          <el-button size="small" type="danger" @click="onDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total"
      layout="total,prev,pager,next" @current-change="loadData" style="margin-top:16px" />

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑库位' : '新增库位'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="所属仓库">
          <el-select v-model="form.warehouseId" placeholder="选择仓库" style="width:100%">
            <el-option v-for="w in warehouses" :key="w.id" :label="w.warehouseName" :value="w.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="库位编码"><el-input v-model="form.locationCode" placeholder="如 A-01-01" /></el-form-item>
        <el-form-item label="库区"><el-input v-model="form.area" /></el-form-item>
        <el-form-item label="货架"><el-input v-model="form.shelf" /></el-form-item>
        <el-form-item label="层"><el-input-number v-model="form.layer" :min="0" /></el-form-item>
        <el-form-item label="列"><el-input-number v-model="form.columnNo" :min="0" /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.locationType" style="width:100%">
            <el-option :value="1" label="正常" />
            <el-option :value="2" label="暂存" />
            <el-option :value="3" label="残次品" />
          </el-select>
        </el-form-item>
        <el-form-item label="容量"><el-input-number v-model="form.capacity" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="停用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="onSubmit">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageLocations, addLocation, updateLocation, deleteLocation } from '@/api/basic/warehouse'
import { listAllWarehouses } from '@/api/basic/warehouse'

const TYPE_MAP: Record<number, string> = { 1: '正常', 2: '暂存', 3: '残次品' }

const query = reactive({ pageNum: 1, pageSize: 10, warehouseId: null as any, locationCode: '' })
const list = ref<any[]>([])
const total = ref(0)
const warehouses = ref<any[]>([])
const dialogVisible = ref(false)
const form = reactive<any>({
  id: null, warehouseId: null, locationCode: '', area: '', shelf: '',
  layer: 1, columnNo: 1, locationType: 1, capacity: 0, status: 1
})

async function loadData() {
  const res: any = await pageLocations(query)
  list.value = res.data.list
  total.value = res.data.total
}

async function loadWarehouses() {
  const res: any = await listAllWarehouses()
  warehouses.value = res.data
}

function getWarehouseName(id: number): string {
  if (!id) return '-'
  const w = warehouses.value.find(w => w.id === id)
  return w ? w.warehouseName : `#${id}`
}

function onAdd() {
  Object.assign(form, {
    id: null, warehouseId: null, locationCode: '', area: '', shelf: '',
    layer: 1, columnNo: 1, locationType: 1, capacity: 100, status: 1
  })
  dialogVisible.value = true
}

function onEdit(row: any) {
  Object.assign(form, row)
  dialogVisible.value = true
}

async function onSubmit() {
  if (!form.warehouseId) { ElMessage.warning('请选择仓库'); return }
  if (!form.locationCode) { ElMessage.warning('请输入库位编码'); return }
  if (form.id) await updateLocation(form)
  else await addLocation(form)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadData()
}

async function onToggle(row: any) {
  const newStatus = row.status === 1 ? 0 : 1
  await updateLocation({ id: row.id, status: newStatus })
  ElMessage.success('已更新')
  loadData()
}

async function onDelete(row: any) {
  await ElMessageBox.confirm(`确定删除库位 "${row.locationCode}" 吗?`, '提示', { type: 'warning' })
  await deleteLocation([row.id])
  ElMessage.success('已删除')
  loadData()
}

onMounted(() => { loadData(); loadWarehouses() })
</script>
