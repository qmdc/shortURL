<template>
  <div class="app container">
    <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal" @select="handleSelect">
      <el-menu-item index="1">
        <router-link class="list-group-item" active-class="active" to="/index">短链生成</router-link>
      </el-menu-item>
      <el-menu-item index="2">
        <router-link class="list-group-item" active-class="active" to="/recovery">短链还原</router-link>
      </el-menu-item>
      <el-menu-item index="3">
        <router-link class="list-group-item" active-class="active" to="/streaming">访问统计</router-link>
      </el-menu-item>
      <el-menu-item index="4">
        <router-link class="list-group-item" active-class="active" to="/record">发件记录</router-link>
      </el-menu-item>
    </el-menu>
    <div class="line"></div>

    <div>
      <router-view></router-view>
    </div>
  </div>
</template>

<script>
export default {
  name: 'App',
  data() {
    return {
      activeIndex: '1',
      activeIndex2: '1'
    };
  },
  methods: {
    handleSelect(key, keyPath) {
      console.log(key, keyPath);
    }
  }
}
</script>

<style>
body{
  margin:0;
  padding:0;
  border:0;
}
.container {
  width: 600px;
  margin: 0 auto;
}
.list-group-item {
  text-decoration: none;
}

</style>
