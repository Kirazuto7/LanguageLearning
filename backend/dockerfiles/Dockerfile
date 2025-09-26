# This Dockerfile is optimized for local development with hot-reloading.
# It relies on a volume mount from docker-compose.yml to get the source code.

# Use a base image with Gradle and the correct JDK for development
FROM gradle:8.5-jdk21

# Set the working directory inside the container
WORKDIR /app

# The default command to run when the container starts.
# 'bootRun' starts the application with Spring Boot DevTools, which enables hot-reloading.

CMD ["gradle", "bootRun", "--no-daemon"]
