// 定义日志级别
const LEVEL = {
    DEBUG: 0,
    INFO: 1,
    WARN: 2,
    ERROR: 3,
    NONE: 4
}

// 当前环境下的日志级别
const currentLevel = process.env.NODE_ENV === 'production' ? LEVEL.ERROR : LEVEL.DEBUG

// 通用日志处理
const formatMsg = (msg) => {
    if (typeof msg === 'object') {
        try {
            return JSON.stringify(msg)
        } catch (e) {
            return msg
        }
    }
    return msg
}

// 日志样式
const styles = {
    debug: 'color: #7f8c8d',
    info: 'color: #2ecc71',
    warn: 'color: #f39c12',
    error: 'color: #e74c3c'
}

// 记录日志并且上报到服务器（生产环境错误）
const logAndReport = (type, msg, reportToServer = false) => {
    const logMethod = type === 'error' ? console.error :
        type === 'warn' ? console.warn :
            type === 'info' ? console.info : console.log

    const style = styles[type]

    // 添加时间戳
    const time = new Date().toLocaleTimeString()
    const formattedMsg = formatMsg(msg)

    // 输出到控制台
    if (style) {
        logMethod(`%c[${time}][${type.toUpperCase()}] ${formattedMsg}`, style)
    } else {
        logMethod(`[${time}][${type.toUpperCase()}] ${formattedMsg}`)
    }

    // 在生产环境中报告错误
    if (reportToServer && process.env.NODE_ENV === 'production') {
        // 这里可以添加错误上报的逻辑
        // 例如向后端发送请求
    }
}

// 日志对象
const logger = {
    // 调试日志
    debug: (msg) => {
        if (currentLevel <= LEVEL.DEBUG) {
            logAndReport('debug', msg)
        }
    },

    // 信息日志
    info: (msg) => {
        if (currentLevel <= LEVEL.INFO) {
            logAndReport('info', msg)
        }
    },

    // 警告日志
    warn: (msg) => {
        if (currentLevel <= LEVEL.WARN) {
            logAndReport('warn', msg)
        }
    },

    // 错误日志
    error: (msg, report = true) => {
        if (currentLevel <= LEVEL.ERROR) {
            logAndReport('error', msg, report)
        }
    }
}

export default logger 