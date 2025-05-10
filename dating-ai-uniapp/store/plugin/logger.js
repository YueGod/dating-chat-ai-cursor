/**
 * Vuex日志插件
 * 用于在开发环境中记录状态变更，方便调试
 * 替代vuex/dist/logger使用，适配小程序环境
 */

/**
 * 创建Vuex日志插件的工厂函数
 * @param {Object} options - 插件配置选项
 * @param {Function} options.transformer - 状态转换函数，可用于格式化输出
 * @param {Boolean} options.collapsed - 是否折叠日志组
 * @param {Function} options.filter - 过滤哪些mutation需要记录
 * @returns {Function} Vuex插件函数
 */
export default function createLogger(options = {}) {
    // 返回Vuex插件函数
    return store => {
        // 订阅store的mutation事件
        store.subscribe((mutation, state) => {
            // 仅在开发环境下记录日志
            if (process.env.NODE_ENV !== 'production') {
                // 使用console.group分组显示日志
                console.group(`%c MUTATION: ${mutation.type}`, 'color: #03A9F4; font-weight: bold')

                // 打印mutation前的状态
                console.log(
                    '%c prev state',
                    'color: #9E9E9E; font-weight: bold',
                    options.transformer ? options.transformer(state) : state
                )

                // 打印mutation载荷
                console.log(
                    '%c mutation',
                    'color: #4CAF50; font-weight: bold',
                    mutation.payload
                )

                // 打印mutation后的状态
                console.log(
                    '%c next state',
                    'color: #FF5722; font-weight: bold',
                    options.transformer ? options.transformer(state) : state
                )

                // 结束分组
                console.groupEnd()
            }
        })
    }
} 