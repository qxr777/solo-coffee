import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../store/authStore'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/HomeView.vue')
    },
    {
      path: '/stores',
      name: 'stores',
      component: () => import('../views/StoreListView.vue')
    },
    {
      path: '/store/:id',
      name: 'store-detail',
      component: () => import('../views/StoreDetailView.vue')
    },
    {
      path: '/products',
      name: 'products',
      component: () => import('../views/ProductListView.vue')
    },
    {
      path: '/product/:id',
      name: 'product-detail',
      component: () => import('../views/ProductDetailView.vue')
    },
    {
      path: '/cart',
      name: 'cart',
      component: () => import('../views/CartView.vue')
    },
    {
      path: '/checkout',
      name: 'checkout',
      component: () => import('../views/CheckoutView.vue')
    },
    {
      path: '/orders',
      name: 'orders',
      component: () => import('../views/OrderHistoryView.vue')
    },
    {
      path: '/order/:id',
      name: 'order-detail',
      component: () => import('../views/OrderDetailView.vue')
    },    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/RegisterView.vue')
    },
    {
      path: '/forgot-password',
      name: 'forgot-password',
      component: () => import('../views/ForgotPasswordView.vue')
    },    {
      path: '/profile',
      name: 'profile',
      component: () => import('../views/ProfileView.vue')
    },
    {
      path: '/change-password',
      name: 'change-password',
      component: () => import('../views/ChangePasswordView.vue')
    },
    {
      path: '/member',
      name: 'member',
      component: () => import('../views/MemberCenterView.vue')
    },
    {
      path: '/settings',
      name: 'settings',
      component: () => import('../views/SettingsView.vue')
    },
    // These views will be implemented in the future
    // {
    //   path: '/pre-order',
    //   name: 'pre-order',
    //   component: () => import('../views/PreOrderView.vue')
    // },
    // {
    //   path: '/voice-order',
    //   name: 'voice-order',
    //   component: () => import('../views/VoiceOrderView.vue')
    // },
    {
      path: '/pre-order',
      name: 'pre-order',
      component: () => import('../views/PreOrderView.vue')
    },
    // These views will be implemented in the future
    // {
    //   path: '/notifications',
    //   name: 'notifications',
    {
      path: '/voice-order',
      name: 'voice-order',
      component: () => import('../views/VoiceOrderView.vue')
    },
    // These views will be implemented in the future
    // {
    //   path: '/notifications',
    //   name: 'notifications',
    //   component: () => import('../views/NotificationView.vue')
    // },
    // {
    //   path: '/settings',
    //   name: 'settings',
    //   component: () => import('../views/SettingsView.vue')
    // }
    // {
    //   path: '/settings',
    //   name: 'settings',
    //   component: () => import('../views/SettingsView.vue')
    // }
  ]
})

// 路由守卫
router.beforeEach((to, _from, next) => {
  const authStore = useAuthStore()
  const requiresAuth = ['checkout', 'profile', 'member', 'pre-order', 'voice-order', 'settings']
  
  if (requiresAuth.includes(to.name as string) && !authStore.isAuthenticated) {
    next('/login')
  } else {
    next()
  }
})

export default router