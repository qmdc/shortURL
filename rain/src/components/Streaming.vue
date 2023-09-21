<template>
<div>
    <div id="myChart"></div>
</div>
</template>

<script>
import {top10} from "@/api";

export default {
  name: 'Streaming',
  data () {
    return {
      datax: [],
      datay: [],
      timer: null
    }
  },
  mounted(){
    this.drawLine();
  },
  methods: {
    async drawLine() {
      console.log("定时器调用")
      let result = await top10();
      if(result.data.code === 0){
        this.datax = Object.keys(result.data.data).reverse();
        this.datay = Object.values(result.data.data).reverse();
      }else{
        this.$message.error('请求出错，请重试');
      }
      let myChart = this.$echarts.init(document.getElementById('myChart'))
      myChart.setOption({
        title: {text: '链接投放点击频率TOP10统计(每5秒刷新)'},
        grid: {
          left: '10%',
          right: '10%',
          bottom: '14%',
          top: '16%'
        },
        tooltip: {},
        xAxis: {
          data: this.datax,
        },
        yAxis: {},
        series: [{
          name: '访问次数',
          type: 'bar',
          data: this.datay,
          itemStyle: {
            normal: {
              color: '#61c589'
            }
          }
        }]
      });
    },

    startTimer() {
      this.timer = setInterval(() => {
        this.drawLine();
      }, 5000);
    },

    stopTimer() {
      clearInterval(this.timer);
    },
  },
  created() {
    this.startTimer();
  },
  beforeDestroy() {
    this.stopTimer();
  },
}
</script>

<style scoped>
#myChart {
  width: 600px;
  height: 350px;
  margin-top: 25px;
  border: 1px solid rgba(0,0,0,.1);
  border-radius: 8px;
  box-shadow: 0 0 45px rgba(0,0,0,.2);
}
</style>