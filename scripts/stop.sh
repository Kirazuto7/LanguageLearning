#!/bin/bash
set -e

# Get the directory of the script
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"

# Navigate to the project root (one level up from the scripts directory)
cd "$SCRIPT_DIR/.."

# --- Default values ---
USE_GPU=false
COMPOSE_FILE="docker-compose.cpu.yml"
PROFILE_ARG=""
DO_HARD_CLEANUP=false

# --- Argument Parsing ---
for arg in "$@"
do
    case $arg in
        -gpu|--gpu)
        USE_GPU=true
        COMPOSE_FILE="docker-compose.gpu.yml"
        shift
        ;;
        -cpu|--cpu)
        USE_GPU=false
        COMPOSE_FILE="docker-compose.cpu.yml"
        shift
        ;;
        --frontend)
        PROFILE_ARG="--profile frontend"
        shift
        ;;
        -v|--volumes)
        DO_HARD_CLEANUP=true
        shift
        ;;
    esac
done

# --- Main Logic ---

echo ""
echo "**********************************************"
echo "*      Stopping Docker Environment...        *"
echo "**********************************************"
echo ""

if [ "$DO_HARD_CLEANUP" = true ]; then
    echo "Performing hard cleanup (removing volumes and pruning builder cache)..."
    docker compose $PROFILE_ARG -f "$COMPOSE_FILE" down -v
    docker builder prune -f
    docker volume prune -f
    echo "Hard cleanup complete."
else
    echo "Stopping containers..."
    docker compose $PROFILE_ARG -f "$COMPOSE_FILE" down
    echo "Containers stopped."
fi