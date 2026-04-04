# Student REST APIs

A RESTful API for managing students, teachers, and courses built with Spring Boot.

## Prerequisites

- Java 11 or higher
- PostgreSQL 10+
- Maven 3.6+

## Setup Instructions

### 1. Clone the Repository
```bash
git clone <repository-url>
cd StudentRestApis
```

### 2. Configure Environment Variables
```bash
# Copy the example file
cp .env.example .env

# Edit .env with your actual values
# DO NOT commit .env file - it's in .gitignore
```

### 3. Create Application Properties
```bash
# Copy the example configuration
cp src/main/resources/application.properties.example src/main/resources/application.properties

# Edit application.properties with your database credentials
```

### 4. Configure Database

Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/your_database
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 5. Build and Run
```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

## API Documentation

Once the application is running, access the API documentation at:
- **Swagger UI**: http://localhost:8000/api/v1/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8000/api/v1/api-docs

## Security

### Important
- **Never commit** `application.properties` with credentials to version control
- **Never commit** `.env` file containing sensitive information
- All `.properties` and `.env` files are automatically excluded by `.gitignore`
- Use `application.properties.example` and `.env.example` as templates

### Credentials Best Practices
- Use strong, unique passwords for database connections
- Use environment variables for deployment credentials
- Rotate database passwords regularly
- Never hardcode secrets in source code

## Development

### Project Structure
```
src/
├── main/
│   ├── java/com/anas/StudentRestApis/
│   │   ├── Controllers/      # REST endpoints
│   │   ├── Entity/          # JPA entities
│   │   ├── Repository/      # Data access layer
│   │   ├── Service/         # Business logic
│   │   ├── Exception/       # Custom exceptions
│   │   └── config/          # Configuration classes
│   └── resources/
│       ├── application.properties     # Main config (in .gitignore)
│       ├── application.properties.example  # Template
│       └── data.sql         # Sample data
└── test/                    # Test files
```

### Environment Profiles
- **Development**: Use `application-dev.properties.example` as reference

## Troubleshooting

### Database Connection Error
- Verify PostgreSQL is running on localhost:5432
- Check username and password in `application.properties`
- Ensure the database exists

### Port Already in Use
- Default port is 8000, change in `application.properties`:
  ```properties
  server.port=8080
  ```

## License

[Your License Here]

## Contributing

See CONTRIBUTING.md for guidelines.
