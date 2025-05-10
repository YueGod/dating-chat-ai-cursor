/**
 * Vuex状态管理配置
 * 集中管理应用状态，实现组件间数据共享
 */
import Vue from 'vue'
import Vuex from 'vuex'
import createLogger from './plugin/logger'

// 导入各个模块
import app from './modules/app'     // 应用全局设置
import user from './modules/user'    // 用户信息
import vip from './modules/vip'      // 会员管理
import chat from './modules/chat'    // 会话管理
import assistant from './modules/assistant' // AI助手

// 安装Vuex插件
Vue.use(Vuex)

// 开发环境开启日志插件，生产环境关闭
const debug = process.env.NODE_ENV !== 'production'

// 创建并导出Vuex实例
export default new Vuex.Store({
    // 注册模块，每个模块有独立的state、mutations、actions和getters
    modules: {
        app,        // 应用设置模块
        user,       // 用户模块
        vip,        // 会员模块
        chat,       // 聊天模块
        assistant   // 助手模块
    },
    strict: debug,  // 开发环境启用严格模式，防止直接修改状态
    plugins: debug ? [createLogger()] : [] // 开发环境启用日志插件
}) 