<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import api from '@/api';
import Navbar from '@/components/Navbar.vue';
import { ArrowLeft, MapPin, Star, Calendar, Plane, Heart, Sparkles, MessageSquare } from 'lucide-vue-next';
import AiPlanner from '@/components/AiPlanner.vue';
import { useUserStore } from '@/stores/userStore';
import { ElMessage } from 'element-plus';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

// 状态
const dest = ref(null);
const reviews = ref([]); // 评论列表
const aiPlannerRef = ref(null);
const isFavorited = ref(false);
const favLoading = ref(false);

// 评论表单
const reviewForm = ref({
  rating: 5,
  content: ''
});
const reviewLoading = ref(false);

// 预约弹窗
const bookingDialogVisible = ref(false);
const bookingLoading = ref(false);
const bookingForm = ref({ startDate: '', people: 1, note: '' });

// 图片兜底
const heroImage = computed(() => {
  if (!dest.value) return '';
  return dest.value.posterUrl || dest.value.imageUrl || dest.value.image || 
         'https://images.unsplash.com/photo-1476514525535-07fb3b4ae5f1?q=80&w=1920&auto=format&fit=crop';
});
const handleImageError = (e) => {
  e.target.src = 'https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?q=80&w=1920&auto=format&fit=crop';
};

// 功能函数
const handleOpenAi = () => aiPlannerRef.value?.open();
const goBack = () => router.back();

// 收藏
const checkFavoriteStatus = async () => {
  if (!userStore.token || !dest.value) return;
  try {
    const res = await api.get('/favorites/check', {
      params: { userId: userStore.user.id, destId: dest.value.id }
    });
    if (res.data.code === 200) isFavorited.value = res.data.data;
  } catch (e) {}
};
const toggleFavorite = async () => {
  if (!userStore.token) return router.push('/login');
  favLoading.value = true;
  try {
    const res = await api.post('/favorites/toggle', {
      userId: userStore.user.id, destId: dest.value.id
    });
    if (res.data.code === 200) {
      isFavorited.value = !isFavorited.value;
      ElMessage.success(res.data.data);
    }
  } catch (e) { ElMessage.error('操作失败'); } 
  finally { favLoading.value = false; }
};

// 预订
const handleOpenBooking = () => {
  if (!userStore.token) return router.push('/login');
  bookingDialogVisible.value = true;
};
const submitBooking = async () => {
  if (!bookingForm.value.startDate) return ElMessage.warning('请选择日期');
  bookingLoading.value = true;
  try {
    const res = await api.post('/bookings/create', {
      userId: userStore.user.id,
      destId: dest.value.id,
      startDate: bookingForm.value.startDate,
      people: bookingForm.value.people,
      note: bookingForm.value.note
    });
    if (res.data.code === 200) {
      ElMessage.success('预订成功！');
      bookingDialogVisible.value = false;
    } else { ElMessage.error(res.data.message); }
  } catch (e) { ElMessage.error('预订失败'); } 
  finally { bookingLoading.value = false; }
};

// 🔥🔥🔥 获取评论列表
const fetchReviews = async () => {
  try {
    const res = await api.get(`/reviews/dest/${route.params.id}`);
    if (res.data.code === 200) reviews.value = res.data.data;
  } catch (e) { console.error(e); }
};

// 🔥🔥🔥 提交评论
const submitReview = async () => {
  if (!userStore.token) return router.push('/login');
  if (!reviewForm.value.content) return ElMessage.warning('写点什么吧');
  
  reviewLoading.value = true;
  try {
    const res = await api.post('/reviews/add', {
      userId: userStore.user.id,
      destId: dest.value.id,
      rating: reviewForm.value.rating,
      content: reviewForm.value.content
    });
    
    if (res.data.code === 200) {
      ElMessage.success('评价已发布！');
      reviewForm.value.content = '';
      reviewForm.value.rating = 5;
      // 重新加载评论和详情(更新分数)
      fetchReviews();
      fetchDetail(); 
    } else { ElMessage.error(res.data.message); }
  } catch (e) { ElMessage.error('发布失败'); } 
  finally { reviewLoading.value = false; }
};

const fetchDetail = async () => {
  try {
    const res = await api.get(`/destinations/${route.params.id}`);
    if (res.data.code === 200) dest.value = res.data.data;
  } catch(e) {}
}

// 初始化
onMounted(async () => {
  await fetchDetail();
  if (dest.value) {
    checkFavoriteStatus();
    fetchReviews(); // 加载评论
  }
});

const formatDate = (date) => {
  if(!date) return '';
  return new Date(date).toLocaleDateString();
}
</script>

<template>
  <div class="min-h-screen bg-[#0f0f11] text-white font-sans">
    <Navbar />
    
    <div v-if="dest" class="relative">
      <div class="fixed inset-0 z-0 h-[70vh]">
        <img :src="heroImage" class="h-full w-full object-cover opacity-60" @error="handleImageError"/>
        <div class="absolute inset-0 bg-gradient-to-t from-[#0f0f11] via-[#0f0f11]/50 to-transparent"></div>
      </div>

      <div class="relative z-10 max-w-7xl mx-auto px-6 pt-32 pb-20">
        <button @click="goBack" class="mb-8 flex items-center gap-2 text-white/60 hover:text-white transition">
          <ArrowLeft :size="20"/> Back
        </button>
        
        <div class="grid grid-cols-1 lg:grid-cols-12 gap-12">
          <div class="lg:col-span-8">
            <div class="flex items-center gap-3 text-orange-400 font-bold uppercase tracking-widest text-sm mb-4">
              <MapPin :size="16" /> <span>{{ dest.location || dest.director || 'WanderLust' }}</span>
            </div>
            <h1 class="text-5xl md:text-7xl font-serif font-bold mb-8 leading-tight">
              {{ dest.title || dest.destinationName }}
            </h1>
            
            <div class="flex gap-4 mb-10">
               <span class="px-4 py-2 bg-white/10 rounded-full text-sm flex gap-2 items-center backdrop-blur-md border border-white/5">
                 <Calendar :size="14"/> {{ dest.releaseYear || '全年开放' }}
               </span>
               <span class="px-4 py-2 bg-white/10 rounded-full text-sm flex gap-2 items-center text-yellow-400 backdrop-blur-md border border-white/5">
                 <Star :size="14"/> {{ dest.rating || dest.avgRating || '暂无评分' }}
               </span>
            </div>
            
            <p class="text-xl text-gray-200 font-serif leading-relaxed opacity-90 mb-12">
              {{ dest.description || '暂无详细介绍...' }}
            </p>

            <div class="border-t border-white/10 pt-10">
              <h3 class="text-2xl font-bold mb-6 flex items-center gap-2">
                <MessageSquare class="text-orange-500"/> 游客评价 ({{ reviews.length }})
              </h3>

              <div class="bg-white/5 p-6 rounded-2xl border border-white/10 mb-8">
                <div v-if="userStore.token">
                  <div class="mb-4">
                    <span class="text-sm text-gray-400 mr-2">打个分:</span>
                    <el-rate v-model="reviewForm.rating" />
                  </div>
                  <el-input
                    v-model="reviewForm.content"
                    type="textarea"
                    rows="3"
                    placeholder="分享你的旅行体验..."
                    class="mb-4 bg-transparent"
                  />
                  <div class="text-right">
                    <el-button type="primary" color="#f97316" :loading="reviewLoading" @click="submitReview">
                      发布评价
                    </el-button>
                  </div>
                </div>
                <div v-else class="text-center py-4">
                  <p class="text-gray-500 mb-2">登录后即可发表评论</p>
                  <el-button @click="router.push('/login')">去登录</el-button>
                </div>
              </div>

              <div class="space-y-6">
                <div v-for="review in reviews" :key="review.id" class="flex gap-4 border-b border-white/5 pb-6">
                  <img :src="review.avatar || 'https://api.dicebear.com/7.x/avataaars/svg?seed=' + review.username" 
                       class="w-10 h-10 rounded-full bg-gray-700 object-cover"/>
                  <div class="flex-1">
                    <div class="flex justify-between items-start mb-1">
                      <h4 class="font-bold text-sm">{{ review.username || '匿名用户' }}</h4>
                      <span class="text-xs text-gray-500">{{ formatDate(review.createTime) }}</span>
                    </div>
                    <el-rate :model-value="review.rating" disabled size="small" class="mb-2"/>
                    <p class="text-gray-300 text-sm leading-relaxed">{{ review.content }}</p>
                  </div>
                </div>
                <div v-if="reviews.length === 0" class="text-center text-gray-500 py-4">
                  暂无评价，快来抢沙发！
                </div>
              </div>
            </div>
          </div>

          <div class="lg:col-span-4">
            <div class="sticky top-32 bg-[#1a1a1a]/80 backdrop-blur-xl p-8 rounded-3xl border border-white/10 shadow-2xl">
              <h3 class="text-2xl font-serif mb-6">Start Journey</h3>
              <button @click="handleOpenAi" class="w-full bg-gradient-to-r from-purple-600 to-pink-600 text-white py-4 rounded-xl font-bold flex justify-center gap-2 mb-4 hover:scale-105 transition-all">
                <Sparkles class="animate-pulse" /> AI 定制行程
              </button>
              <button @click="handleOpenBooking" class="w-full bg-orange-500 py-4 rounded-xl font-bold flex justify-center gap-2 mb-4 hover:scale-105 transition-transform text-white shadow-lg">
                <Plane/> 预订行程
              </button>
              <button @click="toggleFavorite" :disabled="favLoading" class="w-full py-4 rounded-xl font-bold flex justify-center gap-2 transition-all text-white border border-white/10" :class="isFavorited ? 'bg-red-600 border-transparent' : 'bg-white/5 hover:bg-white/10'">
                <Heart :fill="isFavorited ? 'currentColor' : 'none'" :class="{ 'text-white': isFavorited }" /> 
                {{ isFavorited ? '已收藏' : '加入收藏' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <AiPlanner v-if="dest" ref="aiPlannerRef" :destination-name="dest.title || dest.destinationName" />

    <el-dialog v-model="bookingDialogVisible" title="✈️ 开启您的旅程" width="400px" align-center>
      <div class="p-4">
        <p class="mb-4 text-gray-500">目的地：<span class="font-bold text-black">{{ dest?.title }}</span></p>
        <el-form label-position="top">
          <el-form-item label="出发日期"><el-date-picker v-model="bookingForm.startDate" type="date" style="width: 100%" value-format="YYYY-MM-DD" :disabled-date="t=>t.getTime()<Date.now()"/></el-form-item>
          <el-form-item label="人数"><el-input-number v-model="bookingForm.people" :min="1" :max="20" /></el-form-item>
          <el-form-item label="备注"><el-input v-model="bookingForm.note" type="textarea" /></el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="bookingDialogVisible=false">取消</el-button>
        <el-button type="primary" :loading="bookingLoading" @click="submitBooking" color="#f97316">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>