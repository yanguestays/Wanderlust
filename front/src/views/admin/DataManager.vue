<template>
  <div class="data-manager">
    <div class="header-actions">
      <h2>🌍 目的地知识库管理</h2>
      
      <el-upload
        class="upload-demo"
        :action="uploadUrl"
        :headers="uploadHeaders"
        :show-file-list="false"
        :on-success="handleSuccess"
        :on-error="handleError"
        :before-upload="beforeUpload"
        accept=".xlsx, .xls"
      >
        <el-button type="primary" icon="Upload">导入 Excel 更新知识库</el-button>
      </el-upload>
    </div>

    <el-divider />

    <el-alert title="上传 Excel 后，后台会自动调用智谱 AI 生成向量，请耐心等待 5-10 秒。" type="info" show-icon />
    
    <div class="empty-placeholder">
      <el-empty description="数据展示区域 (需自行对接列表接口)" />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/stores/userStore'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()

const uploadUrl = (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080') + '/admin/upload'

// 1. 设置请求头，携带 Token
const uploadHeaders = computed(() => {
  return {
    'Authorization': `Bearer ${userStore.token}`
  }
})

// 2. 上传前检查
const beforeUpload = (file) => {
  const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' || file.name.endsWith('.xlsx');
  if (!isExcel) {
    ElMessage.error('只能上传 xlsx 文件!')
    return false
  }
  return true
}

// 3. 成功回调
const handleSuccess = (response) => {
  if (response.code === 200) { // 假设后端返回标准 Result 结构
    ElMessage.success(response.data || '导入成功！知识库已更新。')
    // 这里可以加一行代码去刷新列表：fetchData()
  } else {
    ElMessage.error('导入失败：' + response.msg)
  }
}

// 4. 失败回调
const handleError = (err) => {
  ElMessage.error('上传请求失败，请检查网络或后端日志')
}
</script>

<style scoped>
.data-manager { padding: 20px; }
.header-actions { display: flex; justify-content: space-between; align-items: center; }
.empty-placeholder { margin-top: 50px; }
</style>