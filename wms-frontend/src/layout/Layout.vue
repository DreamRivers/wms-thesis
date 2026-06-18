<template>
  <el-container class="layout-container">
    <el-aside :width="collapse ? '64px' : '220px'" class="aside">
      <div class="logo">
        <span v-if="!collapse">📦 WMS 仓储</span>
        <span v-else>📦</span>
      </div>
      <el-menu :default-active="active" :collapse="collapse" router class="menu" background-color="#001529" text-color="#fff" active-text-color="#409eff">
        <template v-for="m in menus" :key="m.path">
          <el-menu-item :index="m.path">
            <el-icon><component :is="m.icon" /></el-icon>
            <template #title>{{ m.title }}</template>
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
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const collapse = ref(false)

const menus = [
  { path: '/dashboard', title: '首页', icon: 'HomeFilled' },
  { path: '/system/user', title: '用户管理', icon: 'User' },
  { path: '/system/role', title: '角色管理', icon: 'UserFilled' },
  { path: '/basic/goods', title: '商品管理', icon: 'Goods' },
  { path: '/basic/warehouse', title: '仓库管理', icon: 'House' },
  { path: '/basic/location', title: '库位管理', icon: 'Grid' },
  { path: '/inbound/order', title: '入库管理', icon: 'Upload' },
  { path: '/outbound/order', title: '出库管理', icon: 'Download' },
  { path: '/stock/list', title: '实时库存', icon: 'Box' },
  { path: '/stock/warning', title: '库存预警', icon: 'Warning' },
  { path: '/stock/taking', title: '盘点管理', icon: 'Histogram' },
  { path: '/report/inbound', title: '报表统计', icon: 'DataAnalysis' }
]

const active = computed(() => route.path)

// 需要被 keep-alive 缓存的页面 name
const cachedViews = [
  'Dashboard',
  'User', 'Role', 'Menu', 'OperationLog',
  'Goods', 'Category', 'Warehouse', 'Location', 'Supplier',
  'InboundOrder', 'InboundAudit',
  'OutboundOrder', 'OutboundApply', 'OutboundApproval',
  'StockList', 'StockRecord', 'StockTaking', 'StockWarning',
  'ReportInbound', 'ReportOutbound', 'ReportInventory',
  'Profile'
]

onMounted(async () => {
  if (!userStore.userInfo.userId) {
    try { await userStore.loadInfo() } catch (e) {}
  }
})

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
