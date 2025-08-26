@echo off
setlocal enabledelayedexpansion

set "DO_CLEANUP=false"

:arg_loop
if "%~1"=="" goto :args_done
if /i "%~1"=="-remove" set "DO_CLEANUP=true"
:args_done

if "%DO_CLEANUP%" == "true" (
    call :DockerStartup
    call :DockerHardCleanup
    goto :eof
)

echo Before building the application, make sure you create a .env file in the root
echo directory of the project and include your postgres db setup credentials

call :DockerStartup
call :DockerCleanup
call :DockerBuild

echo Once the containers are built and running, you can view the application at http://localhost:3000/.

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
    echo *        Removing Docker Container(s)        *
    echo **********************************************
    echo.

    docker-compose down
    echo Closing any existing DockerWindow...
    taskkill /F /FI "WINDOWTITLE eq DockerWindow*" /T 2>nul
    exit /b 0

:DockerHardCleanup
    echo.
    echo **********************************************
    echo *        Removing Docker Container(s)        *
    echo **********************************************
    echo.

    docker-compose down -v
    docker builder prune -f
    echo Closing any existing DockerWindow...
    taskkill /F /FI "WINDOWTITLE eq DockerWindow*" /T 2>nul
    exit /b 0

:DockerBuild
echo.
    echo **********************************************
    echo *        Building Docker Environment         *
    echo **********************************************
    echo.

    start "DockerWindow" cmd /k "docker-compose up --build --force-recreate"
    exit /b 0

:end
