<template>
  <div class="home-container">
    <Navbar />

    <div class="hero-background">
      <video 
        class="bg-video" 
        autoplay 
        loop 
        muted 
        playsinline
      >
        <source :src="heroVideo" type="video/mp4">
        您的浏览器不支持视频播放。
      </video>
      <div class="overlay"></div>
    </div>

    <div class="content-wrapper">
      <div class="hero-section" :class="{ 'shrink': hasSearched }">
        <h1 class="main-title" v-if="!hasSearched">
          WanderLust <span class="gradient-text">AI</span>
        </h1>
        <p class="subtitle" v-if="!hasSearched">
          不仅仅是搜索，更是对未知的探索...
        </p>

        <div class="search-box-container">
          <el-input
            v-model="searchQuery"
            class="ai-search-input"
            placeholder="例如：想去一个像《星际穿越》里那样孤独的地方..."
            @keyup.enter="handleSearch"
            :prefix-icon="Search"
            size="large"
          >
            <template #append>
              <el-button class="search-btn" :loading="loading" @click="handleSearch">
                <el-icon><Right /></el-icon> 探索
              </el-button>
            </template>
          </el-input>
        </div>
      </div>

      <transition name="fade-up">
        <div class="results-section" v-if="destinations.length > 0 || loading">
          <div class="section-header">
            <h3>
              <span v-if="loading">🧠 AI 正在疯狂思考中...</span>
              <span v-else-if="hasSearched">为您推荐的目的地</span>
              <span v-else>📅 🔥 热门精选 (当前月份推荐)</span>
            </h3>
            <el-button v-if="hasSearched && !loading" link @click="resetSearch" style="color: #fbc2eb">返回推荐</el-button>
          </div>

          <div v-if="loading" class="cards-grid">
            <el-skeleton v-for="i in 4" :key="i" style="width: 100%" animated>
              <template #template>
                <el-skeleton-item variant="image" style="width: 100%; height: 240px; border-radius: 12px" />
                <div style="padding: 14px 0">
                  <el-skeleton-item variant="h3" style="width: 50%" />
                </div>
              </template>
            </el-skeleton>
          </div>

          <div v-else class="cards-grid">
            <DestinationCard 
              v-for="item in destinations" 
              :key="item.id" 
              :data="item" 
            />
          </div>
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Navbar from '@/components/Navbar.vue'
import DestinationCard from '@/components/DestinationCard.vue'
import { Search, Right } from '@element-plus/icons-vue'
import api from '@/api'
import { ElMessage } from 'element-plus'

// ✅ 根据你的文件目录 image_f1907e.png 导入视频
import heroVideo from '@/assets/videos/hero.mp4'

const searchQuery = ref('')
const loading = ref(false)
const hasSearched = ref(false)
const destinations = ref([]) 

/**
 * 📅 核心逻辑：获取当前月份推荐
 */
const fetchMonthlyRecommendations = async () => {
  try {
    const res = await api.get('/api/destinations/recommend')
    if (res.data.code === 200) {
      destinations.value = res.data.data
    }
  } catch (error) {
    console.error("加载推荐失败", error)
    ElMessage.error("后端服务未响应")
  }
}

/**
 * 🔍 搜索逻辑
 */
const handleSearch = async () => {
  if (!searchQuery.value.trim()) return ElMessage.warning("请输入探索内容")
  
  loading.value = true
  hasSearched.value = true
  
  try {
    const res = await api.get('/api/destinations/search', {
      params: { query: searchQuery.value }
    })
    if (res.data.code === 200) {
      destinations.value = res.data.data
    }
  } catch (error) {
    ElMessage.error('搜索接口异常')
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  hasSearched.value = false
  searchQuery.value = ''
  fetchMonthlyRecommendations() 
}

// 页面挂载时初始化数据
onMounted(() => {
  fetchMonthlyRecommendations()
})
</script>

<style scoped>
/* 🎨 样式修复：确保视频背景在最底层，且不被覆盖 */
.home-container { 
  position: relative; 
  min-height: 100vh; 
  background: transparent; /* 关键：不要在这里写死黑色 */
}

.hero-background { 
  position: fixed; 
  top: 0; 
  left: 0; 
  width: 100%; 
  height: 100%; 
  z-index: -1; /* 必须在内容下方 */
  overflow: hidden;
}

.bg-video { 
  width: 100%; 
  height: 100%; 
  object-fit: cover; 
}

.overlay { 
  position: absolute; 
  top: 0; 
  left: 0; 
  width: 100%; 
  height: 100%; 
  background: rgba(0, 0, 0, 0.4); /* 适当调低暗度，让背景更通透 */
}

.content-wrapper { 
  padding-top: 150px; 
  display: flex; 
  flex-direction: column; 
  align-items: center; 
  z-index: 2; 
  position: relative; 
  padding-bottom: 100px;
}

.hero-section { 
  text-align: center; 
  width: 100%; 
  max-width: 800px; 
  transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1); 
}

.hero-section.shrink { 
  margin-top: -100px; 
  transform: scale(0.85); 
}

.main-title { 
  font-size: 4.5rem; 
  font-weight: 800; 
  text-shadow: 0 4px 15px rgba(0,0,0,0.6); 
  margin-bottom: 10px;
}

.gradient-text { 
  background: linear-gradient(120deg, #a18cd1 0%, #fbc2eb 100%); 
  -webkit-background-clip: text; 
  -webkit-text-fill-color: transparent; 
}

.subtitle { 
  font-size: 1.5rem; 
  margin-bottom: 40px; 
  color: rgba(255,255,255,0.9); 
  text-shadow: 0 2px 4px rgba(0,0,0,0.5);
}

/* 搜索框毛玻璃 */
:deep(.ai-search-input .el-input__wrapper) { 
  background-color: rgba(255, 255, 255, 0.12) !important; 
  backdrop-filter: blur(12px); 
  border-radius: 50px; 
  padding: 10px 25px; 
  border: 1px solid rgba(255, 255, 255, 0.2); 
  box-shadow: 0 8px 32px rgba(0,0,0,0.3) !important; 
}

:deep(.ai-search-input .el-input__inner) { 
  color: #fff; 
  font-size: 1.15rem; 
  height: 50px; 
}

.search-btn { 
  border-radius: 0 50px 50px 0; 
  background: linear-gradient(to right, #a18cd1, #fbc2eb); 
  border: none; 
  color: white; 
  height: 100%; 
  padding: 0 30px; 
  font-weight: bold; 
  transition: transform 0.2s;
}

.search-btn:hover {
  transform: scale(1.05);
}

.results-section { 
  width: 100%; 
  max-width: 1200px; 
  margin-top: 80px; 
  padding: 0 20px; 
}

.section-header { 
  display: flex; 
  justify-content: space-between; 
  align-items: center; 
  margin-bottom: 30px; 
  border-left: 6px solid #fbc2eb; 
  padding-left: 20px; 
}

.section-header h3 { 
  font-size: 1.7rem; 
  margin: 0; 
  color: #fff;
}

.cards-grid { 
  display: grid; 
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); 
  gap: 30px; 
}

/* 进场动画 */
.fade-up-enter-active { 
  transition: all 0.8s ease-out; 
}
.fade-up-enter-from { 
  opacity: 0; 
  transform: translateY(40px); 
}
</style>