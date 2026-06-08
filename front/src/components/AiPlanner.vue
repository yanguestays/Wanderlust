<template>
  <el-drawer
    v-model="visible"
    :title="'🤖 AI 定制：' + destinationName"
    direction="rtl"
    size="500px"
    class="ai-planner-drawer"
  >
    <div class="planner-container">
      <transition name="el-fade-in">
        <div v-if="!planResult" class="input-form">
          <div class="tips-box">
            <el-icon><Compass /></el-icon>
            <p>基于 RRF 算法为您锁定了 <strong>{{ destinationName }}</strong>，让 DeepSeek 为您生成专属行程吧。</p>
          </div>

          <el-form label-position="top">
            <el-form-item label="🗓️ 行程天数">
              <el-slider v-model="form.days" :min="1" :max="10" show-input />
            </el-form-item>
            
            <el-form-item label="💰 预算偏好">
              <el-radio-group v-model="form.budget" size="large">
                <el-radio-button label="穷游" />
                <el-radio-button label="适中" />
                <el-radio-button label="豪华" />
              </el-radio-group>
            </el-form-item>

            <el-form-item label="👥 同行人员">
              <el-select v-model="form.companion" placeholder="请选择" style="width: 100%">
                <el-option label="独自一人" value="独自一人" />
                <el-option label="情侣/夫妻" value="情侣" />
                <el-option label="一家三口" value="带孩子" />
                <el-option label="特种兵朋友" value="朋友" />
              </el-select>
            </el-form-item>

            <el-button type="primary" size="large" class="submit-btn" :loading="loading" @click="generate">
              {{ loading ? 'DeepSeek 正在思考中...' : '✨ 生成行程' }}
            </el-button>
          </el-form>
        </div>
      </transition>

      <transition name="el-zoom-in-bottom">
        <div v-if="planResult" class="result-box">
          <div class="markdown-body" v-html="parsedMarkdown"></div>
          <div class="actions">
            <el-button @click="planResult = ''">重新生成</el-button>
            <el-button type="success" @click="copyPlan">复制行程</el-button>
          </div>
        </div>
      </transition>
    </div>
  </el-drawer>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import api from '@/api'
import MarkdownIt from 'markdown-it'
import { ElMessage, ElNotification } from 'element-plus'
import { Compass } from '@element-plus/icons-vue'

const props = defineProps(['destinationName'])
const visible = ref(false)
const loading = ref(false)
const planResult = ref('')
const md = new MarkdownIt()

const form = reactive({
  days: 3,
  budget: '适中',
  companion: '朋友'
})

const parsedMarkdown = computed(() => md.render(planResult.value))

// 暴露给父组件的方法
const open = () => { visible.value = true }

const generate = async () => {
  loading.value = true
  try {
    const res = await api.post('/api/ai/plan', {
      destination: props.destinationName,
      days: form.days.toString(),
      budget: form.budget,
      companion: form.companion
    })
    
    if (res.data.code === 200) {
      planResult.value = res.data.data
      ElNotification.success({ title: '生成完毕', message: 'DeepSeek 已为您规划好行程！' })
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (err) {
    ElMessage.error('AI 服务响应超时')
  } finally {
    loading.value = false
  }
}

const copyPlan = () => {
  navigator.clipboard.writeText(planResult.value)
  ElMessage.success('已复制到剪贴板')
}

defineExpose({ open })
</script>

<style scoped>
.planner-container { padding: 20px; height: 100%; display: flex; flex-direction: column; }
.tips-box { background: #ecf5ff; padding: 15px; border-radius: 8px; margin-bottom: 25px; color: #409eff; display: flex; gap: 10px; align-items: center; }
.submit-btn { width: 100%; margin-top: 20px; font-weight: bold; background: linear-gradient(135deg, #a18cd1, #fbc2eb); border: none; }
.submit-btn:hover { opacity: 0.9; transform: scale(1.02); }

/* Markdown 样式微调 */
.result-box { flex: 1; overflow-y: auto; padding-right: 5px; }
.markdown-body { font-size: 15px; line-height: 1.7; color: #2c3e50; }
:deep(h1), :deep(h2) { border-bottom: 1px solid #eaecef; padding-bottom: 0.3em; margin-top: 24px; }
:deep(ul) { padding-left: 20px; }
:deep(strong) { color: #626aef; }

.actions { margin-top: 20px; border-top: 1px solid #eee; padding-top: 15px; text-align: right; }
</style>