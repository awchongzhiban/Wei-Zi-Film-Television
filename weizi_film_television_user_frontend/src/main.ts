import { createApp } from 'vue';
import { createPinia } from 'pinia';
import App from './App.vue';
// 引入router规则
import router from './router/index.ts'
// 导入 路由守卫
import './router/permission.ts'
import ElementPlus from 'element-plus';
// 引入Element Plus的中文语言包
import zhCN from 'element-plus/es/locale/lang/zh-cn'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import 'element-plus/dist/index.css';
// 引入pinia持久化
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

const app = createApp(App);

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}
// 挂载Icon全局变量
app.config.globalProperties.$icon = ElementPlusIconsVue
// 使用持久化
const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);

// 应用Element Plus的中文语言包
app.use(ElementPlus, {
    locale: zhCN,
});
app.use(pinia)
app.use(router)

app.mount('#app');
  