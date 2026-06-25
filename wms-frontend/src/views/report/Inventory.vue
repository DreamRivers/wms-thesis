<template>
  <el-card>
    <h3>库存价值</h3>
    <div class="summary-bar">
      <el-tag type="info" size="large">总价值: ¥{{ totalValue.toLocaleString() }} · 覆盖 {{ itemCount }} 个仓库</el-tag>
    </div>
    <v-chart :option="option" autoresize style="height:400px" />
  </el-card>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { PieChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { getInventoryValue } from '@/api/report/dashboard'

use([CanvasRenderer, PieChart, TitleComponent, TooltipComponent, LegendComponent])

const option = ref<any>({})
const raw = ref<any>({})
const totalValue = computed(() => Object.values(raw.value).reduce((a: number, b: any) => a + (b as number), 0))
const itemCount = computed(() => Object.keys(raw.value).length)

onMounted(async () => {
  const res: any = await getInventoryValue()
  raw.value = res.data || {}
  const entries = Object.entries(raw.value)
  option.value = {
    tooltip: { trigger: 'item', formatter: '{b}: ¥{c} ({d}%)' },
    legend: { bottom: 0 },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: entries.map(([k, v]) => ({ name: k, value: v })),
      label: { show: true, formatter: '{b}\n¥{c} ({d}%)' }
    }]
  }
})
</script>

<style scoped>
.summary-bar { margin-bottom: 12px; }
</style>
