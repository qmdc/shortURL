const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  lintOnSave: false
})
module.exports = {
  lintOnSave:false,//关闭eslint校验工具
  productionSourceMap:false,//关闭map文件
  devServer:{
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:8025',
        pathRewrite: { '^/api': '' },
      },
    },
  }
}