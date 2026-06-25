<template>
  <el-card>
    <div class="page-toolbar">
      <h3>🏭 仓库管理</h3>
      <el-button type="primary" @click="onAdd">新增仓库</el-button>
    </div>
    <el-form :inline="true">
      <el-form-item><el-input v-model="query.warehouseName" placeholder="仓库名" clearable /></el-form-item>
      <el-form-item><el-button type="primary" @click="loadData">查询</el-button></el-form-item>
    </el-form>
    <el-table :data="list" border>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="warehouseCode" label="编码" width="140" />
      <el-table-column prop="warehouseName" label="名称" />
      <el-table-column prop="address" label="地址" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" />
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑仓库' : '新增仓库'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="编码"><el-input v-model="form.warehouseCode" placeholder="如 WH001" /></el-form-item>
        <el-form-item label="名称"><el-input v-model="form.warehouseName" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="form.address" /></el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="停用" />
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" /></el-form-item>
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
import { pageWarehouse, addWarehouse, updateWarehouse, deleteWarehouse } from '@/api/basic/warehouse'

const query = reactive({ pageNum: 1, pageSize: 10, warehouseName: '' })
const list = ref<any[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const form = reactive<any>({ id: null, warehouseCode: '', warehouseName: '', address: '', status: 1, remark: '' })

async function loadData() {
  const res: any = await pageWarehouse(query)
  list.value = res.data.list
  total.value = res.data.total
}

function onAdd() {
  Object.assign(form, { id: null, warehouseCode: '', warehouseName: '', address: '', status: 1, remark: '' })
  dialogVisible.value = true
}

function onEdit(row: any) {
  Object.assign(form, row)
  dialogVisible.value = true
}

async function onSubmit() {
  if (!form.warehouseCode) { ElMessage.warning('请输入编码'); return }
  if (!form.warehouseName) { ElMessage.warning('请输入名称'); return }
  if (form.id) await updateWarehouse(form)
  else await addWarehouse(form)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadData()
}

async function onToggle(row: any) {
  const newStatus = row.status === 1 ? 0 : 1
  await updateWarehouse({ id: row.id, status: newStatus })
  ElMessage.success('已更新')
  loadData()
}

async function onDelete(row: any) {
  await ElMessageBox.confirm(`确定删除仓库 "${row.warehouseName}" 吗?`, '提示', { type: 'warning' })
  await deleteWarehouse([row.id])
  ElMessage.success('已删除')
  loadData()
}

onMounted(loadData)
</script>
