<template>
  <div class="auth-container">
    <div class="video-bg">
      <video class="bg-video" autoplay loop muted playsinline>
        <source :src="heroVideo" type="video/mp4">
      </video>
      <div class="overlay"></div>
    </div>

    <div class="auth-card">
      <h2 class="auth-title">WanderLust <span class="dot">.</span> Login</h2>
      <p class="auth-subtitle">欢迎回到你的旅程</p>
      
      <el-form :model="form" @submit.prevent="handleLogin" size="large" class="auth-form">
        <el-form-item>
          <el-input 
            v-model="form.username" 
            placeholder="用户名" 
            prefix-icon="User" 
            class="glass-input"
          />
        </el-form-item>
        <el-form-item>
          <el-input 
            v-model="form.password" 
            type="password" 
            placeholder="密码" 
            prefix-icon="Lock" 
            show-password 
            class="glass-input"
          />
        </el-form-item>
        
        <el-button type="primary" class="submit-btn" @click="handleLogin" :loading="loading">
          立即登录
        </el-button>
        
        <div class="links">
          <span>还没有账号？</span>
          <router-link to="/register" class="register-link">去注册</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import api from '@/api'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/userStore' 
import { ElMessage } from 'element-plus'
import heroVideo from '@/assets/videos/hero.mp4' // 确保你的视频路径正确

const form = ref({ username: '', password: '' })
const loading = ref(false)
const router = useRouter()
const userStore = useUserStore()

const handleLogin = async () => {
  if (!form.value.username || !form.value.password) {
    return ElMessage.warning('请输入用户名和密码')
  }

  loading.value = true
  try {
    const res = await api.post('/auth/login', form.value)
    
    // 你的后端是用 code 200 表示成功
    if (res.data.code === 200) { 
      // 🔥 修复点 1: 数据结构解析
      // 你的后端返回的是扁平结构，不是 { user: {...} }，不要乱加层级！
      const responseData = res.data.data
      const token = responseData.token
      
      // 存入 Pinia
      userStore.login(responseData, token)
      
      ElMessage.success('欢迎回来 ' + responseData.username)

      // 🔥 修复点 2: 角色分流跳转
      // 检查 role 是否为 ADMIN (注意大小写，后端存的是大写)
      if (responseData.role === 'ADMIN') {
        console.log('检测到管理员，跳转后台...')
        router.push('/admin') // 跳去后台
      } else {
        router.push('/') // 普通用户跳去首页
      }
      
    } else {
      ElMessage.error(res.data.msg || '登录失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('无法连接到服务器')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 保持你原来的样式 */
.auth-container {
  height: 100vh;
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
}
.video-bg {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  z-index: 1;
}
.bg-video {
  width: 100%; height: 100%;
  object-fit: cover;
}
.overlay {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(0, 0, 0, 0.5);
}
.auth-card {
  position: relative;
  z-index: 2;
  width: 400px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
  text-align: center;
  color: #fff;
}
.auth-title { font-size: 2rem; font-weight: 700; margin-bottom: 10px; }
.dot { color: #fbc2eb; } 
.auth-subtitle { color: rgba(255, 255, 255, 0.6); margin-bottom: 30px; font-size: 0.9rem; }

:deep(.glass-input .el-input__wrapper) {
  background-color: rgba(0, 0, 0, 0.3) !important;
  box-shadow: none !important;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 8px 15px;
  transition: all 0.3s;
}
:deep(.glass-input .el-input__wrapper.is-focus) {
  border-color: #a18cd1;
  background-color: rgba(0, 0, 0, 0.5) !important;
}
:deep(.glass-input .el-input__inner) { color: #fff; height: 40px; }

.submit-btn {
  width: 100%;
  margin-top: 10px;
  background: linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%);
  border: none;
  font-weight: bold;
  height: 45px;
  border-radius: 8px;
  font-size: 1rem;
}
.submit-btn:hover { opacity: 0.9; transform: translateY(-1px); }

.links { margin-top: 20px; font-size: 0.9rem; color: rgba(255, 255, 255, 0.6); }
.register-link { color: #fbc2eb; text-decoration: none; font-weight: 600; margin-left: 5px; }
.register-link:hover { text-decoration: underline; }
</style>