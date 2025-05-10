# Dating AI Server

Backend server for Dating AI application, built with Spring Boot 3 and JDK 21.

## Technology Stack

- Spring Boot 3.2.2
- JDK 21
- MongoDB 6.0
- Redis 6
- Spring AI 1.0.0-M8
- Knife4j/Swagger 3.0
- JUnit 5 and Mockito
- FastJSON 2
- Lombok
- Apache Commons
- Apache HTTP Client

## Project Structure

The project follows a modular structure:

```
com.dating.ai
├── config        # Configuration classes
├── controller    # REST API controllers
├── domain        # Domain entities (MongoDB documents)
├── dao           # Data access objects
├── dto           # Data transfer objects
├── vo            # View objects
├── service       # Business logic services
└── utils         # Utility classes
```

## Getting Started

### Prerequisites

- JDK 21
- Maven 3.8+
- MongoDB 6.0
- Redis 6

### Setup

1. Clone the repository
2. Configure application properties in `src/main/resources/application.yml`
3. Set environment variables:
   - `OPENAI_API_KEY`: Your OpenAI API key
   - `OPENAI_BASE_URL`: OpenAI API base URL (optional)

### Building

```bash
mvn clean package
```

### Running

```bash
java -jar target/dating-ai-server-1.0.0-SNAPSHOT.jar
```

### API Documentation

API documentation is available at: `http://localhost:8080/api/doc.html`

## Features

- AI-driven dating chat assistance
- User profile management
- Chat history management
- AI personality customization

## Development Guidelines

Refer to the project documentation for:
- Java coding standards
- API design guidelines
- Testing requirements 