<template>
  <div>
    <el-row :gutter="16">
      <el-col :span="6">
        <el-card>
          <div class="stat">
            <div class="stat-icon" style="background:#409eff"><el-icon><Box /></el-icon></div>
            <div>
              <div class="stat-num">{{ data.totalStock || 0 }}</div>
              <div class="stat-label">库存总量(件)</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat">
            <div class="stat-icon" style="background:#67c23a"><el-icon><Upload /></el-icon></div>
            <div>
              <div class="stat-num">{{ data.todayIn || 0 }}</div>
              <div class="stat-label">今日入库单数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat">
            <div class="stat-icon" style="background:#e6a23c"><el-icon><Download /></el-icon></div>
            <div>
              <div class="stat-num">{{ data.todayOut || 0 }}</div>
              <div class="stat-label">今日出库单数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat">
            <div class="stat-icon" style="background:#f56c6c"><el-icon><Warning /></el-icon></div>
            <div>
              <div class="stat-num">{{ data.warningCount || 0 }}</div>
              <div class="stat-label">预警数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top:16px">
      <el-col :span="12">
        <el-card>
          <template #header><span>近 7 天入库趋势</span></template>
          <v-chart :option="inboundOption" autoresize style="height:300px" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header><span>近 7 天出库趋势</span></template>
          <v-chart :option="outboundOption" autoresize style="height:300px" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, reactive } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { getDashboard, getInboundTrend, getOutboundTrend } from '@/api/report/dashboard'

use([CanvasRenderer, LineChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent])

const data = ref<any>({})
const inboundOption = ref<any>({})
const outboundOption = ref<any>({})

onMounted(async () => {
  try {
    const d: any = await getDashboard()
    data.value = d?.data || {}
  } catch (e) {
    console.warn('[dashboard] getDashboard failed', e)
    data.value = { totalStock: 0, todayIn: 0, todayOut: 0, warningCount: 0, stockSkuCount: 0 }
  }
  try {
    const inT: any = await getInboundTrend()
    inboundOption.value = {
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: inT?.data?.dates || [] },
      yAxis: { type: 'value' },
      series: [{ type: 'line', data: inT?.data?.qtys || [], smooth: true, areaStyle: {}, color: '#67c23a' }]
    }
  } catch (e) {
    console.warn('[dashboard] getInboundTrend failed', e)
  }
  try {
    const outT: any = await getOutboundTrend()
    outboundOption.value = {
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: outT?.data?.dates || [] },
      yAxis: { type: 'value' },
      series: [{ type: 'line', data: outT?.data?.qtys || [], smooth: true, areaStyle: {}, color: '#e6a23c' }]
    }
  } catch (e) {
    console.warn('[dashboard] getOutboundTrend failed', e)
  }
})
</script>

<style scoped>
.stat { display: flex; align-items: center; gap: 16px; }
.stat-icon { width: 56px; height: 56px; border-radius: 8px; color: #fff; display: flex; align-items: center; justify-content: center; font-size: 28px; }
.stat-num { font-size: 24px; font-weight: bold; color: #303133; }
.stat-label { color: #909399; font-size: 13px; margin-top: 4px; }
</style>
