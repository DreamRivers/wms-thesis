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
  </el-card>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'
const userStore = useUserStore()
const user = computed(() => userStore.userInfo || {})
</script>
