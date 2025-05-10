# Dating AI

<div align="center">
  <img src="static/images/logo.png" alt="Dating AI Logo" width="200" height="auto">
  <br>
  <p>AI-driven dating conversation platform</p>
  <p>基于人工智能的约会对话平台</p>
  <br>
  <p>
    <a href="README_CN.md">中文文档</a> •
    <a href="README_EN.md">English Documentation</a>
  </p>
</div>

## Overview / 概述

Dating AI is a chat application that uses AI to help users improve their dating conversation skills. The project includes a Java Spring Boot backend and a WeChat Mini Program frontend built with Uni-app.

Dating AI 是一个利用人工智能帮助用户提升约会对话能力的聊天应用。项目包含基于Spring Boot的Java后端和使用Uni-app构建的微信小程序前端。

## Core Innovation / 核心创新

### Advanced AI Architecture / 高级AI架构

- **Multi-level Prompt Engineering**: Implements a sophisticated 5-layer prompt architecture for natural conversation flow
- **Emotion-Aware Response System**: Real-time emotion analysis with 5-dimensional scoring (0-100 points)
- **Persona Consistency Engine**: Maintains consistent personality traits across conversations
- **Dynamic Topic Management**: Implements 4 human-like topic transition strategies
- **Memory-Augmented Generation**: Long-term conversation memory with vector-based retrieval

### Technical Highlights / 技术亮点

- **Spring AI Integration**: First implementation of Spring AI 1.0.0-M8 in a production environment
- **Vector Database**: Milvus-powered semantic search for conversation history
- **Real-time Processing**: Sub-100ms response time for emotion analysis
- **Scalable Architecture**: Microservices-ready design with clear separation of concerns
- **Advanced Caching**: Multi-level caching strategy using Redis

## Features / 特性

- 🧠 AI-driven conversation techniques / AI驱动的对话技术
- 🎯 Human-like personality simulation / 类人格模拟
- 🔄 Natural topic management / 自然话题管理
- 📱 WeChat Mini Program UI / 微信小程序界面
- 🌐 RESTful API backend / RESTful API后端

## Architecture / 架构

### Backend Stack / 后端技术栈
- Java 21 with Spring Boot 3.2
- MongoDB for document storage
- Redis for caching and session management
- Milvus for vector similarity search
- Spring AI 1.0.0-M8 with OpenAI integration

### Frontend Stack / 前端技术栈
- Uni-app framework
- Vue.js for component-based development
- WeChat Mini Program SDK
- Color UI for modern interface design

## AI Implementation / AI实现

### Conversation Flow / 对话流程
```
User Input → Input Analysis → Emotion Scoring → Persona Check → 
Strategy Selection → Response Generation → Topic Management → 
Authenticity Enhancement → Final Response
```

### Emotion Analysis / 情绪分析
- Sentiment vocabulary analysis (40%)
- Syntactic structure analysis (20%)
- Response speed (15%)
- Conversation coherence (15%)
- Symbol usage (10%)

### Topic Management / 话题管理
1. Association-based transitions
2. Return-type jumps
3. Interruption-based jumps
4. Environment-triggered transitions

## Documentation / 文档

For detailed documentation, please refer to the language-specific README files:
详细文档请参阅特定语言的README文件：

- [中文文档](README_CN.md)
- [English Documentation](README_EN.md)

## Contributing / 贡献

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

## License / 许可证

[MIT License](LICENSE) 