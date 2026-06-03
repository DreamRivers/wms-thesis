<template>
  <el-card>
    <h3>📊 入库统计</h3>
    <v-chart :option="option" autoresize style="height:400px" />
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { BarChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, GridComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { getInboundTrend } from '@/api/report/dashboard'

use([CanvasRenderer, BarChart, TitleComponent, TooltipComponent, GridComponent])

const option = ref<any>({})

onMounted(async () => {
  const res: any = await getInboundTrend()
  option.value = {
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: res.data.dates },
    yAxis: { type: 'value' },
    series: [{ type: 'bar', data: res.data.qtys, itemStyle: { color: '#67c23a' } }]
  }
})
</script>
