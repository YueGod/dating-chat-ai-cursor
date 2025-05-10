# Dating AI - 恋爱聊天AI应用

<div align="center">
  <a href="#cn">中文</a> | <a href="#en">English</a>
</div>

---

<div id="cn">

## 项目概述

Dating AI是一个约会聊天人工智能应用，通过AI驱动的恋爱话术提供聊天服务，帮助用户提升社交互动能力。项目包含后端Java服务和基于Uni-app的微信小程序前端。

### 核心功能

- AI驱动的恋爱聊天话术
- 自然流畅的对话体验
- 微信小程序客户端
- Java Spring Boot后端

## 项目架构

### 后端架构

后端使用Spring Boot 3.0构建，基于JDK 21，采用分层架构设计：

```
[项目].[模块].[层]
```

层次结构：
- `domain/meta` - 领域实体/模型
- `dao` - 数据访问对象
- `dto` - 数据传输对象
- `vo` - 视图对象
- `service` - 业务逻辑实现
- `controller` - REST API端点
- `config` - 配置类
- `utils` - 工具类

### 前端架构

前端使用Uni-app框架构建微信小程序，遵循标准的Uni-app结构：

- `pages/` - 应用页面和页面组件
- `components/` - 可复用的UI组件
- `static/` - 静态资源
- `store/` - 状态管理
- `utils/` - 辅助函数和工具
- `api/` - 用于后端通信的API客户端

## 技术栈

### 后端技术

- **框架**: Spring Boot 3.2
- **JDK**: Java 21
- **AI模型**: Spring AI 1.0.0-M8, OpenAI集成
- **数据库**: MongoDB, Redis
- **向量数据库**: Milvus
- **API文档**: Swagger 3.0 / Knife4J
- **JSON处理**: FastJSON 2
- **工具库**: Lombok, Apache Commons, Apache HTTP Client
- **测试**: JUnit 5, Mockito

### 前端技术

- **框架**: Uni-app
- **组件化**: Vue.js
- **状态管理**: Vuex
- **UI库**: Color UI
- **样式**: SCSS/LESS
- **小程序SDK**: 微信小程序

## 项目模块

项目由以下主要模块组成：

1. **dating-ai-server** - 后端服务器，处理AI逻辑和API
2. **dating-ai-uniapp** - 基于Uni-app的微信小程序前端

## 安装与设置

### 后端设置

1. 确保已安装JDK 21
2. 确保安装了Maven
3. 安装MongoDB和Redis
4. 安装Milvus向量数据库
5. 克隆仓库
6. 配置环境变量：
   ```
   OPENAI_API_KEY=你的OpenAI API密钥
   JWT_SECRET=你的JWT密钥
   ```
7. 启动服务：
   ```bash
   cd dating-ai-server
   mvn spring-boot:run
   ```

### 前端设置

1. 确保安装了Node.js
2. 安装HBuilderX IDE
3. 克隆仓库
4. 设置API路径：
   ```
   在配置文件中设置后端API路径
   ```
5. 使用HBuilderX打开项目
6. 运行或发布到微信开发者工具

## API文档

API文档可通过以下方式访问：

- 运行后端服务后访问: `http://localhost:8080/doc.html`
- 使用Knife4j浏览并测试API

## 开发指南

### 编码标准

- **Java代码风格**: 遵循项目Java编码标准
- **前端代码风格**: 遵循Vue.js风格指南
- **API设计**: 遵循RESTful设计原则
- **安全实践**: 遵循安全指南文档
- **AI集成**: 参考AI集成指南

### 贡献流程

1. Fork仓库
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建Pull Request

## 许可证

本项目采用[MIT许可证](LICENSE)

</div>

---

<div id="en">

# Dating AI - Dating Chat AI Application

## Project Overview

Dating AI is a dating chat artificial intelligence application that provides chat services through AI-driven dating conversation techniques, helping users improve their social interaction skills. The project includes a Java backend service and a WeChat Mini Program frontend based on Uni-app.

### Core Features

- AI-driven dating chat techniques
- Natural and fluid conversation experience
- WeChat Mini Program client
- Java Spring Boot backend

## Architecture

### Backend Architecture

The backend is built with Spring Boot 3.0, based on JDK 21, and adopts a layered architecture design:

```
[Project].[Module].[Layer]
```

Layer structure:
- `domain/meta` - Domain entities/models
- `dao` - Data Access Objects
- `dto` - Data Transfer Objects
- `vo` - View Objects
- `service` - Business logic implementation
- `controller` - REST API endpoints
- `config` - Configuration classes
- `utils` - Utility classes

### Frontend Architecture

The frontend is built using the Uni-app framework for WeChat Mini Program, following the standard Uni-app structure:

- `pages/` - Application pages and page components
- `components/` - Reusable UI components
- `static/` - Static resources
- `store/` - State management
- `utils/` - Helper functions and tools
- `api/` - API clients for backend communication

## Technology Stack

### Backend Technologies

- **Framework**: Spring Boot 3.2
- **JDK**: Java 21
- **AI Model**: Spring AI 1.0.0-M8, OpenAI integration
- **Databases**: MongoDB, Redis
- **Vector Database**: Milvus
- **API Documentation**: Swagger 3.0 / Knife4J
- **JSON Processing**: FastJSON 2
- **Libraries**: Lombok, Apache Commons, Apache HTTP Client
- **Testing**: JUnit 5, Mockito

### Frontend Technologies

- **Framework**: Uni-app
- **Component-based**: Vue.js
- **State Management**: Vuex
- **UI Library**: Color UI
- **Styling**: SCSS/LESS
- **Mini Program SDK**: WeChat Mini Program

## Project Modules

The project consists of the following main modules:

1. **dating-ai-server** - Backend server handling AI logic and APIs
2. **dating-ai-uniapp** - WeChat Mini Program frontend based on Uni-app

## Installation & Setup

### Backend Setup

1. Ensure JDK 21 is installed
2. Ensure Maven is installed
3. Install MongoDB and Redis
4. Install Milvus vector database
5. Clone the repository
6. Configure environment variables:
   ```
   OPENAI_API_KEY=your_openai_api_key
   JWT_SECRET=your_jwt_secret
   ```
7. Start the service:
   ```bash
   cd dating-ai-server
   mvn spring-boot:run
   ```

### Frontend Setup

1. Ensure Node.js is installed
2. Install HBuilderX IDE
3. Clone the repository
4. Set API path:
   ```
   Set the backend API path in the configuration file
   ```
5. Open the project with HBuilderX
6. Run or publish to WeChat Developer Tools

## API Documentation

API documentation can be accessed via:

- After running the backend service, visit: `http://localhost:8080/doc.html`
- Use Knife4j to browse and test APIs

## Development Guidelines

### Coding Standards

- **Java Code Style**: Follow the project's Java coding standards
- **Frontend Code Style**: Follow Vue.js style guide
- **API Design**: Follow RESTful design principles
- **Security Practices**: Follow the security guidelines document
- **AI Integration**: Refer to the AI integration guidelines

### Contribution Process

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the [MIT License](LICENSE)

</div> 