<template>
  <el-card>
    <div class="page-toolbar">
      <el-form :inline="true" :model="query">
        <el-form-item><el-input v-model="query.goodsName" placeholder="商品名称" clearable /></el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" @click="onAdd">新增商品</el-button>
    </div>
    <el-table :data="list" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="goodsCode" label="商品编码" width="120" />
      <el-table-column prop="goodsName" label="商品名称" min-width="160" />
      <el-table-column label="分类" width="120">
        <template #default="{ row }">{{ getCategoryName(row.categoryId) }}</template>
      </el-table-column>
      <el-table-column prop="unit" label="单位" width="60" />
      <el-table-column prop="safetyStock" label="安全库存" width="100" />
      <el-table-column prop="purchasePrice" label="进价" width="100" />
      <el-table-column prop="salePrice" label="售价" width="100" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
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
      layout="total,sizes,prev,pager,next,jumper" :page-sizes="[10,20,50]" @size-change="loadData" @current-change="loadData" style="margin-top:16px" />

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑商品' : '新增商品'" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="商品编码" prop="goodsCode"><el-input v-model="form.goodsCode" /></el-form-item>
        <el-form-item label="商品名称" prop="goodsName"><el-input v-model="form.goodsName" /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width:100%" clearable>
            <el-option v-for="c in categories" :key="c.id" :label="c.categoryName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="单位"><el-input v-model="form.unit" /></el-form-item>
        <el-form-item label="安全库存"><el-input-number v-model="form.safetyStock" :min="0" /></el-form-item>
        <el-form-item label="临期预警天"><el-input-number v-model="form.warnDays" :min="0" /></el-form-item>
        <el-form-item label="进价"><el-input-number v-model="form.purchasePrice" :precision="2" :min="0" /></el-form-item>
        <el-form-item label="售价"><el-input-number v-model="form.salePrice" :precision="2" :min="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="onSubmit">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageGoods, addGoods, updateGoods, deleteGoods, pageCategory } from '@/api/basic/goods'

const query = reactive({ pageNum: 1, pageSize: 10, goodsName: '' })
const list = ref<any[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref()
const form = reactive<any>({ id: null, goodsCode: '', goodsName: '', categoryId: null, unit: '个', safetyStock: 0, warnDays: 30, purchasePrice: 0, salePrice: 0, status: 1 })
const rules = { goodsCode: [{ required: true, message: '请输入编码' }], goodsName: [{ required: true, message: '请输入名称' }] }
const categories = ref<any[]>([])

function getCategoryName(id: number): string {
  if (!id) return '-'
  const c = categories.value.find(c => c.id === id)
  return c ? c.categoryName : `#${id}`
}

async function loadData() {
  const res: any = await pageGoods(query)
  list.value = res.data.list
  total.value = res.data.total
}

async function loadCategories() {
  const r: any = await pageCategory()
  categories.value = r.data || []
}

function reset() { query.goodsName = ''; loadData() }

async function onAdd() {
  Object.assign(form, { id: null, goodsCode: '', goodsName: '', categoryId: null, unit: '个', safetyStock: 0, warnDays: 30, purchasePrice: 0, salePrice: 0, status: 1 })
  dialogVisible.value = true
  await loadCategories()
}

async function onEdit(row: any) {
  Object.assign(form, row)
  dialogVisible.value = true
  await loadCategories()
}

async function onSubmit() {
  await formRef.value.validate()
  if (form.id) await updateGoods(form)
  else await addGoods(form)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadData()
}

async function onDelete(row: any) {
  await ElMessageBox.confirm(`确定删除【${row.goodsName}】?`, '提示')
  await deleteGoods([row.id])
  ElMessage.success('删除成功')
  loadData()
}

async function onToggle(row: any) {
  const newStatus = row.status === 1 ? 0 : 1
  await ElMessageBox.confirm(`确定${newStatus === 1 ? '启用' : '停用'}【${row.goodsName}】?`, '提示')
  await updateGoods({ ...row, status: newStatus })
  ElMessage.success('操作成功')
  loadData()
}

onMounted(async () => {
  await loadData()
  await loadCategories()
})
</script>
