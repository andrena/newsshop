#!/bin/bash

set -e

COMPOSE_FILE=".devcontainer/docker-compose.yml"
SERVICE="dev"

echo "Starte ${COMPOSE_FILE}..."
docker compose -f "$COMPOSE_FILE" up -d --build

echo "Verbinde mit Container '$SERVICE'..."
docker compose -f "$COMPOSE_FILE" exec "$SERVICE" bash