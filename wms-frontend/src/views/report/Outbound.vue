<template>
  <el-card>
    <h3>出库统计</h3>
    <div class="summary-bar">
      <el-tag type="warning" size="large">近 {{ data.dates?.length || 0 }} 天出库总数量: {{ totalQty }} 件</el-tag>
    </div>
    <v-chart :option="option" autoresize style="height:400px" />
  </el-card>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, GridComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { getOutboundTrend } from '@/api/report/dashboard'

use([CanvasRenderer, LineChart, TitleComponent, TooltipComponent, GridComponent])

const option = ref<any>({})
const data = ref<any>({ dates: [], qtys: [] })
const totalQty = computed(() => (data.value.qtys || []).reduce((a: number, b: number) => a + b, 0))

onMounted(async () => {
  const res: any = await getOutboundTrend()
  data.value = res.data || { dates: [], qtys: [] }
  option.value = {
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: data.value.dates },
    yAxis: { type: 'value', name: '数量(件)' },
    series: [{ type: 'line', smooth: true, data: data.value.qtys, itemStyle: { color: '#e6a23c' }, areaStyle: { opacity: 0.3 } }]
  }
})
</script>

<style scoped>
.summary-bar { margin-bottom: 12px; }
</style>
