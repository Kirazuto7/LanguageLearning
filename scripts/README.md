# Project Scripts Guide

This directory contains scripts to manage the Docker environment for the Language Learning application.

## Prerequisites

1.  **Docker Desktop**: Ensure Docker Desktop is installed and running on your system.
2.  **`.env` file**: Before running any scripts, you must create a `.env` file in the project's root directory. This file should contain your database credentials. See `.env.example` for the required format.

---

## How to Start the Environment

The `start` scripts will build and run the necessary Docker containers.

### On Linux or macOS (or WSL)

```bash
./scripts/start.sh
```

### On Windows (CMD or PowerShell)

```powershell
.\scripts\start.bat
```

#### Start Options:

*   `--frontend`: Use this flag to also start the frontend development server.
    ```bash
    ./scripts/start.sh --frontend
    ```
*   `-gpu`: Use this flag if you have a compatible NVIDIA GPU and want to use it for AI tasks.
    ```bash
    ./scripts/start.sh -gpu
    ```

---

## How to Stop the Environment

The `stop` scripts will gracefully shut down all running containers.

### On Linux or macOS (or WSL)

```bash
./scripts/stop.sh
```

### On Windows (CMD or PowerShell)

```powershell
.\scripts\stop.bat
```

#### Stop Options:

*   `-v`: Use this flag for a "hard stop" that removes all containers, their associated volumes (including database data), and prunes the Docker builder cache. **Use with caution, as this will delete your database.**
    ```bash
    ./scripts/stop.sh -v
    ```