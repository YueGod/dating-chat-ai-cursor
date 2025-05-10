# Dating AI - 恋爱聊天AI应用

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

## AI系统设计

### 多级提示词工程架构

Dating聊天AI通过多级提示词工程实现高质量对话，设计了以下处理流程：

```
用户输入 → 用户输入分析 → 情绪评分 → 人格一致性检查 → 
对话策略选择 → 话术生成 → 主题管理 → 真实性增强 → 
统一回复构建 → 最终回复
```

### 理论研究基础

该系统基于多个学术领域的研究成果：

1. **传播理论** - 对话作为共同意义建构过程的非线性特性
2. **认知科学** - 有限理性决策模型和选择性注意力机制  
3. **人类化代理仿真** - 思维链表达和记忆检索不均衡现象
4. **用户行为模拟** - 多维度个性模型和动态心理状态系统
5. **协作型代理设计** - 社交风格差异化和信任建立机制

### 核心AI组件

本项目实现了多个专门的AI处理组件：

- **情绪评分系统** - 多维度评估用户情绪状态（0-100分）
- **人格一致性系统** - 确保AI回复符合预设人格特质
- **话术生成系统** - 基于情绪和策略生成自然回复
- **主题管理系统** - 监控话题流动并实现自然话题切换
- **对话记忆系统** - 管理对话历史和用户偏好

### 情绪评分维度

系统通过以下维度评估用户情绪：
- 情感词汇分析 (40%)
- 句法结构分析 (20%)
- 反应速度 (15%)
- 对话连贯性 (15%)
- 符号使用 (10%)

### 话题管理策略

实现了四种人类化话题转换模式：
1. 联想式转换 - 通过个人联想自然切换
2. 返回式跳转 - 回到之前未完成的话题
3. 中断式跳转 - 表现突然想到的内容
4. 环境触发式 - 使用环境因素作为转换理由

## 技术实现

### 系统架构

Dating聊天AI系统采用分层架构，核心模块结构如下：

```
com.dating.ai.chat
├── controller      # API入口点
├── service         # 业务逻辑
│   ├── analyzer    # 用户输入分析
│   ├── emotion     # 情绪评估
│   ├── strategy    # 对话策略
│   ├── generator   # 回复生成
│   └── memory      # 对话记忆
├── model           # 数据模型
├── config          # 配置
├── util            # 工具类
└── client          # 外部AI服务客户端
```

### 关键组件

| 组件 | 职责 | 关键类 |
|------|------|-------|
| 输入分析器 | 解析用户输入，提取意图和实体 | `InputAnalyzer`, `TopicExtractor` |
| 情绪评估器 | 评估用户情绪状态和聊天意愿 | `EmotionScorer`, `EmotionModel` |
| 策略引擎 | 根据情绪和历史确定回复策略 | `ResponseStrategy`, `TopicManager` |
| 内容生成器 | 根据策略生成自然语言回复 | `ResponseGenerator`, `TemplateEngine` |
| 对话记忆 | 管理对话历史和用户偏好 | `ConversationMemory`, `UserProfile` |

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

## 参考文献

本项目的AI设计参考了以下研究：

1. Zhang et al. (2023). "Humanoid Agents: Platform for Simulating Human-like Generative Agents"
2. Chen et al. (2023). "User Behavior Simulation with Large Language Model based Agents"
3. Wang et al. (2022). "MetaAgents: Simulating Interactions of Human Behaviors for LLM-based Task-oriented Coordination"
4. Smith et al. (2022). "Cognitive Biases in Large Language Models: A Framework for Human-like Dialogue"

## 许可证

本项目采用[MIT许可证](LICENSE) 