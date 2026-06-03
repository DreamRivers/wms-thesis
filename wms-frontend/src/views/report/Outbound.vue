<template>
  <el-card>
    <h3>📊 出库统计</h3>
    <v-chart :option="option" autoresize style="height:400px" />
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, GridComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { getOutboundTrend } from '@/api/report/dashboard'

use([CanvasRenderer, LineChart, TitleComponent, TooltipComponent, GridComponent])

const option = ref<any>({})

onMounted(async () => {
  const res: any = await getOutboundTrend()
  option.value = {
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: res.data.dates },
    yAxis: { type: 'value' },
    series: [{ type: 'line', smooth: true, data: res.data.qtys, itemStyle: { color: '#e6a23c' }, areaStyle: {} }]
  }
})
</script>
