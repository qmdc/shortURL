<template>
  <div>
    <el-input
        class="t1"
        type="textarea"
        :autosize="{ minRows: 5, maxRows: 10}"
        placeholder="请输入短链接"
        v-model="shortURL">
    </el-input>

    <el-row class="t2">
      <el-button @click="generate">还原</el-button>
      <el-button @click="recharge">重置</el-button>
    </el-row>

    <el-descriptions title="原长链信息" class="t3" :column=1 v-show=longURL>
      <el-descriptions-item label="长链">{{ longURL }}</el-descriptions-item>
      <el-descriptions-item label="创建日期">{{ createTime }}</el-descriptions-item>
      <el-descriptions-item label="访问次数">{{ num }}</el-descriptions-item>
    </el-descriptions>

    <el-button :plain="true" v-if="false">警告</el-button>
  </div>
</template>

<script>
import {recharge} from '@/api/index';
export default {
  name: "Recovery",
  data() {
    return {
      shortURL: '',
      longURL: '',
      createTime: '',
      num: 0,
      pre: 'https://youyouyou.qiandao.space/'
    }
  },
  methods: {
    recharge(){
      this.shortURL = ''
      this.longURL = ''
      this.createTime = ''
      this.num = 0
    },
    async generate() {
      if(this.shortURL !== ''){
        let result = await recharge(this.shortURL);
        if(result.data.code === 0){
          this.longURL = result.data.data.longURL
          this.createTime = result.data.data.createTime
          this.num = result.data.data.num
          this.$message({
            message: '短链还原成功！',
            type: 'success'
          });
        }else if(result.data.code === "460"){
          this.$message({
            message: '该短链不存在于此系统！',
            type: 'warning'
          });
        }else{
          this.$message.error('请求出错，请重试');
        }
      }else{
        this.$message({
          message: '输入不能为空！',
          type: 'warning'
        });
      }
    },
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
  margin-top: 25px;
}
a {
  text-decoration: none;
}
</style>