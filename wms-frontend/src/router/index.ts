import { createRouter, createWebHistory } from 'vue-router'

const Layout = () => import('@/layout/Layout.vue')

const routes = [
  {
    path: '/login',
    component: () => import('@/views/login/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/Dashboard.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      // 系统管理
      {
        path: 'system/user',
        component: () => import('@/views/system/User.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'system/role',
        component: () => import('@/views/system/Role.vue'),
        meta: { title: '角色管理', icon: 'UserFilled' }
      },
      {
        path: 'system/menu',
        component: () => import('@/views/system/Menu.vue'),
        meta: { title: '菜单管理', icon: 'Menu' }
      },
      {
        path: 'system/log',
        component: () => import('@/views/system/OperationLog.vue'),
        meta: { title: '操作日志', icon: 'Document' }
      },
      // 基础数据
      { path: 'basic/goods', component: () => import('@/views/basic/Goods.vue'), meta: { title: '商品管理', icon: 'Goods' } },
      { path: 'basic/category', component: () => import('@/views/basic/Category.vue'), meta: { title: '商品分类', icon: 'Folder' } },
      { path: 'basic/warehouse', component: () => import('@/views/basic/Warehouse.vue'), meta: { title: '仓库管理', icon: 'House' } },
      { path: 'basic/location', component: () => import('@/views/basic/Location.vue'), meta: { title: '库位管理', icon: 'Grid' } },
      { path: 'basic/supplier', component: () => import('@/views/basic/Supplier.vue'), meta: { title: '供应商管理', icon: 'Avatar' } },
      // 入库
      { path: 'inbound/order', component: () => import('@/views/inbound/Order.vue'), meta: { title: '入库单列表', icon: 'List' } },
      { path: 'inbound/audit', component: () => import('@/views/inbound/Audit.vue'), meta: { title: '入库单审核', icon: 'CircleCheck' } },
      // 出库
      { path: 'outbound/order', component: () => import('@/views/outbound/Order.vue'), meta: { title: '出库单列表', icon: 'List' } },
      { path: 'outbound/apply', component: () => import('@/views/outbound/Apply.vue'), meta: { title: '出库申请', icon: 'EditPen' } },
      { path: 'outbound/approval', component: () => import('@/views/outbound/Approval.vue'), meta: { title: '出库审批', icon: 'CircleCheck' } },
      // 库存
      { path: 'stock/list', component: () => import('@/views/stock/List.vue'), meta: { title: '实时库存', icon: 'Box' } },
      { path: 'stock/record', component: () => import('@/views/stock/Record.vue'), meta: { title: '库存流水', icon: 'Tickets' } },
      { path: 'stock/taking', component: () => import('@/views/stock/Taking.vue'), meta: { title: '盘点管理', icon: 'Histogram' } },
      { path: 'stock/warning', component: () => import('@/views/stock/Warning.vue'), meta: { title: '库存预警', icon: 'Warning' } },
      // 报表
      { path: 'report/inbound', component: () => import('@/views/report/Inbound.vue'), meta: { title: '入库统计', icon: 'TrendCharts' } },
      { path: 'report/outbound', component: () => import('@/views/report/Outbound.vue'), meta: { title: '出库统计', icon: 'PieChart' } },
      { path: 'report/inventory', component: () => import('@/views/report/Inventory.vue'), meta: { title: '库存报表', icon: 'DataLine' } },
      { path: 'profile', component: () => import('@/views/profile/Profile.vue'), meta: { title: '个人中心' } }
    ]
  },
  { path: '/403', component: () => import('@/views/error/403.vue') },
  { path: '/:pathMatch(.*)*', redirect: '/dashboard' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path === '/login') return next()
  if (!token) return next('/login')
  next()
})

export default router
