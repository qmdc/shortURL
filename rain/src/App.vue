<template>
  <div class="app container">
    <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal" @select="handleSelect">
      <template v-if="isLoggedIn">
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
          <router-link class="list-group-item" active-class="active" to="/record">访问分析</router-link>
        </el-menu-item>
        <el-submenu index="5" style="float:right;">
          <template slot="title">
            <i class="el-icon-user"></i>
            <span>{{ username }}</span>
          </template>
          <el-menu-item index="5-1" @click="logout">退出登录</el-menu-item>
        </el-submenu>
      </template>
      <template v-else>
        <el-menu-item index="1" style="float:right;">
          <router-link class="list-group-item" active-class="active" to="/login">登录</router-link>
        </el-menu-item>
        <el-menu-item index="2" style="float:right;">
          <router-link class="list-group-item" active-class="active" to="/register">注册</router-link>
        </el-menu-item>
      </template>
    </el-menu>
    <div class="line"></div>

    <div>
      <router-view></router-view>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex';

export default {
  name: 'App',
  data() {
    return {
      activeIndex: '1',
      activeIndex2: '1'
    };
  },
  computed: {
    ...mapGetters('user', ['isLoggedIn', 'username'])
  },
  methods: {
    handleSelect(key, keyPath) {
      console.log(key, keyPath);
    },
    logout() {
      this.$store.dispatch('user/logout');
      this.$router.push('/login');
      this.$message.success('已退出登录');
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
  width: 90%;
  max-width: 1400px;
  margin: 0 auto;
}
.list-group-item {
  text-decoration: none;
}

</style>
