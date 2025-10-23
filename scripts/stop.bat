@echo off
setlocal

:: Change directory to the project root (one level up from the script's location)
pushd "%~dp0\.."

set "DO_HARD_CLEANUP=false"
set "USE_GPU=false"
set "COMPOSE_FILE=docker-compose.cpu.yml"

:arg_loop
if "%~1"=="" goto :main_logic
if /i "%~1"=="-gpu" (
    set "USE_GPU=true"
    set "COMPOSE_FILE=docker-compose.gpu.yml"
)
if /i "%~1"=="-cpu" (
    set "USE_GPU=false"
    set "COMPOSE_FILE=docker-compose.cpu.yml"
)
if /i "%~1"=="-v" set "DO_HARD_CLEANUP=true"
shift
goto :arg_loop

:main_logic
echo.
echo **********************************************
echo *      Stopping Docker Environment...        *
echo **********************************************
echo.

if "%DO_HARD_CLEANUP%" == "true" (goto :hard_cleanup) else (goto :soft_cleanup)

:soft_cleanup
echo Stopping containers...
docker compose -f "%COMPOSE_FILE%" down
echo Containers stopped.
goto :end_script

:hard_cleanup
echo Performing hard cleanup (removing volumes and pruning builder cache)...
docker compose -f "%COMPOSE_FILE%" down -v
docker builder prune -f
docker volume prune -f
echo Hard cleanup complete.
goto :end_script

:end_script
popd
goto :eof
