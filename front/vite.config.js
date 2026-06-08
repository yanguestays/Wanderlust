import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  server: {
    port: 5173, // 前端端口
    proxy: {
      // 所有后端 API 请求转发到 8080，避免 CORS 问题
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/auth': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/destinations': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/favorites': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/bookings': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/reviews': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/admin': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
})
