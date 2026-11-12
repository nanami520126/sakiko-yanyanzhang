import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    redirect: '/employee',
    children: [
      {
        path: 'employee',
        name: 'Employee',
        component: () => import('@/views/employee/EmployeeList.vue')
      },
      {
        path: 'category',
        name: 'Category',
        component: () => import('@/views/category/CategoryList.vue')
      },
      {
        path: 'dish',
        name: 'Dish',
        component: () => import('@/views/dish/DishList.vue')
      },
      {
        path: 'setmeal',
        name: 'Setmeal',
        component: () => import('@/views/setmeal/SetmealList.vue')
      },
      {
        path: 'order',
        name: 'Order',
        component: () => import('@/views/order/OrderList.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：未登录跳转登录页
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
