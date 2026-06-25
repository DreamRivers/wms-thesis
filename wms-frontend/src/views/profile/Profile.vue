<template>
  <el-card>
    <h3>👤 个人中心</h3>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="账号">{{ user.username }}</el-descriptions-item>
      <el-descriptions-item label="姓名">{{ user.realName }}</el-descriptions-item>
      <el-descriptions-item label="角色">
        <el-tag v-for="r in user.roles" :key="r" style="margin-right:4px">{{ r }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="用户ID">{{ user.userId }}</el-descriptions-item>
    </el-descriptions>

    <div style="margin-top:20px">
      <el-button type="primary" @click="pwdVisible = true">🔑 修改密码</el-button>
    </div>

    <el-dialog v-model="pwdVisible" title="🔑 修改密码" width="450px">
      <el-form :model="pwdForm" label-width="100px" ref="pwdFormRef" :rules="pwdRules">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdVisible = false">取消</el-button>
        <el-button type="primary" @click="onChangePwd">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { changeMyPassword } from '@/api/system/user'

const userStore = useUserStore()
const user = computed(() => userStore.userInfo || {})

// 修改密码
const pwdVisible = ref(false)
const pwdFormRef = ref<any>()
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码至少 6 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (_: any, v: string, cb: any) => {
        if (v !== pwdForm.newPassword) cb(new Error('两次输入不一致'))
        else cb()
      },
      trigger: 'blur'
    }
  ]
}

async function onChangePwd() {
  if (!pwdFormRef.value) return
  await pwdFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    try {
      await changeMyPassword({
        oldPassword: pwdForm.oldPassword,
        newPassword: pwdForm.newPassword
      })
      ElMessage.success('密码修改成功,请重新登录')
      pwdVisible.value = false
      // 清空表单
      pwdForm.oldPassword = ''
      pwdForm.newPassword = ''
      pwdForm.confirmPassword = ''
      // 退出登录
      setTimeout(() => userStore.doLogout(), 1000)
    } catch (e: any) {
      ElMessage.error(e?.message || '修改失败')
    }
  })
}
</script>
