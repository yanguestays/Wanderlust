<template>
  <nav class="navbar" :class="{ 'scrolled': isScrolled }">
    <div class="logo">
      WanderLust <span class="ai-badge">AI</span>
    </div>

    <div class="actions">
      <div v-if="userStore.token" class="user-profile">
        <el-dropdown trigger="click">
          <div class="avatar-wrapper">
            <el-avatar :size="40" :src="userStore.user?.avatar || defaultAvatar" />
            <span class="username">{{ userStore.user?.username || '用户' }}</span>
            <el-icon class="el-icon--right"><arrow-down /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item icon="User" @click="$router.push('/profile')">个人中心</el-dropdown-item>
              
              <el-dropdown-item 
                v-if="userStore.user?.role && userStore.user.role.toUpperCase() === 'ADMIN'" 
                icon="Setting" 
                @click="$router.push('/admin')"
                divided
              >
                <span style="color: #E6A23C; font-weight: bold;">后台管理</span>
              </el-dropdown-item>

              <el-dropdown-item divided icon="SwitchButton" @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>

      <div v-else class="auth-buttons">
        <el-button link class="login-btn" @click="$router.push('/login')">登录</el-button>
        <el-button type="primary" round class="register-btn" @click="$router.push('/register')">
          注册账号
        </el-button>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useUserStore } from '@/stores/userStore'
import { useRouter } from 'vue-router'
import { ArrowDown, User, Setting, SwitchButton } from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter()
const isScrolled = ref(false)

// 🔥 修复：换成稳定的默认头像，解决控制台 ERR_CONNECTION_CLOSED 报错
const defaultAvatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=Felix'

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

// 监听页面滚动
const handleScroll = () => {
  isScrolled.value = window.scrollY > 50
}

onMounted(() => window.addEventListener('scroll', handleScroll))
onUnmounted(() => window.removeEventListener('scroll', handleScroll))
</script>

<style scoped>
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 70px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 40px;
  z-index: 1000;
  transition: all 0.3s ease;
  background: transparent; 
  color: white;
  box-sizing: border-box;
}

.navbar.scrolled {
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(10px);
}

.logo {
  font-size: 24px;
  font-weight: 700;
  letter-spacing: 1px;
  font-family: 'Helvetica Neue', sans-serif;
  cursor: pointer;
}

.ai-badge {
  font-size: 12px;
  background: linear-gradient(45deg, #ff00cc, #3333ff);
  padding: 2px 6px;
  border-radius: 4px;
  vertical-align: top;
}

.avatar-wrapper {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: white;
  outline: none; /* 去除点击时的黑框 */
}

.username {
  margin: 0 8px;
  font-weight: 500;
}

.login-btn {
  color: white !important;
  font-size: 16px;
  margin-right: 15px;
}
.login-btn:hover {
  color: #409eff !important;
}
</style>