#!/bin/bash
set -e # Exit immediately if a command exits with a non-zero status.

# --- Default values ---
DO_HARD_CLEANUP=false
USE_GPU=false
COMPOSE_FILE="docker-compose.cpu.yml"

# --- Argument Parsing ---
# A simple loop to process all arguments provided
for arg in "$@"
do
    case $arg in
        -remove|--remove)
        DO_HARD_CLEANUP=true
        shift # Remove --remove from processing
        ;;
        -gpu|--gpu)
        USE_GPU=true
        COMPOSE_FILE="docker-compose.gpu.yml"
        shift # Remove --gpu from processing
        ;;
        -cpu|--cpu)
        USE_GPU=false
        COMPOSE_FILE="docker-compose.cpu.yml"
        shift # Remove --cpu from processing
        ;;
    esac
done

# --- Subroutines (as functions) ---

# Function to check if Docker is running
check_docker_status() {
    echo ""
    echo "****************************************"
    echo "*        Checking Docker Status        *"
    echo "****************************************"
    echo ""

    if ! docker info > /dev/null 2>&1; then
        echo "Docker is not running. Please start Docker Desktop and try again."
        exit 1
    else
        echo "Docker is running."
    fi
}

# Function for standard cleanup
docker_cleanup() {
    echo ""
    echo "**********************************************"
    echo "*        Removing Docker Container(s)        *"
    echo "**********************************************"
    echo ""
    docker-compose -f "$COMPOSE_FILE" down
}

# Function for hard cleanup (including volumes)
docker_hard_cleanup() {
    echo ""
    echo "**********************************************"
    echo "*    Removing Containers & Pruning Volumes   *"
    echo "**********************************************"
    echo ""
    docker-compose -f "$COMPOSE_FILE" down -v
    docker builder prune -f
    docker volume prune -f
}

# --- Main Logic ---

check_docker_status

if [ "$DO_HARD_CLEANUP" = true ]; then
    docker_hard_cleanup
    echo "Hard cleanup complete."
    exit 0
fi

echo "Before building the application, make sure you create a .env file in the root"
echo "directory of the project and include your postgres db setup credentials"

docker_cleanup

echo ""
echo "**********************************************"
echo "*        Building Docker Environment         *"
echo "**********************************************"
echo ""
docker-compose -f "$COMPOSE_FILE" up --build -d

echo ""
echo "Docker environment is starting up in the background."
echo "Once the containers are built and running, you can view the application at http://localhost:3000/."
echo "To view logs, run: docker-compose -f $COMPOSE_FILE logs -f"