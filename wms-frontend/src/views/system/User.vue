<template>
  <el-card>
    <div class="page-toolbar">
      <h3>👤 用户管理</h3>
      <el-button type="primary" @click="onAdd">新增用户</el-button>
    </div>
    <el-form :inline="true">
      <el-form-item><el-input v-model="query.realName" placeholder="姓名" clearable /></el-form-item>
      <el-form-item><el-button type="primary" @click="loadData">查询</el-button></el-form-item>
    </el-form>
    <el-table :data="list" border>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="username" label="账号" />
      <el-table-column prop="realName" label="姓名" />
      <el-table-column prop="phone" label="电话" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="280">
        <template #default="{ row }">
          <el-button size="small" @click="onEdit(row)">编辑</el-button>
          <el-button size="small" @click="onResetPwd(row)">重置密码</el-button>
          <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'" @click="onToggle(row)">
            {{ row.status === 1 ? '停用' : '启用' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total"
      layout="total,prev,pager,next" @current-change="loadData" style="margin-top:16px" />

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑用户' : '新增用户'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="账号" v-if="!form.id"><el-input v-model="form.username" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.realName" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
        <el-form-item label="密码" v-if="!form.id"><el-input v-model="form.password" placeholder="留空默认 123456" /></el-form-item>
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
import { pageUser, addUser, updateUser, resetPwd, changeStatus } from '@/api/system/user'

const query = reactive({ pageNum: 1, pageSize: 10, realName: '', status: null as any })
const list = ref<any[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const form = reactive<any>({ id: null, username: '', realName: '', phone: '', email: '', password: '' })

async function loadData() {
  const res: any = await pageUser(query)
  list.value = res.data.list
  total.value = res.data.total
}

function onAdd() {
  Object.assign(form, { id: null, username: '', realName: '', phone: '', email: '', password: '' })
  dialogVisible.value = true
}

function onEdit(row: any) {
  Object.assign(form, row)
  dialogVisible.value = true
}

async function onSubmit() {
  if (form.id) await updateUser(form)
  else await addUser(form)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadData()
}

async function onResetPwd(row: any) {
  await ElMessageBox.confirm(`重置 ${row.realName || row.username} 的密码为 123456?`, '提示')
  await resetPwd(row.id)
  ElMessage.success('密码已重置')
}

async function onToggle(row: any) {
  const newStatus = row.status === 1 ? 0 : 1
  await changeStatus(row.id, newStatus)
  ElMessage.success('已更新')
  loadData()
}

onMounted(loadData)
</script>
