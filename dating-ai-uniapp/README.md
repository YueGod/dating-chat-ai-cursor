# Dating AI 小程序前端

基于 Uni-app 的微信小程序前端，提供AI驱动的恋爱聊天助手服务。

## 项目架构

前端遵循标准的Uni-app结构：

- `pages/` - 应用页面和页面组件
  - `index/` - 首页
  - `login/` - 登录页面
  - `chat/` - 聊天页面和聊天详情
  - `assistant/` - AI助手页面
  - `profile/` - 个人中心和资料设置
  - `vip/` - 会员中心
- `components/` - 可复用的UI组件
- `static/` - 静态资源如图片和图标
- `store/` - 状态管理
- `utils/` - 辅助函数和工具
- `api/` - 用于后端通信的API客户端
- `style/` - 全局样式和主题

## 技术栈

- Uni-app框架
- Vue.js用于组件架构
- 微信小程序SDK
- SCSS用于样式
- Color UI用于UI设计

## 功能模块

- 用户认证与账户管理
- 聊天会话管理
- AI聊天回复生成
- 会员与支付
- 个人资料与设置

## 开发指南

1. 安装HBuilderX开发工具
2. 安装微信开发者工具
3. 导入本项目
4. 在HBuilderX中调试或发布到微信开发者工具

## API集成

- 所有API调用使用集中的API客户端
- 统一处理加载状态和错误
- 实现适当的重试和错误处理机制
- 缓存适当的数据以减少后端调用

## 图标使用指南

项目中的图标采用了两种方式：

1. **ColorUI图标类**：大部分页面内图标使用ColorUI提供的图标类

```html
<!-- 使用ColorUI图标类示例 -->
<text class="cuIcon-emoji text-blue"></text>
<text class="cuIcon-heart text-red cuIcon-xl"></text>
```

常用图标类列表：
- `cuIcon-back` - 返回箭头
- `cuIcon-search` - 搜索图标
- `cuIcon-close` - 关闭/叉号图标
- `cuIcon-add` - 添加/加号图标
- `cuIcon-chat` - 聊天图标
- `cuIcon-robot` - 机器人/AI助手图标
- `cuIcon-profile` - 用户/个人图标
- `cuIcon-crown` - 皇冠/VIP图标
- `cuIcon-heart` - 心形图标
- `cuIcon-star` - 星星图标
- `cuIcon-weixin` - 微信图标

图标样式类：
- `cuIcon-sm` - 小尺寸图标
- `cuIcon-lg` - 大尺寸图标 
- `cuIcon-xl` - 超大尺寸图标
- `text-red`, `text-blue`, `text-green` 等 - 设置图标颜色

2. **Tab Bar图标**：底部导航栏图标使用PNG图片，不支持图标类

## 资源文件说明

- `/static/images/logo.jpeg` - 应用Logo
- `/static/images/default-avatar.png` - 默认头像
- `/static/images/tabbar/` - 底部导航栏图标 