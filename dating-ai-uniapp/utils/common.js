/**
 * 通用工具函数
 */

/**
 * 检查手机号格式是否正确
 * @param {String} mobile 手机号
 * @returns {Boolean} 是否合法
 */
export const isMobile = (mobile) => {
    return /^1[3-9]\d{9}$/.test(mobile)
}

/**
 * 格式化时间
 * @param {Date|String|Number} time 时间对象、时间字符串或时间戳
 * @param {String} format 格式化模板，默认为'YYYY-MM-DD HH:mm:ss'
 * @returns {String} 格式化后的时间字符串
 */
export const formatDate = (time, format = 'YYYY-MM-DD HH:mm:ss') => {
    if (!time) return ''

    let date
    if (typeof time === 'object') {
        date = time
    } else {
        if (typeof time === 'string') {
            if (/^[0-9]+$/.test(time)) {
                time = parseInt(time)
            } else {
                time = time.replace(/-/g, '/')
            }
        }

        if (typeof time === 'number' && time.toString().length === 10) {
            time = time * 1000
        }

        date = new Date(time)
    }

    const formatObj = {
        YYYY: date.getFullYear(),
        MM: date.getMonth() + 1,
        DD: date.getDate(),
        HH: date.getHours(),
        mm: date.getMinutes(),
        ss: date.getSeconds(),
        day: date.getDay()
    }

    const timeStr = format.replace(/(YYYY|MM|DD|HH|mm|ss|day)/g, (match) => {
        let value = formatObj[match]
        if (match === 'day') {
            return ['日', '一', '二', '三', '四', '五', '六'][value]
        }

        if (match === 'MM' || match === 'DD' || match === 'HH' || match === 'mm' || match === 'ss') {
            if (value < 10) {
                value = '0' + value
            }
        }

        return value
    })

    return timeStr
}

/**
 * 格式化相对时间
 * @param {Date|String|Number} time 时间对象、时间字符串或时间戳
 * @returns {String} 相对时间字符串
 */
export const formatRelativeTime = (time) => {
    if (!time) return ''

    const now = Date.now()
    let date

    if (typeof time === 'object') {
        date = time
    } else {
        if (typeof time === 'string') {
            if (/^[0-9]+$/.test(time)) {
                time = parseInt(time)
            } else {
                time = time.replace(/-/g, '/')
            }
        }

        if (typeof time === 'number' && time.toString().length === 10) {
            time = time * 1000
        }

        date = new Date(time)
    }

    const diff = now - date.getTime()

    if (diff < 0) {
        return formatDate(date)
    }

    const minute = 1000 * 60
    const hour = minute * 60
    const day = hour * 24
    const week = day * 7
    const month = day * 30

    if (diff < minute) {
        return '刚刚'
    } else if (diff < hour) {
        return Math.floor(diff / minute) + '分钟前'
    } else if (diff < day) {
        return Math.floor(diff / hour) + '小时前'
    } else if (diff < week) {
        return Math.floor(diff / day) + '天前'
    } else if (diff < month) {
        return Math.floor(diff / week) + '周前'
    } else {
        return formatDate(date, 'YYYY-MM-DD')
    }
}

/**
 * 格式化数字
 * @param {Number} num 数字
 * @param {Number} decimals 小数位数
 * @returns {String} 格式化后的数字字符串
 */
export const formatNumber = (num, decimals = 0) => {
    return num.toFixed(decimals).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

/**
 * 复制文本到剪贴板
 * @param {String} text 要复制的文本
 * @param {Function} callback 复制成功后的回调函数
 */
export const copyToClipboard = (text, callback) => {
    uni.setClipboardData({
        data: text,
        success: function () {
            if (typeof callback === 'function') {
                callback()
            } else {
                uni.showToast({
                    title: '复制成功',
                    icon: 'success',
                    duration: 1500
                })
            }
        }
    })
}

/**
 * 生成随机字符串
 * @param {Number} length 字符串长度
 * @returns {String} 随机字符串
 */
export const randomString = (length = 16) => {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
    let result = ''

    for (let i = 0; i < length; i++) {
        result += chars.charAt(Math.floor(Math.random() * chars.length))
    }

    return result
}

/**
 * 深拷贝对象
 * @param {Object} obj 要拷贝的对象
 * @returns {Object} 拷贝后的对象
 */
export const deepClone = (obj) => {
    if (obj === null || typeof obj !== 'object') {
        return obj
    }

    if (obj instanceof Date) {
        return new Date(obj.getTime())
    }

    if (obj instanceof Array) {
        return obj.map(item => deepClone(item))
    }

    if (obj instanceof Object) {
        const copy = {}
        Object.keys(obj).forEach(key => {
            copy[key] = deepClone(obj[key])
        })
        return copy
    }

    throw new Error('Unable to copy object: Unsupported type')
}

/**
 * 防抖函数
 * @param {Function} fn 要执行的函数
 * @param {Number} delay 延迟时间，默认300ms
 * @returns {Function} 防抖函数
 */
export const debounce = (fn, delay = 300) => {
    let timer = null

    return function () {
        const context = this
        const args = arguments

        clearTimeout(timer)

        timer = setTimeout(function () {
            fn.apply(context, args)
        }, delay)
    }
}

/**
 * 节流函数
 * @param {Function} fn 要执行的函数
 * @param {Number} delay 延迟时间，默认300ms
 * @returns {Function} 节流函数
 */
export const throttle = (fn, delay = 300) => {
    let timer = null
    let lastExecTime = 0

    return function () {
        const context = this
        const args = arguments
        const now = Date.now()

        if (now - lastExecTime >= delay) {
            lastExecTime = now
            fn.apply(context, args)
        } else {
            clearTimeout(timer)

            timer = setTimeout(function () {
                lastExecTime = now
                fn.apply(context, args)
            }, delay)
        }
    }
} 