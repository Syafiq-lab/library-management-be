# Build stage
FROM maven:3.9-amazoncorretto-23 AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests

# Runtime stage
FROM amazoncorretto:23-alpine
WORKDIR /app
# Create a non-root user to run the application
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar
# Make the JAR owned by the non-root user
RUN chown -R appuser:appgroup /app
# Switch to non-root user
USER appuser
# Expose application port
EXPOSE 8080
# Environment variables with defaults
ENV SPRING_PROFILES_ACTIVE=prod \
    DB_USERNAME=maybank \
    DB_PASSWORD=maybank \
    JWT_SECRET=change-me-in-production

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]