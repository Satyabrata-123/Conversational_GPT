# Satya GPT - Conversational AI with Spring Boot & React

A full-stack conversational AI application built with Spring Boot backend, React frontend, and MySQL database. Features conversation history, thread management, and integration with Google's Gemini AI.

## Features

- 🤖 **AI-Powered Chat**: Integration with Google Gemini AI
- 💬 **Conversation History**: Persistent chat threads with MySQL
- 🎨 **Modern UI**: React-based responsive interface
- 🔄 **Real-time Updates**: Dynamic conversation management
- 🐳 **Docker Support**: Complete containerization with Docker Compose
- 🔒 **CORS Configured**: Secure cross-origin requests

## Tech Stack

### Backend
- **Spring Boot 3.2.5** - Java framework
- **Spring AI** - AI integration framework
- **Spring Data JPA** - Database abstraction
- **MySQL 8.0** - Database
- **Maven** - Build tool

### Frontend
- **React 18** - UI framework
- **Vite** - Build tool
- **Modern CSS** - Styling

## Quick Start with Docker

### Prerequisites
- Docker and Docker Compose installed
- Google Gemini API key

### 1. Clone the repository
```bash
git clone <repository-url>
cd satya-gpt
```

### 2. Set up environment variables
```bash
cp .env.example .env
# Edit .env and add your Gemini API key
```

### 3. Run with Docker Compose
```bash
docker-compose up -d
```

This will start:
- **MySQL** on port 3306
- **Spring Boot API** on port 8080
- **React Frontend** on port 5173

### 4. Access the application
- Frontend: http://localhost:5173
- Backend API: http://localhost:8080
- Health Check: http://localhost:8080/actuator/health

## Manual Setup (Development)

### Backend Setup
```bash
cd Satya_Gpt

# Install dependencies and run
mvn clean install
mvn spring-boot:run
```

### Frontend Setup
```bash
cd ConversationalUI-main/ConversationalHistory-UI

# Install dependencies
npm install

# Start development server
npm run dev
```

### Database Setup
```bash
# Create MySQL database
mysql -u root -p < database_setup.sql
```

## Configuration

### Environment Variables
- `GEMINI_API_KEY`: Your Google Gemini API key
- `MYSQL_ROOT_PASSWORD`: MySQL root password
- `MYSQL_DATABASE`: Database name
- `MYSQL_USER`: Database user
- `MYSQL_PASSWORD`: Database password

### Application Properties
Key configurations in `application.properties`:
```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/satya_gpt_db
spring.datasource.username=root
spring.datasource.password=Satya@123

# Gemini AI
spring.ai.openai.api-key=${GEMINI_API_KEY}
spring.ai.openai.base-url=https://generativelanguage.googleapis.com/v1beta/openai/
```

## API Endpoints

### Chat Endpoints
- `POST /api/chat` - Send message and get AI response
- `GET /api/conversations` - Get all conversation threads
- `GET /api/conversations/{threadId}` - Get specific conversation
- `POST /api/conversations` - Create new conversation
- `DELETE /api/conversations/{threadId}` - Delete conversation

### Legacy Endpoint
- `GET /api/{message}` - Simple chat endpoint (backward compatibility)

## Docker Commands

### Build and run
```bash
docker-compose up --build
```

### Run in background
```bash
docker-compose up -d
```

### View logs
```bash
docker-compose logs -f
```

### Stop services
```bash
docker-compose down
```

### Clean up (remove volumes)
```bash
docker-compose down -v
```

## Development

### Project Structure
```
├── Satya_Gpt/                          # Spring Boot backend
│   ├── src/main/java/com/Satya/SpringAI/
│   │   ├── Controller/                  # REST controllers
│   │   ├── service/                     # Business logic
│   │   ├── entity/                      # JPA entities
│   │   ├── repository/                  # Data repositories
│   │   ├── dto/                         # Data transfer objects
│   │   └── config/                      # Configuration classes
│   ├── src/main/resources/
│   │   └── application.properties       # App configuration
│   └── Dockerfile                       # Backend container
├── ConversationalUI-main/ConversationalHistory-UI/  # React frontend
│   ├── src/
│   │   ├── App.jsx                      # Main component
│   │   ├── App.css                      # Styles
│   │   └── assets/                      # Static assets
│   ├── Dockerfile                       # Frontend container
│   └── package.json                     # Dependencies
├── docker-compose.yml                   # Docker orchestration
└── README.md                           # This file
```

### Adding New Features
1. **Backend**: Add controllers, services, entities as needed
2. **Frontend**: Update React components and state management
3. **Database**: Create migration scripts for schema changes

## Troubleshooting

### Common Issues

1. **CORS Errors**: Check CorsConfig.java configuration
2. **Database Connection**: Verify MySQL is running and credentials are correct
3. **API Key**: Ensure Gemini API key is valid and set correctly
4. **Port Conflicts**: Make sure ports 5173, 8080, 3306 are available

### Health Checks
- Backend: http://localhost:8080/actuator/health
- Database: Check Docker logs with `docker-compose logs mysql`

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is licensed under the MIT License.