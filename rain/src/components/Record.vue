<template>
  <div class="t1">
    <el-table
        :data="tableData"
        border
        style="width: 100%">
      <el-table-column
          prop="from"
          label="发件人"
          width="180">
      </el-table-column>
      <el-table-column
          prop="to"
          label="收件人"
          width="180">
      </el-table-column>
      <el-table-column
          prop="message"
          label="发件内容">
      </el-table-column>
      <!--<el-table-column-->
      <!--    prop="dateTime"-->
      <!--    label="发件时间">-->
      <!--</el-table-column>-->
    </el-table>
  </div>
</template>

<script>
import {record} from '@/api/index';

export default {
  name: "Record",
  data() {
    return {
      tableData: []
    }
  },
  methods: {
    async getRecord() {
      let result = await record();
      if (result.data.code === 0) {
        this.tableData = result.data.data;
      }else{
        this.$message.error('请求出错，请重试');
      }
    },
  },
  mounted() {
    this.getRecord();
  }
}
</script>

<style scoped>
.t1 {
  margin-top: 20px;
}
</style>