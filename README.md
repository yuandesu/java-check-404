# Java Check 404 - Simple Java Application with Datadog Tracing

A simple Netty-based Java application that accepts GET and POST requests with integrated Datadog tracing and monitoring.

## API Endpoints

### GET /api/greeting
Returns a greeting message.

**Response:**
```
Hi ! I know you are using netty.
```

### GET /api/not-found
Always returns a 404 status code with a JSON response.

**Response:**
```json
{
  "message": "This endpoint always returns 404",
  "timestamp": "2024-01-01T12:00:00",
  "status": "not_found"
}
```

## Prerequisites

- Docker and Docker Compose
- Datadog account (optional, for full monitoring)

## Setup

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd java-check-404
   ```

2. **Configure Datadog (Optional):**
   - Copy `env.example` to `.env`
   - Add your Datadog API key to the `.env` file:
     ```bash
     cp env.example .env
     # Edit .env and add your DD_API_KEY
     ```

3. **Start the application:**
   ```bash
   # Default (404 rule enabled)
   docker-compose up -d --build
   
   # Dynamic configuration
   DD_TRACE_STATUS404RULE_ENABLED=false docker-compose up -d --build
   ```

4. **Access the application:**
   - Application: http://localhost:8080

## Testing the API

### Greeting Endpoint
```bash
curl http://localhost:8080/api/greeting
```

### Test 404 Endpoint
```bash
curl -w "HTTP Status: %{http_code}\n" http://localhost:8080/api/not-found
```

## Datadog Integration

### 404 Status Rule Configuration

This application demonstrates Datadog's 404 status rule behavior:

**Default Behavior (DD_TRACE_STATUS404RULE_ENABLED=true):**
- When an endpoint returns HTTP 404, Datadog automatically changes the resource name to `404`
- Example: `/api/not-found` вк resource becomes `404`
- **This is the default behavior when no configuration is set**

**With 404 Rule Disabled (DD_TRACE_STATUS404RULE_ENABLED=false):**
- 404 responses keep their original resource name
- Example: `/api/not-found` вк resource remains `GET /api/not-found`

**Configuration Options:**
```bash
# In Dockerfile (JVM argument)
-Ddd.trace.status404rule.enabled=false

# In docker-compose.yml (environment variable)
DD_TRACE_STATUS404RULE_ENABLED=false

# Dynamic configuration at runtime
DD_TRACE_STATUS404RULE_ENABLED=true docker-compose up -d --build
DD_TRACE_STATUS404RULE_ENABLED=false docker-compose up -d --build
```

**Default Value:**
- Datadog's default is `DD_TRACE_STATUS404RULE_ENABLED=true`
- This application also defaults to `true` (404 responses become "404" resource)

**Testing Purpose:**
This application is designed to test and demonstrate how Datadog handles 404 responses and how the status404rule configuration affects resource naming in traces.

### Viewing Traces

1. Go to your Datadog dashboard
2. Navigate to APM > Services
3. Look for the `java-check-404` service
4. View traces, metrics, and logs

## Development

### Local Development (without Docker)

1. **Install Java 17 and Maven**
2. **Set environment variables:**
   ```bash
   export DD_API_KEY=your-api-key
   export DD_AGENT_HOST=localhost
   export DD_TRACE_AGENT_PORT=8126
   ```

3. **Run the application:**
   ```bash
   mvn clean package
   java -jar target/java-check-404-1.0.0.jar
   ```

### Building the JAR

```bash
mvn clean package
java -javaagent:dd-java-agent.jar -jar target/java-check-404-1.0.0.jar
```

## Frequently Use Docker Commands

### Build and run
```bash
docker-compose up --build
```

### Run in background
```bash
docker-compose up -d --build
```

### View logs
```bash
docker-compose logs -f app
```

### Stop services
```bash
docker-compose down
```

## Troubleshooting

### Application not starting
- Check if port 8080 is available
- Verify Docker and Docker Compose are installed
- Check application logs: `docker-compose logs app`

### Datadog agent issues
- Verify your API key is correct
- Check agent logs: `docker-compose logs datadog-agent`
- Ensure the agent can reach Datadog servers

### No traces appearing
- Verify the Datadog agent is running
- Check that the Java agent is properly configured
- Ensure the application is making requests

## Project Structure

```
java-check-404/
изибиб src/
ив   ижибиб main/
ив       изибиб java/com/example/
ив       ив   изибиб Application.java
ив       ив   ижибиб controller/
ив       ив       ижибиб ApiController.java
ив       ижибиб resources/
ив           ижибиб application.yml
изибиб Dockerfile
изибиб docker-compose.yml
изибиб pom.xml
изибиб env.example
ижибиб README.md
```

## License

This project is for educational purposes. 