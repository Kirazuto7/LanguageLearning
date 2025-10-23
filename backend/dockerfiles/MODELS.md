# Managing Stable Diffusion Models

This document outlines two methods for adding or changing the Stable Diffusion models used by the `image-api` service.

---

### Method 1: Adding a New Model via Docker Volume (Recommended for Testing)

This method uses the persistent Docker volume to add new models without rebuilding the main image. This is ideal for quickly testing new models or expanding your collection.

1.  **Ensure the Application is Running**

    Start the services using the GPU profile to ensure the `image-api` container is running.
    ```sh
    ./scripts/start.sh -gpu
    ```

2.  **Find the Model URL**

    Go to a site like Hugging Face and find a model you want to use (usually a `.safetensors` file). Right-click the "Download" button and copy the link address.

3.  **Download the Model into the Container**

    Use `docker exec` to run a `wget` command inside the running `image-api` container. This downloads the model directly into the correct volume.

    Replace `<MODEL_URL>` with the URL you copied and `<FILENAME.safetensors>` with the desired output filename.

    ```sh
    docker compose exec image-api wget -O /app/models/Stable-diffusion/<FILENAME.safetensors> "<MODEL_URL>"
    ```

    The new model is now available and can be used by calling the API and specifying its filename.

---

### Method 2: Changing the Default Baked-In Model (For Production Updates)

This method involves rebuilding the image with a new default model. This is the correct approach when you want to permanently change the base model for all future deployments.

1.  **Update the Dockerfile**

    Open `backend/dockerfiles/DockerfileImageAPI` and change the `CHECKPOINT_URL` argument to the URL of your new default model.

2.  **Build and Push the New Image Version**

    Build the image, giving it a new version tag (e.g., `:v2`). Then, push it to Docker Hub.
    ```sh
    # Build and tag the new version
    docker build -t kirazuto7/language-learning-image-api:v2 -f backend/dockerfiles/DockerfileImageAPI .

    # Push the new version to the registry
    docker push kirazuto7/language-learning-image-api:v2
    ```

3.  **Update Docker Compose**

    In `docker-compose.gpu.yml`, update the `image` tag for the `image-api` service to point to your new version (e.g., `kirazuto7/language-learning-image-api:v2`). The next time you start the application, it will pull and use the updated image.