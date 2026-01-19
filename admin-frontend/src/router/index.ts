
import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import Layout from '../components/layout/Layout.vue'
import Login from '../views/auth/Login.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: Layout,
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('../views/dashboard/Dashboard.vue'),
        meta: { title: '仪表盘' }
      },
      {
        path: 'sales-analytics',
        name: 'SalesAnalytics',
        component: () => import('../views/sales/SalesAnalytics.vue'),
        meta: { title: '销售分析' }
      },
      {
        path: 'employee-scheduling',
        name: 'EmployeeScheduling',
        component: () => import('../views/employee/EmployeeScheduling.vue'),
        meta: { title: '员工排班' }
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('../views/order/OrderList.vue'),
        meta: { title: '订单管理' }
      },
      {
        path: 'orders/:id',
        name: 'OrderDetail',
        component: () => import('../views/order/OrderDetail.vue'),
        meta: { title: '订单详情' }
      },
      {
        path: 'members',
        name: 'Members',
        component: () => import('../views/customer/CustomerList.vue'),
        meta: { title: '会员管理' }
      },
      {
        path: 'products',
        name: 'Products',
        component: () => import('../views/product/ProductList.vue'),
        meta: { title: '商品管理' }
      },
      {
        path: 'products/:id',
        name: 'ProductDetail',
        component: () => import('../views/product/ProductDetail.vue'),
        meta: { title: '商品详情' }
      },
      {
        path: 'inventory',
        name: 'Inventory',
        component: () => import('../views/inventory/InventoryList.vue'),
        meta: { title: '库存管理' }
      },
      {
        path: 'customers',
        name: 'Customers',
        component: () => import('../views/customer/CustomerList.vue'),
        meta: { title: '客户管理' }
      },
      {
        path: 'stores',
        name: 'Stores',
        component: () => import('../views/store/StoreList.vue'),
        meta: { title: '门店管理' }
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('../views/settings/Settings.vue'),
        meta: { title: '系统设置' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const requiresAuth = to.meta.requiresAuth
  const isAuthenticated = localStorage.getItem('token') !== null

  if (requiresAuth && !isAuthenticated) {
    next('/login')
  } else {
    next()
  }
})

export default router
