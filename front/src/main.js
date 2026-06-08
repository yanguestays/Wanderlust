import { createApp } from 'vue'
import { createPinia } from 'pinia' // 👈 关键：必须引入 Pinia
import App from './App.vue'
import router from './router'

// 引入 Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// 引入全局样式 (如果有的话)
import './assets/main.css'

const app = createApp(App)
const pinia = createPinia() // 👈 关键：创建 Pinia 实例

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(pinia) // 👈 关键：必须在 router 之前挂载！否则 Navbar 会报错消失
app.use(router)
app.use(ElementPlus)

app.mount('#app')