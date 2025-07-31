FROM openjdk:17-jdk-slim

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy Maven files
COPY pom.xml .
COPY src ./src

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Build the application
RUN mvn clean package -DskipTests

# Download Datadog Java agent
RUN curl -L -o /app/dd-java-agent.jar https://dtdg.co/latest-java-tracer

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/api/greeting || exit 1

# Run the application with Datadog agent
CMD ["java", \
     "-javaagent:/app/dd-java-agent.jar", \
     "-Ddd.service=java-check-404", \
     "-Ddd.env=development", \
     "-Ddd.version=1.0.0", \
     "-Ddd.trace.sample.rate=1.0", \
     "-Ddd.logs.injection=true", \
     "-Ddd.traces.startup.logs=true", \
     "-jar", "target/java-check-404-1.0.0.jar"] 