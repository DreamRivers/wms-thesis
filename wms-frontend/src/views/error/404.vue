<template>
  <el-result icon="error" title="404" sub-title="页面走丢了">
    <template #extra>
      <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
    </template>
  </el-result>
</template>
