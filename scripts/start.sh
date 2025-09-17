#!/bin/bash
set -e # Exit immediately if a command exits with a non-zero status.

# Get the directory of the script
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"

# Navigate to the project root (one level up from the scripts directory)
cd "$SCRIPT_DIR/.."

# --- Default values ---
USE_GPU=false
COMPOSE_FILE="docker-compose.cpu.yml"
PROFILE_ARG=""

# --- Argument Parsing ---
# A simple loop to process all arguments provided
for arg in "$@"
do
    case $arg in
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
        --frontend)
        PROFILE_ARG="--profile frontend"
        shift # Remove --frontend from processing
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
    echo "************************************************"
    echo "* Ensuring a clean state before starting...  *"
    echo "************************************************"
    echo ""
    docker compose $PROFILE_ARG -f "$COMPOSE_FILE" down
}

# --- Main Logic ---

check_docker_status

echo "Before building the application, make sure you create a .env file in the root"
echo "directory of the project and include your postgres db setup credentials"

docker_cleanup

echo ""
echo "**********************************************"
echo "*        Building Docker Environment         *"
echo "**********************************************"
echo ""
docker compose $PROFILE_ARG -f "$COMPOSE_FILE" up --build -d

echo ""
echo "Docker environment is starting up in the background."
echo "To include the frontend dev server, use the --frontend flag."
echo "Once the containers are built and running, you can view the application at http://localhost:3000/."
echo "To view logs, run: docker compose $PROFILE_ARG -f $COMPOSE_FILE logs -f"