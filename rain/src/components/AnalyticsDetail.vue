<template>
  <div class="analytics-detail">
    <el-page-header @back="goBack" content="返回">
      <template slot="content">
        <div class="page-title">
          <span>访问分析详情</span>
          <el-tag type="info" class="map-id-tag">{{ mapId }}</el-tag>
        </div>
      </template>
    </el-page-header>

    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-icon total">
              <i class="el-icon-s-data"></i>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.totalVisits || 0 }}</div>
              <div class="stat-label">总访问量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-icon unique">
              <i class="el-icon-user"></i>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.uniqueVisits || 0 }}</div>
              <div class="stat-label">独立访客</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-icon today">
              <i class="el-icon-time"></i>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.todayVisits || 0 }}</div>
              <div class="stat-label">今日访问</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-icon today-unique">
              <i class="el-icon-star-on"></i>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.todayUniqueVisits || 0 }}</div>
              <div class="stat-label">今日独立访客</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-tabs v-model="activeTab" class="main-tabs">
      <el-tab-pane label="访问趋势" name="trend">
        <el-card class="chart-card">
          <div slot="header" class="card-header">
            <span>访问趋势图</span>
            <div class="header-right">
              <el-radio-group v-model="trendType" size="small" @change="loadTrendData">
                <el-radio-button label="hourly">24小时</el-radio-button>
                <el-radio-button label="daily">近30天</el-radio-button>
                <el-radio-button label="weekly">近8周</el-radio-button>
              </el-radio-group>
            </div>
          </div>
          <v-chart ref="trendChart" :options="trendChartOptions" autoresize class="chart-container" />
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="地域分布" name="region">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card class="chart-card">
              <div slot="header" class="card-header">
                <span>省份分布 (Top 10)</span>
              </div>
              <v-chart ref="regionChart" :options="regionChartOptions" autoresize class="chart-container" />
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card class="chart-card">
              <div slot="header" class="card-header">
                <span>设备类型分布</span>
              </div>
              <v-chart ref="deviceChart" :options="deviceChartOptions" autoresize class="chart-container" />
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>

      <el-tab-pane label="终端分析" name="terminal">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card class="chart-card">
              <div slot="header" class="card-header">
                <span>浏览器分布</span>
              </div>
              <v-chart ref="browserChart" :options="browserChartOptions" autoresize class="chart-container" />
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card class="chart-card">
              <div slot="header" class="card-header">
                <span>操作系统分布</span>
              </div>
              <v-chart ref="osChart" :options="osChartOptions" autoresize class="chart-container" />
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>

      <el-tab-pane label="访问日志" name="logs">
        <el-card class="table-card">
          <div slot="header" class="card-header">
            <span>详细访问日志</span>
          </div>
          <el-table
            :data="visitLogs"
            v-loading="logsLoading"
            border
            style="width: 100%">
            <el-table-column
              prop="ip"
              label="访问IP"
              width="140">
            </el-table-column>
            <el-table-column
              prop="country"
              label="国家"
              width="80">
              <template slot-scope="scope">
                <span>{{ scope.row.country || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column
              prop="province"
              label="省份"
              width="100">
              <template slot-scope="scope">
                <span>{{ scope.row.province || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column
              prop="city"
              label="城市"
              width="100">
              <template slot-scope="scope">
                <span>{{ scope.row.city || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column
              prop="device_type"
              label="设备"
              width="80">
              <template slot-scope="scope">
                <el-tag :type="getDeviceTagType(scope.row.device_type)" size="mini">
                  {{ scope.row.device_type || '未知' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              prop="browser"
              label="浏览器"
              width="100">
              <template slot-scope="scope">
                <span>{{ scope.row.browser || '未知' }}</span>
              </template>
            </el-table-column>
            <el-table-column
              prop="os"
              label="操作系统"
              width="100">
              <template slot-scope="scope">
                <span>{{ scope.row.os || '未知' }}</span>
              </template>
            </el-table-column>
            <el-table-column
              prop="is_unique"
              label="唯一访问"
              width="80">
              <template slot-scope="scope">
                <el-tag :type="scope.row.is_unique === 1 ? 'success' : 'info'" size="mini">
                  {{ scope.row.is_unique === 1 ? '是' : '否' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              prop="create_time"
              label="访问时间"
              width="170">
            </el-table-column>
          </el-table>
          <el-pagination
            @size-change="handleLogsSizeChange"
            @current-change="handleLogsCurrentChange"
            :current-page="logsPagination.current"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="logsPagination.size"
            layout="total, sizes, prev, pager, next, jumper"
            :total="logsPagination.total"
            class="pagination">
          </el-pagination>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import VChart from 'vue-echarts'
import { getVisitStats, getVisitRecords, getHourlyTrend, getDailyTrend, getWeeklyTrend, getRegionDistribution, getDeviceDistribution, getBrowserDistribution } from '@/api/index'

export default {
  name: "AnalyticsDetail",
  components: {
    'v-chart': VChart
  },
  data() {
    return {
      mapId: '',
      stats: {},
      activeTab: 'trend',
      trendType: 'daily',
      loading: false,
      logsLoading: false,
      trendChartOptions: {},
      regionChartOptions: {},
      deviceChartOptions: {},
      browserChartOptions: {},
      osChartOptions: {},
      visitLogs: [],
      logsPagination: {
        current: 1,
        size: 20,
        total: 0
      }
    }
  },
  methods: {
    goBack() {
      this.$router.go(-1)
    },
    async loadStats() {
      try {
        let result = await getVisitStats(this.mapId, 30)
        if (result.data.code === 0) {
          this.stats = result.data.data || {}
        }
      } catch (error) {
        console.error('获取统计数据失败', error)
      }
    },
    async loadTrendData() {
      try {
        let trendData = []
        if (this.trendType === 'hourly') {
          let result = await getHourlyTrend(this.mapId, 1)
          trendData = result.data.data || []
        } else if (this.trendType === 'daily') {
          let result = await getDailyTrend(this.mapId, 30)
          trendData = result.data.data || []
        } else if (this.trendType === 'weekly') {
          let result = await getWeeklyTrend(this.mapId, 8)
          trendData = result.data.data || []
        }
        this.buildTrendChart(trendData)
      } catch (error) {
        console.error('获取趋势数据失败', error)
      }
    },
    async loadRegionData() {
      try {
        let regionResult = await getRegionDistribution(this.mapId, 30)
        let regionData = regionResult.data.data || []
        this.buildRegionChart(regionData)
      } catch (error) {
        console.error('获取地域数据失败', error)
      }
      try {
        let deviceResult = await getDeviceDistribution(this.mapId, 30)
        let deviceData = deviceResult.data.data || []
        this.buildDeviceChart(deviceData)
      } catch (error) {
        console.error('获取设备数据失败', error)
      }
    },
    async loadTerminalData() {
      try {
        let browserResult = await getBrowserDistribution(this.mapId, 30)
        let browserData = browserResult.data.data || []
        this.buildBrowserChart(browserData)
      } catch (error) {
        console.error('获取浏览器数据失败', error)
      }
      try {
        const visitLogs = this.visitLogs.filter(v => v.os && v.os !== '未知')
        const osMap = {}
        visitLogs.forEach(log => {
          if (osMap[log.os]) {
            osMap[log.os]++
          } else {
            osMap[log.os] = 1
          }
        })
        const osData = Object.keys(osMap).map(key => ({ name: key, value: osMap[key] }))
        this.buildOsChart(osData)
      } catch (error) {
        console.error('构建OS数据失败', error)
      }
    },
    async loadVisitLogs() {
      this.logsLoading = true
      try {
        let result = await getVisitRecords(this.mapId, this.logsPagination.current, this.logsPagination.size)
        if (result.data.code === 0) {
          const data = result.data.data || {}
          this.visitLogs = data.records || []
          this.logsPagination.total = data.total || 0
        }
      } catch (error) {
        console.error('获取访问日志失败', error)
      } finally {
        this.logsLoading = false
      }
    },
    buildTrendChart(data) {
      const categories = data.map(item => item.time || item.start_date || `第${item.week}周`)
      const totalData = data.map(item => Number(item.total) || 0)
      const uniqueData = data.map(item => Number(item.unique_count) || 0)

      this.trendChartOptions = {
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['总访问量', '独立访客']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: categories
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '总访问量',
            type: 'line',
            smooth: true,
            data: totalData,
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(102, 126, 234, 0.3)' },
                { offset: 1, color: 'rgba(102, 126, 234, 0.05)' }
              ])
            },
            lineStyle: {
              color: '#667eea',
              width: 2
            }
          },
          {
            name: '独立访客',
            type: 'line',
            smooth: true,
            data: uniqueData,
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(111, 207, 151, 0.3)' },
                { offset: 1, color: 'rgba(111, 207, 151, 0.05)' }
              ])
            },
            lineStyle: {
              color: '#6fcf97',
              width: 2
            }
          }
        ]
      }
    },
    buildRegionChart(data) {
      const top10 = data.slice(0, 10)
      const categories = top10.map(item => item.name)
      const values = top10.map(item => Number(item.value))

      this.regionChartOptions = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'value'
        },
        yAxis: {
          type: 'category',
          data: categories.reverse()
        },
        series: [
          {
            name: '访问量',
            type: 'bar',
            data: values.reverse(),
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                { offset: 0, color: '#667eea' },
                { offset: 1, color: '#764ba2' }
              ])
            }
          }
        ]
      }
    },
    buildDeviceChart(data) {
      const pieData = data.map(item => ({
        name: item.name,
        value: Number(item.value)
      }))

      this.deviceChartOptions = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: '设备类型',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 16,
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: pieData
          }
        ]
      }
    },
    buildBrowserChart(data) {
      const pieData = data.map(item => ({
        name: item.name,
        value: Number(item.value)
      }))

      this.browserChartOptions = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: '浏览器',
            type: 'pie',
            radius: '60%',
            center: ['50%', '50%'],
            data: pieData,
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      }
    },
    buildOsChart(data) {
      const pieData = data.map(item => ({
        name: item.name,
        value: Number(item.value)
      }))

      this.osChartOptions = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: '操作系统',
            type: 'pie',
            radius: '60%',
            center: ['50%', '50%'],
            data: pieData,
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      }
    },
    handleLogsSizeChange(val) {
      this.logsPagination.size = val
      this.loadVisitLogs()
    },
    handleLogsCurrentChange(val) {
      this.logsPagination.current = val
      this.loadVisitLogs()
    },
    getDeviceTagType(deviceType) {
      if (deviceType === 'Mobile') return 'warning'
      if (deviceType === 'Tablet') return 'primary'
      if (deviceType === 'Desktop') return 'info'
      return 'info'
    }
  },
  watch: {
    activeTab(val) {
      if (val === 'region') {
        this.loadRegionData()
      } else if (val === 'terminal') {
        this.loadTerminalData()
      } else if (val === 'logs') {
        this.loadVisitLogs()
      }
    }
  },
  async mounted() {
    this.mapId = this.$route.params.mapId
    await Promise.all([
      this.loadStats(),
      this.loadTrendData()
    ])
  }
}
</script>

<style scoped>
.analytics-detail {
  padding: 20px;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.map-id-tag {
  font-weight: normal;
}

.stat-cards {
  margin: 20px 0;
}

.stat-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.stat-icon i {
  font-size: 24px;
  color: #fff;
}

.stat-icon.total {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.unique {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.today {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.today-unique {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 2px;
}

.main-tabs {
  margin-top: 20px;
}

.chart-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.chart-container {
  height: 350px;
  width: 100%;
}

.table-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
