<template>
  <div class="layout" :style="{ backgroundImage: `url(${bgImage})` }">
    <!-- 左侧导航栏（毛玻璃） -->
    <aside class="sidebar">
      <div class="logo">缘缘庄餐饮系统</div>
      <el-menu
        class="menu"
        :default-active="$route.path"
        router
        background-color="transparent"
        text-color="#303133"
        active-text-color="#303133"
      >
        <el-menu-item index="/employee">
          <el-icon><User /></el-icon>
          <span>员工管理</span>
        </el-menu-item>
        <el-menu-item index="/category">
          <el-icon><Grid /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/dish">
          <el-icon><KnifeFork /></el-icon>
          <span>菜品管理</span>
        </el-menu-item>
        <el-menu-item index="/setmeal">
          <el-icon><Bowl /></el-icon>
          <span>套餐管理</span>
        </el-menu-item>
        <el-menu-item index="/order">
          <el-icon><Tickets /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
      </el-menu>

      <!-- 左下：登录管理 -->
      <div class="sidebar-bottom">
        <el-icon><UserFilled /></el-icon>
        <span class="username">{{ userStore.name || userStore.userName }}</span>
        <el-button link type="primary" @click="handleLogout">退出登录</el-button>
      </div>
    </aside>

    <!-- 右侧内容栏（毛玻璃） -->
    <main class="content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { logout } from '@/api/employee'
import { useUserStore } from '@/store/user'
const bgImage = 'https://sakikosama.oss-cn-beijing.aliyuncs.com/si-bg.webp'

const router = useRouter()
const userStore = useUserStore()

function handleLogout() {
  ElMessageBox.confirm('确认退出登录吗？', '提示', { type: 'warning' })
    .then(() => logout())
    .then(() => {
      userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/login')
    })
    .catch(() => {})
}
</script>

<style scoped>
.layout {
  height: 100%;
  display: flex;
  gap: 16px;
  padding: 16px;
  box-sizing: border-box;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

/* 左侧导航栏 */
.sidebar {
  width: 200px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  padding: 16px 8px;
  box-sizing: border-box;
  background: rgba(255, 255, 255, 0.35);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.45);
  border-radius: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.25);
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #303133;
  font-size: 16px;
  font-weight: bold;
}

.menu {
  flex: 1;
  border-right: none;
  background-color: transparent;
}

/* 选中菜单项：字体颜色不变，浅绿色实心框 */
.menu :deep(.el-menu-item.is-active) {
  color: #303133;
  background-color: #90ee90;
  border-radius: 8px;
}

.sidebar-bottom {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
}

.username {
  color: #303133;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 右侧内容栏 */
.content {
  flex: 1;
  min-width: 0;
  padding: 16px;
  box-sizing: border-box;
  overflow: auto;
  background: rgba(255, 255, 255, 0.35);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.45);
  border-radius: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.25);
}
</style>
