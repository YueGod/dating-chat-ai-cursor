/**
 * API模块统一出口
 */
import * as auth from './modules/auth'
import * as user from './modules/user'
import * as conversation from './modules/conversation'
import * as assistant from './modules/assistant'
import * as style from './modules/style'
import * as vip from './modules/vip'
import * as membership from './modules/membership'
import * as coupon from './modules/coupon'
import * as order from './modules/order'
import * as payment from './modules/payment'
import * as tag from './modules/tag'
import * as message from './modules/message'
import * as upload from './modules/upload'

// 导出所有API模块
export {
    auth,
    user,
    message,
    upload,
    conversation,
    assistant,
    style,
    vip,
    membership,
    coupon,
    order,
    payment,
    tag
}

// 默认导出所有API模块
export default {
    auth,
    user,
    message,
    upload,
    conversation,
    assistant,
    style,
    vip,
    membership,
    coupon,
    order,
    payment,
    tag
} 