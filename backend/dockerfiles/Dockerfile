# This Dockerfile is optimized for local development with hot-reloading.
# It relies on a volume mount from docker-compose.yml to get the source code.

# Use a base image with Gradle and the correct JDK for development
FROM gradle:8.5-jdk21

# Install necessary packages:
# - curl: required for the healthcheck in docker-compose.
USER root
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Set the working directory inside the container
WORKDIR /app

# The default command to run when the container starts.
CMD ["gradle", "bootRun", "-Pdocker", "--no-daemon"]
