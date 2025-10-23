#!/bin/sh
set -e

 # This script runs each time the 'ai' container starts.
 # It checks if the models have already been pulled by looking for a marker file.

# Make the marker file specific to the profile to handle profile switching.
MARKER_FILE="/root/.ollama/models_pulled_${AI_PROFILE:-cpu}.marker"

if [ ! -f "$MARKER_FILE" ]; then
  echo "--- First time startup: Pulling models... ---"

  # Start the server in the background to pull models
  ollama serve &
  PID=$!

  # Wait for the server to be ready by polling the root endpoint.
  echo "Waiting for Ollama server to start..."
  until curl -s -f -o /dev/null http://localhost:11434; do
      printf '.'
      sleep 1
  done
  echo "Ollama server started."

  # Pull the required models
  if [ "$AI_PROFILE" = "gpu" ]; then
    ollama pull qwen3:8b
    #ollama pull exaone3.5:7.8b
  else
    ollama pull qwen3:4b
    #ollama pull exaone3.5:2.4b
  fi
    echo "--- Models pulled successfully. ---"
    touch "$MARKER_FILE"
    kill $PID
    wait $PID
else
  echo "--- Models already present, skipping pull. ---"
fi

echo "--- Starting Ollama server... ---"
ollama serve &
PID=$!

# Always create the marker file to signal that the setup is complete and the server is running.
touch "$MARKER_FILE"

echo "--- AI service is ready. Keeping container alive. ---"
wait $PID
