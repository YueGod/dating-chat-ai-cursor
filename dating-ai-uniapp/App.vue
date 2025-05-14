<script>
export default {
  onLaunch: function() {
    console.log('App Launch')
    // 检查登录状态
    this.checkLoginStatus()
    // 初始化应用
    this.initApp()
  },
  onShow: function() {
    console.log('App Show')
  },
  onHide: function() {
    console.log('App Hide')
  },
  methods: {
    // 检查登录状态
    checkLoginStatus() {
      const token = uni.getStorageSync('ylToken')
      if (!token) {
        // 如果没有登录，跳转到登录页
        if (this.isLoginPage()) return
        setTimeout(() => {
          uni.reLaunch({
            url: '/pages/login/login'
          })
        }, 1500)
      } else {
        // 已登录，刷新用户信息
        this.$store.dispatch('user/getUserInfo')
      }
    },
    // 判断当前是否在登录页
    isLoginPage() {
      const pages = getCurrentPages()
      if (pages.length === 0) return false
      const currentPage = pages[pages.length - 1]
      return currentPage.route === 'pages/login/login'
    },
    // 初始化应用
    initApp() {
      // 获取系统信息
      const systemInfo = uni.getSystemInfoSync()
      this.$store.commit('app/SET_SYSTEM_INFO', systemInfo)
      
      // 设置状态栏高度
      const statusBarHeight = systemInfo.statusBarHeight
      this.$store.commit('app/SET_STATUS_BAR_HEIGHT', statusBarHeight)
      
      // 检查网络状态
      uni.getNetworkType({
        success: (res) => {
          this.$store.commit('app/SET_NETWORK_TYPE', res.networkType)
        }
      })
      
      // 监听网络状态变化
      uni.onNetworkStatusChange((res) => {
        this.$store.commit('app/SET_NETWORK_TYPE', res.networkType)
        if (!res.isConnected) {
          uni.showToast({
            title: '网络连接已断开',
            icon: 'none',
            duration: 2000
          })
        } else {
          uni.showToast({
            title: '网络已恢复',
            icon: 'none',
            duration: 2000
          })
        }
      })
    }
  }
}
</script>

<style lang="scss">
// 引入ColorUI样式
@import "./colorui/css/main.css";
@import "./colorui/css/font-fix.css";
@import "./colorui/css/icon.css";

// 引入全局样式
@import "./style/index.scss";

// 应用主题变量
page {
  --brand-gradient: linear-gradient(135deg, #ff6b6b, #fc466b);
  --brand-color-primary: #fc466b;
  --brand-color-light: rgba(252, 70, 107, 0.15);
  background-color: #f5f5f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
  line-height: 1.6;
  color: #333;
}

/* 底部安全区 */
.safe-area-bottom {
  padding-bottom: constant(safe-area-inset-bottom);
  padding-bottom: env(safe-area-inset-bottom);
}
</style> 