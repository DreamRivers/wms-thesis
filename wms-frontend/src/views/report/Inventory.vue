<template>
  <el-card>
    <h3>📊 库存报表</h3>
    <v-chart :option="option" autoresize style="height:400px" />
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { PieChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { getInventoryValue } from '@/api/report/dashboard'

use([CanvasRenderer, PieChart, TitleComponent, TooltipComponent, LegendComponent])

const option = ref<any>({})

onMounted(async () => {
  const res: any = await getInventoryValue()
  const entries = Object.entries(res.data)
  option.value = {
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: entries.map(([k, v]) => ({ name: k, value: v })),
      label: { show: true, formatter: '{b}: {c} ({d}%)' }
    }]
  }
})
</script>
