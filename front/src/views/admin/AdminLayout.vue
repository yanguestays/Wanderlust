<template>
  <div class="admin-container">
    <Adminbar />
    <div class="main-body">
      <header class="top-nav">
        <span>当前位置：{{ $route.name === 'admin-users' ? '用户管理' : '数据管理' }}</span>
        <el-button link @click="$router.push('/')">回到首页</el-button>
      </header>
      <div class="view-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import Adminbar from '@/components/Adminbar.vue'
</script>

<style scoped>
.admin-container { display: flex; height: 100vh; }
.main-body { flex: 1; display: flex; flex-direction: column; background: #f0f2f5; overflow-y: auto; }
.top-nav { height: 60px; background: white; display: flex; align-items: center; justify-content: space-between; padding: 0 20px; box-shadow: 0 1px 4px rgba(0,0,0,0.1); }
.view-content { padding: 20px; }
</style>