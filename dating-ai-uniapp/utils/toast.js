/**
 * 显示消息提示
 * @param {String} title 提示内容
 * @param {String} icon 提示图标，可选值：success、loading、none
 * @param {Number} duration 提示持续时间
 * @param {Boolean} mask 是否显示透明蒙层
 */
export const toast = (title, icon = 'none', duration = 1500, mask = false) => {
    if (title) {
        uni.showToast({
            title,
            icon,
            duration,
            mask
        })
    }
}

/**
 * 显示加载提示
 * @param {String} title 提示内容
 * @param {Boolean} mask 是否显示透明蒙层
 */
export const loading = (title = '加载中...', mask = true) => {
    uni.showLoading({
        title,
        mask
    })
}

/**
 * 隐藏加载提示
 */
export const hideLoading = () => {
    uni.hideLoading()
}

/**
 * 显示模态对话框
 * @param {String} content 提示内容
 * @param {String} title 标题
 * @param {Boolean} showCancel 是否显示取消按钮
 * @returns {Promise} 返回Promise对象
 */
export const modal = (content, title = '提示', showCancel = true) => {
    return new Promise((resolve, reject) => {
        uni.showModal({
            title,
            content,
            showCancel,
            success: (res) => {
                if (res.confirm) {
                    resolve(true)
                } else if (res.cancel) {
                    resolve(false)
                }
            },
            fail: () => {
                reject(new Error('Modal failed'))
            }
        })
    })
}

/**
 * 显示操作菜单
 * @param {Array} itemList 操作菜单项列表
 * @returns {Promise} 返回Promise对象，其值为用户点击的按钮序号，从0开始
 */
export const actionSheet = (itemList) => {
    return new Promise((resolve, reject) => {
        uni.showActionSheet({
            itemList,
            success: (res) => {
                resolve(res.tapIndex)
            },
            fail: () => {
                reject(new Error('ActionSheet failed'))
            }
        })
    })
}

export default {
    toast,
    loading,
    hideLoading,
    modal,
    actionSheet
} 