<template>
  <div class="analytics-container">
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-icon total">
              <i class="el-icon-s-data"></i>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ overview.totalVisits || 0 }}</div>
              <div class="stat-label">总访问量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-icon today">
              <i class="el-icon-time"></i>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ overview.todayVisits || 0 }}</div>
              <div class="stat-label">今日访问</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-icon unique">
              <i class="el-icon-user"></i>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ overview.uniqueVisits || 0 }}</div>
              <div class="stat-label">独立访客</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="table-card">
      <div slot="header" class="table-header">
        <span>访问记录列表</span>
        <div class="header-right">
          <el-select v-model="selectedDays" placeholder="选择时间范围" size="small" @change="loadData">
            <el-option label="最近1天" :value="1"></el-option>
            <el-option label="最近7天" :value="7"></el-option>
            <el-option label="最近30天" :value="30"></el-option>
            <el-option label="最近90天" :value="90"></el-option>
          </el-select>
          <el-button type="primary" size="small" icon="el-icon-refresh" @click="loadData" :loading="loading">刷新</el-button>
        </div>
      </div>

      <el-table
        :data="tableData"
        v-loading="loading"
        border
        style="width: 100%"
        :default-sort="{prop: 'total_visits', order: 'descending'}">
        <el-table-column
          prop="original_url"
          label="原链接"
          min-width="200">
          <template slot-scope="scope">
            <el-tooltip :content="scope.row.original_url" placement="top">
              <span>{{ scope.row.original_url ? (scope.row.original_url.length > 40 ? scope.row.original_url.substring(0, 40) + '...' : scope.row.original_url) : '-' }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column
          prop="random_url"
          label="短链接"
          width="120">
          <template slot-scope="scope">
            <el-tag type="success" size="small">{{ scope.row.random_url || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="ip"
          label="访问IP"
          width="130">
          <template slot-scope="scope">
            <el-tooltip :content="scope.row.ip" placement="top">
              <span>{{ scope.row.ip || '-' }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column
          prop="province"
          label="地区"
          width="100">
          <template slot-scope="scope">
            <span>{{ scope.row.province || scope.row.country || '未知' }}</span>
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
          width="170"
          sortable="custom">
          <template slot-scope="scope">
            <span>{{ scope.row.create_time || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column
          label="操作"
          width="100"
          fixed="right">
          <template slot-scope="scope">
            <el-button
              type="text"
              size="small"
              @click="goToDetail(scope.row.map_id)">
              查看分析
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.current"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pagination.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        class="pagination">
      </el-pagination>
    </el-card>
  </div>
</template>

<script>
import { getAnalyticsOverview, getAnalyticsRecords } from '@/api/index';

export default {
  name: "Record",
  data() {
    return {
      overview: {},
      tableData: [],
      loading: false,
      selectedDays: 30,
      pagination: {
        current: 1,
        size: 20,
        total: 0
      }
    }
  },
  methods: {
    async loadOverview() {
      try {
        let result = await getAnalyticsOverview();
        if (result.data.code === 0) {
          this.overview = result.data.data || {};
        }
      } catch (error) {
        console.error('获取概览失败', error);
      }
    },
    async loadRecords() {
      this.loading = true;
      try {
        let result = await getAnalyticsRecords(this.pagination.current, this.pagination.size, this.selectedDays);
        if (result.data.code === 0) {
          const data = result.data.data || {};
          this.tableData = data.records || [];
          this.pagination.total = data.total || 0;
        } else {
          this.$message.error(result.data.msg || '加载数据失败');
        }
      } catch (error) {
        this.$message.error('加载数据失败');
      } finally {
        this.loading = false;
      }
    },
    async loadData() {
      this.pagination.current = 1;
      await Promise.all([this.loadOverview(), this.loadRecords()]);
    },
    handleSizeChange(val) {
      this.pagination.size = val;
      this.loadRecords();
    },
    handleCurrentChange(val) {
      this.pagination.current = val;
      this.loadRecords();
    },
    goToDetail(mapId) {
      if (mapId) {
        this.$router.push(`/analytics/${mapId}`);
      }
    },
    getDeviceTagType(deviceType) {
      if (deviceType === 'Mobile') return 'warning';
      if (deviceType === 'Tablet') return 'primary';
      if (deviceType === 'Desktop') return 'info';
      return 'info';
    }
  },
  mounted() {
    this.loadData();
  }
}
</script>

<style scoped>
.analytics-container {
  padding: 20px;
}

.stat-cards {
  margin-bottom: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.stat-icon i {
  font-size: 28px;
  color: #fff;
}

.stat-icon.total {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.today {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.unique {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.table-card {
  margin-top: 20px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
