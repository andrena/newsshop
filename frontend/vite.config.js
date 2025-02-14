import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  build: {
    rollupOptions: {
      input: {
        admin: 'admin.html',
        subscribe: 'subscribe.html',
        index: 'index.html'
      }
    }
  }
})
