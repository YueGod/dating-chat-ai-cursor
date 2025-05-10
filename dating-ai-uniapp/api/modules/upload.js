import request from '@/utils/request'

export const uploadFile = (filePath, params = {}) => {
    return new Promise((resolve, reject) => {
        uni.uploadFile({
            url: request.baseURL + '/upload',
            filePath,
            name: 'file',
            formData: params,
            header: request.getHeaders(),
            success: (res) => {
                if (res.statusCode === 200) {
                    let data = JSON.parse(res.data)
                    resolve(data)
                } else {
                    reject(new Error('Upload failed'))
                }
            },
            fail: (err) => {
                reject(err)
            }
        })
    })
}

export const uploadImage = (filePath, params = {}) => {
    return uploadFile(filePath, { ...params, type: 'image' })
}

export const uploadAvatar = (filePath) => {
    return uploadFile(filePath, { type: 'avatar' })
}

export default {
    uploadFile,
    uploadImage,
    uploadAvatar
} 