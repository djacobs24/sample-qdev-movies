
# Java Project Development Rules

## Code Style and Standards

### Java Conventions
- Use camelCase for variables and methods
- Use PascalCase for class names
- Use UPPER_SNAKE_CASE for constants
- Follow Oracle Java naming conventions
- Maintain consistent indentation (4 spaces)

### Spring Boot Best Practices
- Use `@RestController` for REST endpoints
- Implement proper exception handling with `@ControllerAdvice`
- Use `@Service` for business logic layers
- Apply `@Repository` for data access layers
- Leverage Spring Boot's auto-configuration when possible

### AWS SDK Integration
- Use AWS SDK v2 (already configured in pom.xml)
- Implement proper credential management (IAM roles, not hardcoded keys)
- Use AWS SDK builders for service clients
- Handle AWS service exceptions appropriately
- Implement retry logic for transient failures

## Code Quality Requirements

### Error Handling
- Always use try-catch blocks for external service calls
- Log errors with appropriate log levels
- Return meaningful error messages to clients
- Use custom exceptions for business logic errors

### Testing
- Write unit tests for all service methods
- Use `@MockBean` for Spring Boot integration tests
- Test AWS service integrations with mocked clients
- Maintain minimum 80% code coverage

### Security
- Never hardcode credentials or sensitive data
- Use Spring Security for authentication/authorization
- Validate all input parameters
- Implement proper CORS configuration
- Use HTTPS in production environments

## Architecture Guidelines

### Dependency Management
- Keep dependencies up to date
- Use Spring Boot's dependency management
- Avoid unnecessary dependencies
- Document any custom dependencies

### Performance
- Use connection pooling for AWS services
- Implement caching where appropriate
- Use async processing for long-running operations
- Monitor and log performance metrics

## Documentation Requirements
- Add JavaDoc comments for public methods
- Document API endpoints with clear descriptions
- Include usage examples in README
- Document configuration properties
