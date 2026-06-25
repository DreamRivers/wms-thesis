<template>
  <el-card>
    <div class="page-toolbar">
      <h3>🛡️ 角色管理</h3>
      <el-button type="primary" @click="onAdd">新增角色</el-button>
    </div>
    <el-form :inline="true">
      <el-form-item><el-input v-model="query.roleName" placeholder="角色名" clearable /></el-form-item>
      <el-form-item><el-button type="primary" @click="loadData">查询</el-button></el-form-item>
    </el-form>
    <el-table :data="list" border>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="roleName" label="角色名" />
      <el-table-column prop="roleCode" label="编码" />
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" />
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="280">
        <template #default="{ row }">
          <el-button size="small" @click="onEdit(row)">编辑</el-button>
          <el-button size="small" type="primary" @click="onAssignMenus(row)">分配权限</el-button>
          <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'" @click="onToggle(row)">
            {{ row.status === 1 ? '停用' : '启用' }}
          </el-button>
          <el-button size="small" type="danger" @click="onDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total"
      layout="total,prev,pager,next" @current-change="loadData" style="margin-top:16px" />

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑角色' : '新增角色'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="角色名"><el-input v-model="form.roleName" /></el-form-item>
        <el-form-item label="编码"><el-input v-model="form.roleCode" placeholder="如 ADMIN/WMS_ADMIN" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
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

    <el-dialog v-model="permVisible" :title="`分配权限: ${currentRole?.roleName || ''}`" width="500px" @open="onPermOpen">
      <div style="margin-bottom:12px">
        <el-button size="small" type="primary" @click="onCheckAll">全选</el-button>
        <el-button size="small" @click="onUncheckAll">清空</el-button>
      </div>
      <el-tree
        ref="permTreeRef"
        :data="menuTree"
        :props="{ label: 'menuName', children: 'children' }"
        show-checkbox
        node-key="id"
        :default-checked-keys="checkedMenuIds"
        :default-expand-all="true"
        check-strictly
        style="margin-top:12px"
      />
      <template #footer>
        <el-button @click="permVisible = false">取消</el-button>
        <el-button type="primary" @click="onPermSubmit">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { pageRole, addRole, updateRole, deleteRole, getRoleMenus, assignRoleMenus } from '@/api/system/role'

const query = reactive({ pageNum: 1, pageSize: 10, roleName: '' })
const list = ref<any[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const form = reactive<any>({ id: null, roleName: '', roleCode: '', sort: 0, status: 1, remark: '' })

// 权限分配弹窗
const permVisible = ref(false)
const permTreeRef = ref<any>()
const menuTree = ref<any[]>([])
const checkedMenuIds = ref<number[]>([])
const currentRole = ref<any>(null)

async function loadData() {
  const res: any = await pageRole(query)
  list.value = res.data.list
  total.value = res.data.total
}

function onAdd() {
  Object.assign(form, { id: null, roleName: '', roleCode: '', sort: 0, status: 1, remark: '' })
  dialogVisible.value = true
}

function onEdit(row: any) {
  Object.assign(form, row)
  dialogVisible.value = true
}

async function onSubmit() {
  if (!form.roleName) { ElMessage.warning('请输入角色名'); return }
  if (!form.roleCode) { ElMessage.warning('请输入角色编码'); return }
  if (form.id) await updateRole(form)
  else await addRole(form)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadData()
}

async function onToggle(row: any) {
  const newStatus = row.status === 1 ? 0 : 1
  await updateRole({ id: row.id, status: newStatus })
  ElMessage.success('已更新')
  loadData()
}

async function onDelete(row: any) {
  await ElMessageBox.confirm(`确定删除角色 "${row.roleName}" 吗?`, '提示', { type: 'warning' })
  await deleteRole([row.id])
  ElMessage.success('已删除')
  loadData()
}

// ========= 权限分配 =========
async function buildTree(list: any[]): Promise<any[]> {
  const map: Record<number, any> = {}
  list.forEach(m => { map[m.id] = { ...m, children: [] } })
  const roots: any[] = []
  list.forEach(m => {
    if (m.parentId && map[m.parentId]) {
      map[m.parentId].children.push(map[m.id])
    } else {
      roots.push(map[m.id])
    }
  })
  return roots
}

async function onAssignMenus(row: any) {
  currentRole.value = row
  permVisible.value = true
}

async function onPermOpen() {
  // 拉菜单 + 当前角色已勾选的菜单
  const [mRes, rRes] = await Promise.all([
    request.get('/system/menu/list'),
    getRoleMenus(currentRole.value.id)
  ])
  menuTree.value = await buildTree(mRes.data || [])
  checkedMenuIds.value = (rRes.data || []).map((x: any) => Number(x))
  // 等两次 nextTick: 一次给 el-tree 渲染, 一次给勾选
  await nextTick()
  await nextTick()
  if (permTreeRef.value) {
    permTreeRef.value.setCheckedKeys(checkedMenuIds.value, false)
  }
}

function onCheckAll() {
  if (!permTreeRef.value) return
  const allIds: number[] = []
  const walk = (nodes: any[]) => nodes.forEach(n => { allIds.push(n.id); if (n.children) walk(n.children) })
  walk(menuTree.value)
  permTreeRef.value.setCheckedKeys(allIds)
}

function onUncheckAll() {
  if (!permTreeRef.value) return
  permTreeRef.value.setCheckedKeys([])
}

async function onPermSubmit() {
  if (!permTreeRef.value) return
  const checked = permTreeRef.value.getCheckedKeys() as number[]
  const halfChecked = permTreeRef.value.getHalfCheckedKeys() as number[]
  const all = [...checked, ...halfChecked]
  await assignRoleMenus({ roleId: currentRole.value.id, menuIds: all })
  ElMessage.success('权限已更新')
  permVisible.value = false
  // 刷新当前用户菜单 (如果改的是当前用户的角色)
  try {
    const userStore = (await import('@/stores/user')).useUserStore()
    if (userStore.userInfo && userStore.userInfo.userId) {
      await userStore.loadRoutes()
      // 更新 localStorage
      const paths = (userStore.routes || []).map((r: any) => (r.path || '').replace(/^\//, ''))
      localStorage.setItem('user_routes', JSON.stringify(paths))
      console.log('[Role] 路由已刷新:', paths)
      // 提示用户
      ElMessage.info('菜单已更新,部分页面可能需要重新登录生效')
    }
  } catch (e) {
    console.error('[Role] 刷新路由失败', e)
  }
}

onMounted(loadData)
</script>
