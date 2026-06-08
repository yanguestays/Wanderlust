import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import UserProfile from '../views/user/UserProfile.vue'
import { useUserStore } from '@/stores/userStore' 

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/auth/login.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/auth/Register.vue')
    },
    // 🔥🔥🔥 修复点 1：详情页路由 (必须加！)
    {
      path: '/dest/:id',
      name: 'destination-detail',
      component: () => import('../views/destination/DestinationDetail.vue')
    },
    // 🔥🔥🔥 修复点 2：个人中心路由
    {
      path: '/profile',
      name: 'profile',
      component: UserProfile, 
      meta: { requiresAuth: true }
    },
    // 管理员路由
    {
      path: '/admin',
      component: () => import('../views/admin/AdminLayout.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
      children: [
        {
          path: '', 
          redirect: '/admin/data' 
        },
        {
          path: 'data',
          name: 'admin-dashboard',
          component: () => import('../views/admin/DataManager.vue')
        },
        {
          path: 'users',
          name: 'admin-users',
          component: () => import('../views/admin/UserManager.vue')
        }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore() 
  if (to.meta.requiresAuth && !userStore.token) {
    return next('/login')
  }
  const userRole = userStore.user?.role ? userStore.user.role.toUpperCase() : ''
  if (to.meta.requiresAdmin && userRole !== 'ADMIN') {
    return next('/')
  }
  next()
})

export default router