<template>
  <el-container class="layout-container">
    <el-aside :width="collapse ? '64px' : '220px'" class="aside">
      <div class="logo">
        <span v-if="!collapse">📦 WMS 仓储</span>
        <span v-else>📦</span>
      </div>
      <el-menu :default-active="active" :collapse="collapse" class="menu" background-color="#001529" text-color="#fff" active-text-color="#409eff">
        <template v-for="m in dynamicMenus" :key="m.path">
          <el-menu-item :index="m.path" @click="goMenu(m)">
            <el-icon><component :is="m.meta?.icon || 'Menu'" /></el-icon>
            <template #title>{{ m.meta?.title || m.name }}</template>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <el-icon class="toggle" @click="collapse = !collapse">
          <Fold v-if="!collapse" />
          <Expand v-else />
        </el-icon>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>{{ $route.meta.title }}</el-breadcrumb-item>
        </el-breadcrumb>
        <div class="right">
          <el-dropdown @command="handleCommand">
            <span class="user">
              <el-avatar :size="32" :src="userStore.userInfo.avatar" />
              {{ userStore.userInfo.realName || userStore.userInfo.username }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main>
        <router-view v-slot="{ Component }">
          <keep-alive :include="cachedViews">
            <component :is="Component" />
          </keep-alive>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const collapse = ref(false)

// 动态菜单: 从 userStore.routes 转换, 全部拍平为一级
const dynamicMenus = computed(() => {
  const home = { path: '/dashboard', name: '首页', meta: { title: '首页', icon: 'HomeFilled' } }
  const result: any[] = [home]
  // 后端返回的树: 跳过父级, 把所有叶子节点(无children)拍平
  const flatten = (nodes: any[]) => {
    nodes.forEach((n: any) => {
      if (n.path === '/dashboard') return
      if (n.children && n.children.length) {
        flatten(n.children)
      } else {
        result.push(n)
      }
    })
  }
  flatten(userStore.routes || [])
  return result
})

const active = computed(() => route.path)

// 需要被 keep-alive 缓存的页面 name
const cachedViews = [
  'Dashboard',
  'User', 'Role',
  'Goods', 'Category', 'Warehouse', 'Location',
  'InboundOrder',
  'OutboundOrder', 'OutboundApply', 'OutboundApproval',
  'StockList', 'StockRecord', 'StockTaking', 'StockWarning',
  'ReportInbound', 'ReportOutbound', 'ReportInventory',
  'Profile'
]

onMounted(async () => {
  if (!userStore.userInfo.userId) {
    try { await userStore.loadInfo() } catch (e) {}
  }
  // 确保菜单路由已加载
  if (!userStore.routes || !userStore.routes.length) {
    try { await userStore.loadRoutes() } catch (e) { console.error('loadRoutes failed', e) }
  }
})

function goMenu(m: any) {
  // 后端 path 是 /basic/goods, 前端路由是 basic/goods (子路由相对)
  const p = m.path.startsWith('/') ? m.path.substring(1) : m.path
  console.log('[goMenu] click', m.path, '->', p, 'current route:', route.path)
  // 防止重复点击同一路径导致 reject
  if (route.path === '/' + p) {
    console.log('[goMenu] 已经在该页面')
    return
  }
  router.push('/' + p).catch((e) => console.error('[goMenu] push error', e))
}

async function handleCommand(cmd: string) {
  if (cmd === 'logout') {
    await userStore.doLogout()
    router.push('/login')
  } else if (cmd === 'profile') {
    router.push('/profile')
  }
}
</script>

<style scoped>
.layout-container { height: 100vh; }
.aside { background: #001529; transition: width .3s; }
.logo { color: #fff; padding: 16px; font-size: 18px; text-align: center; font-weight: bold; }
.menu { border-right: 0; height: calc(100vh - 60px); }
.header { background: #fff; display: flex; align-items: center; box-shadow: 0 1px 4px rgba(0,21,41,.08); }
.toggle { font-size: 20px; cursor: pointer; margin-right: 16px; }
.right { margin-left: auto; }
.user { display: flex; align-items: center; gap: 8px; cursor: pointer; }
.el-main { background: #f0f2f5; padding: 16px; }
</style>
