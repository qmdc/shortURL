<template>
<div>
  <el-input
      class="t1"
      type="textarea"
      :autosize="{ minRows: 3, maxRows: 5}"
      placeholder="请输入长链接"
      v-model="longURL">
  </el-input>

  <el-row class="t2">
    <el-button type="primary" @click="generate" :loading="generating">生成短链接</el-button>
    <el-button @click="recharge">重置</el-button>
    <el-button type="info" @click="refreshList" :loading="loading">刷新列表</el-button>
  </el-row>

  <el-collapse accordion class="t3" v-show="id">
    <el-collapse-item title="唯一主键数字短链">
      <div><el-link :href="id" target="_blank">{{id}}</el-link></div>
    </el-collapse-item>
    <el-collapse-item title="MD5特征提取短链">
      <div><el-link :href="md5" target="_blank">{{md5}}</el-link></div>
    </el-collapse-item>
    <el-collapse-item title="精简随机数短链">
      <div><el-link :href="random_url" target="_blank">{{random_url}}</el-link></div>
    </el-collapse-item>
  </el-collapse>

  <el-divider>我的短链接列表</el-divider>

  <el-table :data="shortUrlList" v-loading="loading" border style="width: 100%">
    <el-table-column prop="url" label="原链接" min-width="200">
      <template slot-scope="scope">
        <el-tooltip :content="scope.row.url" placement="top">
          <el-link :href="scope.row.url" target="_blank" type="primary">{{ scope.row.url.length > 40 ? scope.row.url.substring(0, 40) + '...' : scope.row.url }}</el-link>
        </el-tooltip>
      </template>
    </el-table-column>
    <el-table-column prop="random_url" label="短链接" width="150">
      <template slot-scope="scope">
        <el-link :href="pre + scope.row.random_url" target="_blank" type="success">{{ scope.row.random_url }}</el-link>
      </template>
    </el-table-column>
    <el-table-column prop="click_num" label="访问次数" width="100">
      <template slot-scope="scope">
        <el-tag type="info">{{ scope.row.click_num }}</el-tag>
      </template>
    </el-table-column>
    <el-table-column prop="create_time" label="创建时间" width="180">
      <template slot-scope="scope">
        {{ scope.row.create_time }}
      </template>
    </el-table-column>
    <el-table-column label="操作" width="100" fixed="right">
      <template slot-scope="scope">
        <el-button type="text" size="small" @click="copyUrl(scope.row)">复制</el-button>
        <el-button type="text" size="small" style="color: #F56C6C" @click="deleteUrl(scope.row)">删除</el-button>
      </template>
    </el-table-column>
  </el-table>

  <el-empty v-if="!loading && shortUrlList.length === 0" description="暂无短链接，快去生成一个吧！"></el-empty>
</div>
</template>

<script>
import { create, getUserShortUrls, deleteShortUrl } from '@/api/index';

export default {
  name: "Index",
  data() {
    return {
      longURL: '',
      id: '',
      md5: '',
      random_url: '',
      msg: '',
      pre: 'https://youyouyou.qiandao.space/',
      shortUrlList: [],
      generating: false,
      loading: false
    }
  },
  methods: {
    recharge(){
      this.longURL = ''
      this.id = ''
      this.md5 = ''
      this.random_url = ''
      this.msg = ''
    },
    async generate() {
      if(this.longURL !== ''){
        this.generating = true;
        try {
          let result = await create(this.longURL);
          if(result.data.code === 0){
            this.id = this.pre + result.data.data.id
            this.md5 = this.pre + result.data.data.md5
            this.random_url = this.pre + result.data.data.random_url
            this.$message({
              message: '短链生成成功！',
              type: 'success'
            });
            this.refreshList();
          }else {
            this.$message.error(result.data.msg || '请求出错，请重试');
          }
        } catch (error) {
          this.$message.error('请求出错，请重试');
        } finally {
          this.generating = false;
        }
      }else{
        this.$message({
          message: '输入不能为空！',
          type: 'warning'
        });
      }
    },
    async refreshList() {
      this.loading = true;
      try {
        let result = await getUserShortUrls();
        if(result.data.code === 0){
          this.shortUrlList = result.data.data;
        }else {
          this.$message.error(result.data.msg || '获取列表失败');
        }
      } catch (error) {
        this.$message.error('获取列表失败');
      } finally {
        this.loading = false;
      }
    },
    copyUrl(row) {
      const url = this.pre + row.random_url;
      const input = document.createElement('input');
      input.value = url;
      document.body.appendChild(input);
      input.select();
      document.execCommand('copy');
      document.body.removeChild(input);
      this.$message({
        message: '短链接已复制到剪贴板',
        type: 'success'
      });
    },
    async deleteUrl(row) {
      this.$confirm('确定要删除该短链接吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          let result = await deleteShortUrl(row.id);
          if(result.data.code === 0){
            this.$message({
              message: '删除成功',
              type: 'success'
            });
            this.refreshList();
          }else {
            this.$message.error(result.data.msg || '删除失败');
          }
        } catch (error) {
          this.$message.error('删除失败');
        }
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    }
  },
  mounted() {
    this.refreshList();
  }
}
</script>

<style scoped>
.t1 {
  margin-top: 15px;
}
.t2 {
  margin-top: 10px;
  text-align: center;
}
.t3 {
  margin-top: 20px;
}
a {
  text-decoration: none;
}
</style>