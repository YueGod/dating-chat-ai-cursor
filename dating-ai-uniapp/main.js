import Vue from 'vue'
import App from './App'
import store from './store'

Vue.config.productionTip = false

// 导入请求拦截器和API
import { setupInterceptors } from './utils/request'
import * as api from './api/index'

// 挂载API到Vue全局
Vue.prototype.$api = api

// 配置请求拦截器
setupInterceptors()

// 全局工具函数
import * as utils from './utils/common'
Vue.prototype.$utils = utils

// 日志工具
import logger from './utils/logger'
Vue.prototype.$log = logger

// 提示工具
import { toast, loading, hideLoading } from './utils/toast'
Vue.prototype.$toast = toast
Vue.prototype.$loading = loading
Vue.prototype.$hideLoading = hideLoading

App.mpType = 'app'

const app = new Vue({
    store,
    ...App
})
app.$mount() 