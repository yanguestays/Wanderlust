<script setup>
import { ref, onMounted } from 'vue';
import { useUserStore } from '@/stores/userStore';
import api from '@/api';
import { useRouter } from 'vue-router';
// 引入图标
import {
  User, MapPin, Heart, Calendar, Star, Settings,
  LogOut, ArrowLeft, Loader2, Edit, Plane, Trash2
} from 'lucide-vue-next'; 
import { ElMessage, ElMessageBox } from 'element-plus';

const router = useRouter();
const userStore = useUserStore();

// 数据状态
const user = ref({});
const favorites = ref([]);
const bookings = ref([]); 
const loading = ref(true);
const activeTab = ref('favorites'); // 'favorites', 'bookings', 'info'

// 初始化加载
onMounted(async () => {
  if (!userStore.token) {
    ElMessage.warning('请先登录');
    router.push('/login');
    return;
  }
  loading.value = true;
  await Promise.all([fetchUserData(), fetchFavorites(), fetchBookings()]);
  loading.value = false;
});

// 1. 获取用户信息
const fetchUserData = async () => {
  try {
    // 🔥 修正路径：去掉 /api，直接请求 /auth
    const res = await api.get(`/auth/user/${userStore.user.id}`);
    if (res.data.code === 200) {
      user.value = res.data.data;
    }
  } catch (e) { console.error('获取用户信息失败:', e); }
};

// 2. 获取收藏列表
const fetchFavorites = async () => {
  try {
    // 🔥 修正路径：直接请求 /favorites
    const res = await api.get(`/favorites/user/${userStore.user.id}`);
    if (res.data.code === 200) {
      favorites.value = res.data.data;
      console.log('收藏数据:', favorites.value); // 调试用
    }
  } catch (e) { console.error('获取收藏失败:', e); }
};

// 3. 获取订单列表
const fetchBookings = async () => {
  try {
    // 🔥 修正路径：直接请求 /bookings
    const res = await api.get(`/bookings/user/${userStore.user.id}`);
    if (res.data.code === 200) {
      bookings.value = res.data.data;
      console.log('订单数据:', bookings.value); // 调试用
    }
  } catch (e) { console.error('获取订单失败:', e); }
};

// 移除收藏逻辑
const removeFavorite = async (favId, destTitle) => {
  try {
    await ElMessageBox.confirm(`确定要取消收藏 "${destTitle}" 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });

    const res = await api.delete(`/favorites/${favId}`);
    
    if (res.data.code === 200) {
      ElMessage.success('已取消收藏');
      // 前端移除
      favorites.value = favorites.value.filter(f => f.id !== favId);
    } else {
      ElMessage.error(res.data.msg || '操作失败');
    }
  } catch (e) {
    if(e !== 'cancel') console.error(e);
  }
};

const handleLogout = () => {
  userStore.logout();
  ElMessage.success('已退出登录');
  router.push('/');
};

// 工具函数：格式化日期
const formatDate = (dateString) => {
  if(!dateString) return '待定';
  // 兼容后端 Java LocalDate 格式
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' });
};

// 工具函数：翻译状态
const formatStatus = (status) => {
  const map = { 'PENDING': '待确认', 'CONFIRMED': '已确认', 'CANCELLED': '已取消' };
  return map[status] || status;
};

// 🔥🔥🔥 新增：图片加载失败处理 (兜底图)
const handleImageError = (e) => {
  // 当图片加载失败时，自动替换为这张风景图，防止页面裂图
  e.target.src = 'https://images.unsplash.com/photo-1476514525535-07fb3b4ae5f1?q=80&w=800&auto=format&fit=crop';
};

// Tab 配置
const tabs = [
  { id: 'favorites', name: '我的收藏', icon: Heart },
  { id: 'bookings', name: '我的行程', icon: Plane },
  { id: 'info', name: '个人信息', icon: User }
];
</script>

<template>
  <div class="min-h-screen bg-[#0f0f11] text-white">
    <nav class="fixed top-0 left-0 w-full z-50 bg-black/50 backdrop-blur-xl border-b border-white/10 py-4 px-6">
      <div class="max-w-7xl mx-auto flex items-center justify-between">
        <button @click="router.push('/')" class="flex items-center gap-2 text-white/60 hover:text-white transition-colors">
          <ArrowLeft :size="20" /> <span>返回首页</span>
        </button>
        <h1 class="text-xl font-serif font-bold">个人中心</h1>
        <button @click="handleLogout" class="flex items-center gap-2 px-4 py-2 bg-white/5 hover:bg-red-500/20 hover:text-red-400 rounded-lg transition-all">
          <LogOut :size="18" /> <span>退出</span>
        </button>
      </div>
    </nav>

    <div class="pt-24 pb-12 px-6 max-w-7xl mx-auto">
      
      <div class="bg-gradient-to-br from-white/5 to-white/0 backdrop-blur-xl border border-white/10 rounded-2xl p-8 mb-8 flex flex-col md:flex-row items-center gap-8">
        <div class="relative">
          <img :src="user.avatar || 'https://api.dicebear.com/7.x/avataaars/svg?seed=' + user.username" 
               class="w-24 h-24 rounded-full object-cover border-4 border-white/10 bg-gray-800" />
          <div class="absolute bottom-0 right-0 bg-green-500 w-4 h-4 rounded-full border-2 border-[#0f0f11]"></div>
        </div>
        <div class="text-center md:text-left">
          <h2 class="text-3xl font-bold mb-2">{{ user.username || 'Loading...' }}</h2>
          <div class="flex flex-wrap justify-center md:justify-start gap-4 text-sm text-gray-400">
            <span class="flex items-center gap-1 bg-white/5 px-3 py-1 rounded-full border border-white/5">
              <User :size="14"/> ID: {{ user.id }}
            </span>
            <span class="flex items-center gap-1 bg-white/5 px-3 py-1 rounded-full border border-white/5">
              <Star :size="14" class="text-yellow-500"/> {{ user.role === 'ADMIN' ? '管理员' : '旅行者' }}
            </span>
          </div>
        </div>
        <div class="flex-1"></div>
        <div class="flex gap-8 text-center">
          <div>
            <div class="text-2xl font-bold text-pink-500">{{ favorites.length }}</div>
            <div class="text-xs text-gray-500 uppercase tracking-wider">收藏</div>
          </div>
          <div>
            <div class="text-2xl font-bold text-orange-500">{{ bookings.length }}</div>
            <div class="text-xs text-gray-500 uppercase tracking-wider">行程</div>
          </div>
        </div>
      </div>

      <div class="flex gap-2 border-b border-white/10 mb-8 overflow-x-auto pb-1">
        <button v-for="tab in tabs" :key="tab.id" @click="activeTab = tab.id"
          class="px-6 py-3 flex items-center gap-2 transition-all rounded-t-lg relative"
          :class="activeTab === tab.id ? 'bg-white/10 text-orange-400' : 'text-white/40 hover:text-white hover:bg-white/5'"
        >
          <component :is="tab.icon" :size="18" />
          <span class="font-medium">{{ tab.name }}</span>
          <div v-if="activeTab === tab.id" class="absolute bottom-0 left-0 w-full h-0.5 bg-orange-400"></div>
        </button>
      </div>

      <div v-if="activeTab === 'favorites'">
        <div v-if="loading" class="flex justify-center py-20"><Loader2 class="animate-spin text-orange-500 w-8 h-8" /></div>
        <div v-else-if="favorites.length === 0" class="flex flex-col items-center py-20 text-gray-500">
          <Heart :size="48" class="mb-4 opacity-20"/>
          <p>暂无收藏，快去探索吧！</p>
          <button @click="router.push('/')" class="mt-4 text-orange-400 hover:underline">去首页逛逛</button>
        </div>
        <div v-else class="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-4 gap-6">
          <div v-for="fav in favorites" :key="fav.id" 
               class="group bg-white/5 border border-white/10 rounded-2xl overflow-hidden hover:border-orange-500/50 transition-all relative">
            
            <button @click.stop="removeFavorite(fav.id, fav.destinationTitle)" 
              class="absolute top-2 right-2 z-10 p-2 bg-black/50 rounded-full text-white/50 hover:text-red-500 hover:bg-black transition-colors opacity-0 group-hover:opacity-100">
              <Trash2 :size="16"/>
            </button>

            <div class="h-48 overflow-hidden relative cursor-pointer" @click="router.push(`/dest/${fav.destinationId}`)">
              <img 
                :src="fav.destinationImage" 
                @error="handleImageError"
                class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
              />
              <div class="absolute inset-0 bg-gradient-to-t from-black/80 via-transparent to-transparent"></div>
              <div class="absolute bottom-3 left-4 right-4">
                <h3 class="font-bold text-lg truncate">{{ fav.destinationTitle }}</h3>
                <p class="text-xs text-white/60 flex items-center gap-1 mt-1">
                  <MapPin :size="12"/> {{ fav.destinationCountry }}
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-else-if="activeTab === 'bookings'">
        <div v-if="loading" class="flex justify-center py-20"><Loader2 class="animate-spin text-orange-500 w-8 h-8" /></div>
        <div v-else-if="bookings.length === 0" class="flex flex-col items-center py-20 text-gray-500">
          <Plane :size="48" class="mb-4 opacity-20"/>
          <p>还没有预订行程哦</p>
        </div>
        <div v-else class="space-y-4">
          <div v-for="order in bookings" :key="order.id" 
               class="bg-white/5 border border-white/10 rounded-2xl p-6 flex flex-col md:flex-row gap-6 items-center hover:bg-white/[0.07] transition-colors relative overflow-hidden">
            
            <div class="absolute left-0 top-0 bottom-0 w-1" 
                 :class="order.status === 'CONFIRMED' ? 'bg-green-500' : (order.status === 'CANCELLED' ? 'bg-red-500' : 'bg-yellow-500')"></div>

            <img 
              :src="order.destinationImage" 
              @error="handleImageError"
              class="w-full md:w-40 h-28 rounded-xl object-cover bg-gray-800 shadow-lg" 
            />
            
            <div class="flex-1 w-full">
              <div class="flex justify-between items-start mb-3">
                <div>
                  <h3 class="text-xl font-bold flex items-center gap-2">
                    {{ order.destinationTitle }}
                    <span class="text-xs font-normal text-gray-500 px-2 py-0.5 border border-white/10 rounded">订单号 #{{ order.id }}</span>
                  </h3>
                </div>
                <span class="px-3 py-1 rounded-full text-xs font-bold border"
                  :class="order.status === 'PENDING' ? 'bg-yellow-500/10 border-yellow-500/20 text-yellow-400' : 
                          (order.status === 'CONFIRMED' ? 'bg-green-500/10 border-green-500/20 text-green-400' : 'bg-red-500/10 border-red-500/20 text-red-400')">
                  {{ formatStatus(order.status) }}
                </span>
              </div>
              
              <div class="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm text-gray-300 bg-black/20 p-4 rounded-lg">
                <div class="flex flex-col">
                  <span class="text-xs text-gray-500 mb-1">出发日期</span>
                  <div class="flex items-center gap-2"><Calendar :size="14" class="text-orange-400"/> {{ formatDate(order.startDate) }}</div>
                </div>
                <div class="flex flex-col">
                  <span class="text-xs text-gray-500 mb-1">行程状态</span>
                  <div class="flex items-center gap-2"><Calendar :size="14" class="text-gray-400"/> {{ formatStatus(order.status) }}</div>
                </div>
                <div class="flex flex-col">
                  <span class="text-xs text-gray-500 mb-1">出行人数</span>
                  <div class="flex items-center gap-2"><User :size="14" class="text-blue-400"/> {{ order.peopleCount }} 人</div>
                </div>
                <div class="flex flex-col">
                  <span class="text-xs text-gray-500 mb-1">总价</span>
                  <div class="flex items-center gap-2 font-bold text-green-400">¥ {{ order.totalPrice }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-else-if="activeTab === 'info'" class="flex justify-center">
        <div class="w-full max-w-2xl bg-white/5 p-8 rounded-2xl border border-white/10">
          <h3 class="text-lg font-bold mb-6 border-b border-white/10 pb-4">基本资料</h3>
          <div class="space-y-6">
            <div class="grid grid-cols-3 gap-4 items-center">
              <label class="text-sm text-gray-400 text-right">头像</label>
              <div class="col-span-2">
                <img :src="user.avatar" class="w-16 h-16 rounded-full bg-gray-700"/>
              </div>
            </div>
            <div class="grid grid-cols-3 gap-4 items-center">
              <label class="text-sm text-gray-400 text-right">用户名</label>
              <div class="col-span-2 text-lg font-medium">{{ user.username }}</div>
            </div>
            <div class="grid grid-cols-3 gap-4 items-center">
              <label class="text-sm text-gray-400 text-right">用户角色</label>
              <div class="col-span-2">
                <span class="px-3 py-1 bg-white/10 rounded text-sm">{{ user.role }}</span>
              </div>
            </div>
            <div class="grid grid-cols-3 gap-4 items-center">
              <label class="text-sm text-gray-400 text-right">系统 UID</label>
              <div class="col-span-2 font-mono text-gray-500">{{ user.id }}</div>
            </div>
          </div>
          
          <div class="mt-8 pt-6 border-t border-white/10 text-center">
            <p class="text-xs text-gray-500">如需修改密码或个人信息，请联系管理员。</p>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>