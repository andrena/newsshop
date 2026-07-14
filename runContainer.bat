@echo off
setlocal

set "COMPOSE_FILE=.devcontainer/docker-compose.yml"
set "SERVICE=dev"

echo Starte %COMPOSE_FILE%...
call docker compose -f "%COMPOSE_FILE%" up -d --build

echo Verbinde mit Container '%SERVICE%'...
call docker compose -f "%COMPOSE_FILE%" exec "%SERVICE%" bash
