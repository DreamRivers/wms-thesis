import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import 'element-plus/dist/index.css'
import './styles/index.scss'

const app = createApp(App)
// 全局注册所有 element-plus icons
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component as any)
}
app.use(createPinia())
app.use(ElementPlus)
app.use(router)
app.mount('#app')
