<template>
  <div class="user-list-card">
    <h2>👤 注册用户管理</h2>
    <el-table :data="users" v-loading="loading" stripe style="width: 100%">
      <el-table-column prop="id" label="UID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="role" label="身份角色">
        <template #default="scope">
          <el-tag :type="scope.row.role === 'ADMIN' ? 'danger' : 'info'">{{ scope.row.role }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="管理操作">
        <template #default="scope">
          <el-button 
            type="danger" 
            size="small" 
            :disabled="scope.row.username === 'admin'"
            @click="deleteUser(scope.row)"
          >注销</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const users = ref([])
const loading = ref(false)

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await api.get('/admin/users')
    users.value = res.data.data
  } catch (e) { ElMessage.error("获取数据失败") }
  finally { loading.value = false }
}

const deleteUser = (user) => {
  ElMessageBox.confirm(`确定要彻底注销用户 ${user.username} 吗？`, '危险操作', { type: 'error' })
    .then(async () => {
      const res = await api.delete(`/admin/users/${user.id}`)
      if (res.data.code === 200) {
        ElMessage.success("注销成功")
        loadUsers()
      } else { ElMessage.error(res.data.message) }
    })
}

onMounted(loadUsers)
</script>

<style scoped>
.user-list-card { background: white; padding: 20px; border-radius: 8px; }
</style>