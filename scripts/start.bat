@echo off
setlocal enabledelayedexpansion

:: Change directory to the project root (one level up from the script's location)
pushd "%~dp0\.."

set "DO_CPU=false"
set "DO_GPU=false"

:arg_loop
if "%~1"=="" goto :main_logic
if /i "%~1"=="-cpu" set "DO_CPU=true"
if /i "%~1"=="-gpu" set "DO_GPU=true"
shift
goto :arg_loop

:main_logic
echo Before building the application, make sure you create a .env file in the root
echo directory of the project and include your postgres db setup credentials

call :DockerStartup
call :DockerCleanup
call :DockerBuild

echo Once the containers are built and running, you can view the application at http://localhost:3000/.

popd
goto :eof

:: SubRoutines Defined Below
:DockerStartup
    echo.
    echo ****************************************
    echo *        Checking Docker Status        *
    echo ****************************************
    echo.

    docker info > nul 2>nul
    if %ERRORLEVEL% neq 0 (
        echo Docker is not running. Attempting to start Docker Desktop...
        if exist "%ProgramFiles%\Docker\Docker\Docker Desktop.exe" (
            start "" "%ProgramFiles%\Docker\Docker\Docker Desktop.exe"
            echo Waiting for Docker to initialize ^(Start Docker^)...
            set /a DOCKER_WAIT_ATTEMPTS=0
            :wait_for_docker
            timeout /t 15 /nobreak >nul
            docker info >nul 2>nul
            
            if !ERRORLEVEL! neq 0 (
                set /a DOCKER_WAIT_ATTEMPTS+=1
                if !DOCKER_WAIT_ATTEMPTS! geq 12 (
                    echo Docker failed to start after 3 minutes. Exiting.
                    exit /b 1
                )
                goto wait_for_docker
            )
            echo Docker started successfully.
        ) else (
            echo "Docker Desktop.exe" not found. Please start it manually and press any key to continue.
            pause
        )
    ) else (
        echo Docker is running.
    )
    exit /b 0

:DockerCleanup
    echo.
    echo **********************************************
    echo *   Ensuring a clean state before starting...  *
    echo **********************************************
    echo.
    if "%DO_GPU%" == "true" (
        docker compose -f docker-compose.gpu.yml down
    ) else (
        docker compose -f docker-compose.cpu.yml down
    )
    echo Closing any existing DockerWindow...
    taskkill /F /FI "WINDOWTITLE eq DockerWindow*" /T 2>nul
    exit /b 0

:DockerBuild
echo.
    echo **********************************************
    echo *        Building Docker Environment         *
    echo **********************************************
    echo.

    if "%DO_GPU%" == "true" (
        start "DockerWindow" cmd /k "docker compose -f docker-compose.gpu.yml up --build -d"
    ) else (
        start "DockerWindow" cmd /k "docker compose -f docker-compose.cpu.yml up --build -d"
    )
    exit /b 0

:end
