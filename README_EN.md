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

## AI System Design

### Multi-level Prompt Engineering Architecture

Dating Chat AI achieves high-quality conversations through a multi-level prompt engineering approach with the following processing flow:

```
User Input → Input Analysis → Emotion Scoring → Persona Consistency Check → 
Response Strategy Selection → Content Generation → Topic Management → 
Authenticity Enhancement → Unified Response Construction → Final Response
```

### Theoretical Research Foundations

The system is based on research findings from multiple academic fields:

1. **Communication Theory** - Non-linear characteristics of dialogue as a process of joint meaning construction
2. **Cognitive Science** - Limited rationality decision models and selective attention mechanisms
3. **Humanoid Agents Simulation** - Chain-of-thought expression and memory retrieval imbalance phenomena
4. **User Behavior Simulation** - Multi-dimensional personality models and dynamic psychological state systems
5. **Collaborative Agent Design** - Social style differentiation and trust-building mechanisms

### Core AI Components

This project implements several specialized AI processing components:

- **Emotion Scoring System** - Multi-dimensional assessment of user emotional states (0-100 points)
- **Persona Consistency System** - Ensures AI responses align with preset personality traits
- **Response Generation System** - Generates natural responses based on emotion and strategy
- **Topic Management System** - Monitors topic flow and implements natural topic transitions
- **Conversation Memory System** - Manages conversation history and user preferences

### Emotion Scoring Dimensions

The system evaluates user emotions through the following dimensions:
- Sentiment vocabulary analysis (40%)
- Syntactic structure analysis (20%)
- Response speed (15%)
- Conversation coherence (15%)
- Symbol usage (10%)

### Topic Management Strategies

The system implements four humanized topic transition modes:
1. Association-based transitions - Natural transitions through personal associations
2. Return-type jumps - Returning to previously unfinished topics
3. Interruption-based jumps - Expressing sudden thoughts
4. Environment-triggered transitions - Using environmental factors as reasons for transition

## Technical Implementation

### System Architecture

The Dating Chat AI system adopts a layered architecture with the following core module structure:

```
com.dating.ai.chat
├── controller      # API entry points
├── service         # Business logic
│   ├── analyzer    # User input analysis
│   ├── emotion     # Emotion assessment
│   ├── strategy    # Conversation strategy
│   ├── generator   # Response generation
│   └── memory      # Conversation memory
├── model           # Data models
├── config          # Configurations
├── util            # Utilities
└── client          # External AI service clients
```

### Key Components

| Component | Responsibility | Key Classes |
|-----------|----------------|-------------|
| Input Analyzer | Parse user input, extract intents and entities | `InputAnalyzer`, `TopicExtractor` |
| Emotion Evaluator | Assess user emotional state and chat willingness | `EmotionScorer`, `EmotionModel` |
| Strategy Engine | Determine response strategy based on emotion and history | `ResponseStrategy`, `TopicManager` |
| Content Generator | Generate natural language responses based on strategy | `ResponseGenerator`, `TemplateEngine` |
| Conversation Memory | Manage conversation history and user preferences | `ConversationMemory`, `UserProfile` |

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

## References

The AI design of this project references the following research:

1. Zhang et al. (2023). "Humanoid Agents: Platform for Simulating Human-like Generative Agents"
2. Chen et al. (2023). "User Behavior Simulation with Large Language Model based Agents"
3. Wang et al. (2022). "MetaAgents: Simulating Interactions of Human Behaviors for LLM-based Task-oriented Coordination"
4. Smith et al. (2022). "Cognitive Biases in Large Language Models: A Framework for Human-like Dialogue"

## License

This project is licensed under the [MIT License](LICENSE) 