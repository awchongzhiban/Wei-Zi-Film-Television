import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// 导入element-plus
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

// 引入path
import path from 'path'
// 引入自定义icon插件
import { createSvgIconsPlugin } from 'vite-plugin-svg-icons'
// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    // 自动导入element-plus
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    Components({
      resolvers: [ElementPlusResolver()],
    }),
    // 配置自定义icon
    createSvgIconsPlugin({
      iconDirs: [path.resolve(process.cwd(), 'src/assets/icons/svg')],
      symbolId: '[name]'
    })
  ],
  resolve: {
    // 使用import导入文件时刻省略后缀
    extensions: ['.js', '.vue', '.json','.css'],
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  }
})
