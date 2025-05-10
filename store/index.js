import Vue from 'vue'
import Vuex from 'vuex'
// Replace the import from vuex/dist/logger with our custom logger
import createLogger from './plugin/logger'

// Import modules
import app from './modules/app'
import user from './modules/user'
import vip from './modules/vip'
import chat from './modules/chat'
import assistant from './modules/assistant'

Vue.use(Vuex)

// Determine if we should use the logger plugin
const debug = process.env.NODE_ENV !== 'production'

export default new Vuex.Store({
    modules: {
        app,
        user,
        vip,
        chat,
        assistant
    },
    strict: debug,
    plugins: debug ? [createLogger()] : []
}) 