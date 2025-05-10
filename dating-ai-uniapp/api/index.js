/**
 * API模块统一出口
 */
import * as auth from './modules/auth'
import * as user from './modules/user'
import * as conversation from './modules/conversation'
import * as assistant from './modules/assistant'
import * as style from './modules/style'
import * as membership from './modules/membership'
import * as order from './modules/order'
import * as tag from './modules/tag'

// 导出所有API模块
export {
    auth,
    user,
    conversation,
    assistant,
    style,
    membership,
    order,
    tag
}

// 默认导出
export default {
    auth,
    user,
    conversation,
    assistant,
    style,
    membership,
    order,
    tag
} 