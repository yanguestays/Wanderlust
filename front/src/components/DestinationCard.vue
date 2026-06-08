<template>
  <div class="destination-card" @click="goToDetail">
    <div class="image-wrapper">
      <img 
        :src="data.posterUrl || data.imageUrl || data.image || 'https://via.placeholder.com/400x300?text=No+Image'" 
        alt="风景" 
        loading="lazy" 
        @error="handleImageError"
      />
      
      <div class="card-overlay">
        <div class="rating">
          <el-icon><StarFilled /></el-icon>
          <span>{{ data.avgRating || data.rating || 9.0 }}</span>
        </div>
      </div>
    </div>

    <div class="card-info">
      <div class="header">
        <h3>{{ data.title || data.destinationName }}</h3>
        <span class="location">{{ data.location || data.director || '未知地点' }}</span>
      </div>
      
      <div class="tags">
        <span v-for="(tag, index) in parseTags(data.tags)" :key="index" class="tag">
          {{ tag }}
        </span>
      </div>

      <div class="footer">
        <p class="desc">{{ truncate(data.description) }}</p>
        <el-button link type="primary" class="go-btn">
          探索 <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { StarFilled, ArrowRight } from '@element-plus/icons-vue'

const props = defineProps({
  data: {
    type: Object,
    required: true
  }
})

const router = useRouter()

const goToDetail = () => {
  if (props.data && props.data.id) {
    router.push(`/dest/${props.data.id}`)
  }
}

// 图片加载失败兜底
const handleImageError = (e) => {
  e.target.src = "https://images.unsplash.com/photo-1476514525535-07fb3b4ae5f1?q=80&w=400&auto=format&fit=crop"
}

const parseTags = (tags) => {
  if (!tags) return ['热门', '推荐']
  if (Array.isArray(tags)) return tags.slice(0, 3)
  return typeof tags === 'string' ? tags.split(/[,，]/).slice(0, 3) : ['精选']
}

const truncate = (text) => {
  if (!text) return '暂无介绍...'
  return text.length > 40 ? text.substring(0, 40) + '...' : text
}
</script>

<style scoped>
/* 🔥 改为深色玻璃拟态风格，适配你的黑色背景 */
.destination-card {
  background: rgba(30, 30, 35, 0.7); /* 半透明深灰 */
  backdrop-filter: blur(10px);        /* 毛玻璃特效 */
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.4s ease;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.destination-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.4);
  border-color: rgba(255, 255, 255, 0.2);
}

.image-wrapper {
  height: 200px;
  width: 100%;
  position: relative;
  overflow: hidden;
}

.image-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s ease;
}

.destination-card:hover .image-wrapper img {
  transform: scale(1.05);
}

.card-overlay {
  position: absolute;
  top: 12px;
  right: 12px;
}

.rating {
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  color: #f1c40f;
  padding: 4px 8px;
  border-radius: 6px;
  font-weight: bold;
  font-size: 0.85rem;
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-info {
  padding: 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

/* 🔥 文字改为浅色 */
.header h3 {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 700;
  color: #e0e0e0; 
  line-height: 1.4;
}

.location {
  font-size: 0.8rem;
  color: #9ca3af;
  white-space: nowrap;
  margin-left: 8px;
}

.tags {
  display: flex;
  gap: 6px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.tag {
  background: rgba(255, 255, 255, 0.1);
  color: #d1d5db;
  font-size: 0.7rem;
  padding: 2px 8px;
  border-radius: 4px;
}

.desc {
  font-size: 0.85rem;
  color: #9ca3af;
  margin: 0;
  line-height: 1.5;
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.footer {
  margin-top: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.go-btn {
  padding: 0;
  font-size: 0.9rem;
}
</style>