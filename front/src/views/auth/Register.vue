<template>
  <div class="auth-container">
    <div class="video-bg">
      <video class="bg-video" autoplay loop muted playsinline>
        <source :src="heroVideo" type="video/mp4">
      </video>
      <div class="overlay"></div>
    </div>

    <div class="auth-card">
      <h2 class="auth-title">Join <span class="highlight">WanderLust</span></h2>
      <p class="auth-subtitle">开启你的专属旅程</p>
      
      <el-form :model="form" @submit.prevent="handleRegister" size="large" class="auth-form">
        <el-form-item>
          <el-input 
            v-model="form.username" 
            placeholder="设置用户名" 
            prefix-icon="User" 
            class="glass-input"
          />
        </el-form-item>
        <el-form-item>
          <el-input 
            v-model="form.password" 
            type="password" 
            placeholder="设置密码" 
            prefix-icon="Lock" 
            show-password 
            class="glass-input"
          />
        </el-form-item>
        <el-form-item>
          <el-input 
            v-model="confirmPassword" 
            type="password" 
            placeholder="确认密码" 
            prefix-icon="Lock" 
            show-password 
            class="glass-input"
          />
        </el-form-item>
        
        <el-button type="success" class="submit-btn" @click="handleRegister" :loading="loading">
          立即注册
        </el-button>
        
        <div class="links">
          <span>已有账号？</span>
          <router-link to="/login" class="login-link">去登录</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import api from '@/api'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
// 🔥🔥 修正 2：正确引入视频资源
import heroVideo from '@/assets/videos/hero.mp4'

const form = ref({ username: '', password: '' })
const confirmPassword = ref('')
const loading = ref(false)
const router = useRouter()

const handleRegister = async () => {
  // 基础校验
  if (!form.value.username || !form.value.password) {
    return ElMessage.warning('请填写完整信息')
  }
  if (form.value.password !== confirmPassword.value) {
    return ElMessage.error('两次输入的密码不一致')
  }

  loading.value = true
  try {
    // 发送请求
    const res = await api.post('/auth/register', form.value)
    
    // 🔥🔥 调试关键：在控制台打印响应结果
    console.log('后端响应:', res.data);

    // 判断逻辑：根据你后端的 Result 结构，成功可能是 200 也可能是 1，请以控制台打印的为准
    if (res.data.code === 200 || res.data.code === 1) {
      ElMessage.success('注册成功！请登录')
      router.push('/login')
    } else {
      ElMessage.error(res.data.msg || '注册失败')
    }
  } catch (error) {
    // 🔥🔥 调试关键：打印完整的错误信息
    console.error('请求发生错误:', error);
    
    if (error.response) {
        // 请求已发出，但服务器响应的状态码不在 2xx 范围内
        console.error('错误状态码:', error.response.status);
        console.error('错误数据:', error.response.data);
        ElMessage.error(`注册失败: ${error.response.data.msg || '服务器错误'}`);
    } else if (error.request) {
        // 请求已发出，但没有收到响应 (通常是后端没启动)
        ElMessage.error('无法连接到服务器，请检查后端是否启动')
    } else {
        ElMessage.error('请求配置错误')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 容器 */
.auth-container {
  height: 100vh;
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
}

/* 🔥🔥 修正 3：添加视频背景样式 (复用 Login.vue 的写法) */
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
  background: rgba(0, 0, 0, 0.6); /* 遮罩层，让文字更清晰 */
}

/* 卡片样式 - 提高 z-index 确保在视频上方 */
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

.auth-title {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 10px;
}
.highlight { color: #8fd3f4; }
.auth-subtitle {
  color: rgba(255, 255, 255, 0.6);
  margin-bottom: 30px;
  font-size: 0.9rem;
}

/* 输入框样式 */
:deep(.glass-input .el-input__wrapper) {
  background-color: rgba(0, 0, 0, 0.3) !important;
  box-shadow: none !important;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 8px 15px;
  transition: all 0.3s;
}
:deep(.glass-input .el-input__wrapper.is-focus) {
  border-color: #8fd3f4;
  background-color: rgba(0, 0, 0, 0.5) !important;
}
:deep(.glass-input .el-input__inner) {
  color: #fff;
  height: 40px;
}

/* 按钮样式 */
.submit-btn {
  width: 100%;
  margin-top: 10px;
  background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%);
  border: none;
  font-weight: bold;
  height: 45px;
  border-radius: 8px;
  font-size: 1rem;
}
.submit-btn:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}

.links {
  margin-top: 20px;
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.6);
}
.login-link {
  color: #84fab0;
  text-decoration: none;
  font-weight: 600;
  margin-left: 5px;
}
.login-link:hover { text-decoration: underline; }
</style>