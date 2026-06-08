<template>
  <div class="admin-sidebar">
    <div class="logo">WanderLust <span>Admin</span></div>
    <el-menu
      :default-active="$route.path"
      router
      background-color="#1a1a1a"
      text-color="#fff"
      active-text-color="#fbc2eb"
    >
      <el-menu-item index="/admin/data">
        <el-icon><Management /></el-icon>
        <span>目的地管理</span>
      </el-menu-item>
      <el-menu-item index="/admin/users">
        <el-icon><User /></el-icon>
        <span>用户管理</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script setup>
import { Management, User } from '@element-plus/icons-vue'
</script>

<style scoped>
.admin-sidebar { width: 240px; height: 100vh; background: #1a1a1a; border-right: 1px solid #333; }
.logo { padding: 30px; font-size: 1.5rem; font-weight: bold; color: white; border-bottom: 1px solid #333; }
.logo span { color: #fbc2eb; }
</style>