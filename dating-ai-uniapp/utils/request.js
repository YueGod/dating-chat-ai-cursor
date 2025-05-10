import store from '../store'
import { BASE_URL } from '../config'

// 请求状态码
const CODE = {
    SUCCESS: '000000',
    UNAUTHORIZED: '000401',
    FORBIDDEN: '000403',
    ERROR: '000500'
}

// 创建请求对象
const request = (options) => {
    return new Promise((resolve, reject) => {
        // 默认配置
        const defaultOptions = {
            url: '',
            method: 'GET',
            data: {},
            header: {},
            timeout: 30000
        }

        // 合并配置
        const requestOptions = {
            ...defaultOptions,
            ...options,
            header: {
                'Content-Type': 'application/json',
                ...options.header
            }
        }

        // 获取令牌
        const token = uni.getStorageSync('ylToken')
        if (token && !requestOptions.noAuth) {
            requestOptions.header['Authorization'] = `Bearer ${token}`
        }

        // 拼接URL
        if (!requestOptions.url.startsWith('http')) {
            requestOptions.url = BASE_URL + requestOptions.url
        }

        // 显示加载提示
        if (requestOptions.loading) {
            uni.showLoading({
                title: typeof requestOptions.loading === 'string' ? requestOptions.loading : '加载中...',
                mask: true
            })
        }

        // 发送请求
        uni.request({
            ...requestOptions,
            success: (res) => {
                // 隐藏加载提示
                if (requestOptions.loading) {
                    uni.hideLoading()
                }

                // HTTP状态检查
                if (res.statusCode !== 200) {
                    reject({
                        code: res.statusCode,
                        msg: '网络请求失败'
                    })
                    return
                }

                // 业务状态检查
                const { code, msg, data } = res.data

                if (code === CODE.SUCCESS) {
                    // 成功
                    resolve(data)
                } else if (code === CODE.UNAUTHORIZED) {
                    // 未授权，需要重新登录
                    store.dispatch('user/logout')
                    uni.showToast({
                        title: '登录已过期，请重新登录',
                        icon: 'none',
                        duration: 2000
                    })

                    // 跳转到登录页
                    setTimeout(() => {
                        uni.reLaunch({
                            url: '/pages/login/login'
                        })
                    }, 1500)

                    reject({ code, msg })
                } else {
                    // 其他错误
                    if (requestOptions.showError !== false) {
                        uni.showToast({
                            title: msg || '请求失败',
                            icon: 'none',
                            duration: 2000
                        })
                    }
                    reject({ code, msg })
                }
            },
            fail: (err) => {
                // 隐藏加载提示
                if (requestOptions.loading) {
                    uni.hideLoading()
                }

                // 网络错误处理
                if (requestOptions.showError !== false) {
                    uni.showToast({
                        title: '网络连接失败，请检查网络设置',
                        icon: 'none',
                        duration: 2000
                    })
                }

                reject({
                    code: -1,
                    msg: '网络连接失败'
                })
            }
        })
    })
}

// 拦截器设置
export const setupInterceptors = () => {
    // 这里可以添加额外的拦截器逻辑
    console.log('请求拦截器已配置')
}

// 方法别名
export const get = (url, data = {}, options = {}) => {
    return request({
        url,
        method: 'GET',
        data,
        ...options
    })
}

export const post = (url, data = {}, options = {}) => {
    return request({
        url,
        method: 'POST',
        data,
        ...options
    })
}

export const put = (url, data = {}, options = {}) => {
    return request({
        url,
        method: 'PUT',
        data,
        ...options
    })
}

export const del = (url, data = {}, options = {}) => {
    return request({
        url,
        method: 'DELETE',
        data,
        ...options
    })
}

export default request 