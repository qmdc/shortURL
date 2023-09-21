<template>
<div>
  <el-input
      class="t1"
      type="textarea"
      :autosize="{ minRows: 5, maxRows: 10}"
      placeholder="请输入长链接"
      v-model="longURL">
  </el-input>

  <el-row class="t2">
    <el-button @click="generate">生成</el-button>
    <el-button @click="recharge">重置</el-button>
  </el-row>

  <el-collapse accordion class="t3" v-show=id>
    <el-collapse-item title="唯一主键数字短链">
      <div><el-link :href=id target="_blank">{{id}}</el-link></div>
    </el-collapse-item>
    <el-collapse-item title="MD5特征提取短链">
      <div><el-link :href=md5 target="_blank">{{md5}}</el-link></div>
    </el-collapse-item>
    <el-collapse-item title="精简随机数短链">
      <div><el-link :href=random_url target="_blank">{{random_url}}</el-link></div>
    </el-collapse-item>
  </el-collapse>

  <el-button :plain="true" v-if="false">警告</el-button>
</div>
</template>

<script>
import {create} from '@/api/index';
export default {
  name: "Index",
  data() {
    return {
      longURL: '',
      id: '',
      md5: '',
      random_url: '',
      msg: '',
      pre: 'https://youyouyou.qiandao.space/'
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
        let result = await create(this.longURL);
        if(result.data.code === 0){
          this.id = this.pre + result.data.data.id
          this.md5 = this.pre + result.data.data.md5
          this.random_url = this.pre + result.data.data.random_url
          this.$message({
            message: '短链生成成功！',
            type: 'success'
          });
        }else {
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
  margin-top: 20px;
}
a {
  text-decoration: none;
}
</style>