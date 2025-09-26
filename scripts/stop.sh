#!/bin/bash
set -e

# Get the directory of the script
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"

# Navigate to the project root (one level up from the scripts directory)
cd "$SCRIPT_DIR/.."

# --- Default values ---
USE_GPU=false
COMPOSE_FILE="docker-compose.cpu.yml"
DO_HARD_CLEANUP=false

# --- Argument Parsing ---
while [[ $# -gt 0 ]]; do
    case "$1" in
        -gpu|--gpu)
            USE_GPU=true
            COMPOSE_FILE="docker-compose.gpu.yml"
            ;;
        -cpu|--cpu)
            USE_GPU=false
            COMPOSE_FILE="docker-compose.cpu.yml"
            ;;
        -v|--volumes)
            DO_HARD_CLEANUP=true
            ;;
    esac
    shift
done

# --- Main Logic ---

echo ""
echo "**********************************************"
echo "*      Stopping Docker Environment...        *"
echo "**********************************************"
echo ""

if [ "$DO_HARD_CLEANUP" = true ]; then
    echo "Performing hard cleanup (removing volumes and pruning builder cache)..."
    docker compose -f "$COMPOSE_FILE" down -v
    docker builder prune -af
    docker volume prune -f
    echo "Hard cleanup complete."
else
    echo "Stopping containers..."
    docker compose -f "$COMPOSE_FILE" down
    echo "Containers stopped."
fi