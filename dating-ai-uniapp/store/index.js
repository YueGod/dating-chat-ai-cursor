import Vue from 'vue'
import Vuex from 'vuex'
import createLogger from 'vuex/dist/logger'

// 模块
import app from './modules/app'
import user from './modules/user'
import chat from './modules/chat'
import assistant from './modules/assistant'

Vue.use(Vuex)

// 开发环境开启日志
const debug = process.env.NODE_ENV !== 'production'

export default new Vuex.Store({
    modules: {
        app,
        user,
        chat,
        assistant
    },
    strict: debug,
    plugins: debug ? [createLogger()] : []
}) 