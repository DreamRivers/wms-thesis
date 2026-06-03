<template>
  <div class="login-wrap">
    <div class="login-box">
      <h2>📦 WMS 电商仓储物资管理系统</h2>
      <p class="sub">毕业设计 · Spring Boot + Vue 3</p>
      <el-form ref="formRef" :model="form" :rules="rules" size="large" @submit.prevent="onLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="账号" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" :prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item prop="captcha">
          <el-input v-model="form.captcha" placeholder="验证码" :prefix-icon="Key" style="flex:1" />
          <div class="captcha" @click="loadCaptcha">{{ captchaText }}</div>
        </el-form-item>
        <el-button type="primary" :loading="loading" style="width:100%" @click="onLogin">登 录</el-button>
      </el-form>
      <p class="tip">默认账号:admin / wh001 / dept001 / emp001 &nbsp;密码:123456</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Key } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getCaptcha } from '@/api/system/login'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const captchaText = ref('')
const captchaKey = ref('')

const form = reactive({
  username: 'admin',
  password: '123456',
  captcha: ''
})

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function loadCaptcha() {
  const res: any = await getCaptcha()
  captchaKey.value = res.data.captchaKey
  captchaText.value = res.data.captchaImg
}

async function onLogin() {
  await formRef.value.validate()
  loading.value = true
  try {
    await userStore.doLogin({ ...form, captchaKey: captchaKey.value })
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadCaptcha()
})
</script>

<style scoped>
.login-wrap { height: 100vh; display: flex; justify-content: center; align-items: center; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.login-box { width: 420px; padding: 40px; background: #fff; border-radius: 12px; box-shadow: 0 8px 32px rgba(0,0,0,0.18); }
h2 { text-align: center; margin: 0 0 4px; }
.sub { text-align: center; color: #999; margin-bottom: 30px; }
.captcha { width: 100px; height: 40px; line-height: 40px; text-align: center; background: #409eff; color: #fff; border-radius: 4px; cursor: pointer; font-weight: bold; letter-spacing: 4px; }
.tip { color: #999; text-align: center; margin-top: 16px; font-size: 12px; }
:deep(.el-form-item__content) { display: flex; gap: 8px; }
</style>
