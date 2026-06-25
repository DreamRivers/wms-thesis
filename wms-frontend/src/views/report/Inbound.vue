<template>
  <el-card>
    <h3>入库统计</h3>
    <div class="summary-bar">
      <el-tag type="success" size="large">近 {{ data.dates?.length || 0 }} 天入库总数量: {{ totalQty }} 件</el-tag>
    </div>
    <v-chart :option="option" autoresize style="height:400px" />
  </el-card>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { BarChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, GridComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { getInboundTrend } from '@/api/report/dashboard'

use([CanvasRenderer, BarChart, TitleComponent, TooltipComponent, GridComponent])

const option = ref<any>({})
const data = ref<any>({ dates: [], qtys: [] })
const totalQty = computed(() => (data.value.qtys || []).reduce((a: number, b: number) => a + b, 0))

onMounted(async () => {
  const res: any = await getInboundTrend()
  data.value = res.data || { dates: [], qtys: [] }
  option.value = {
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: data.value.dates },
    yAxis: { type: 'value', name: '数量(件)' },
    series: [{ type: 'bar', data: data.value.qtys, itemStyle: { color: '#67c23a' }, barWidth: '40%' }]
  }
})
</script>

<style scoped>
.summary-bar { margin-bottom: 12px; }
</style>
