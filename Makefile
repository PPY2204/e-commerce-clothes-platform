# Yamatee Club - Ultimate Developer Experience Makefile

.PHONY: build up down logs restart clean ssl ps help

# Variables
DC=docker-compose
ENV_FILE=.env

help:
	@echo "Available commands:"
	@echo "  make ssl     - Generate self-signed SSL certificates"
	@echo "  make build   - Build all microservices"
	@echo "  make up      - Start the entire platform (detached)"
	@echo "  make lite    - Start core services only (saves RAM)"
	@echo "  make down    - Stop and remove all containers"
	@echo "  make logs    - View real-time logs of all services"
	@echo "  make restart - Restart all services"
	@echo "  make clean   - Clean maven targets and docker images"
	@echo "  make ps      - Show running containers"

ssl:
	@echo "Generating SSL certificates..."
ifeq ($(OS),Windows_NT)
	powershell.exe -ExecutionPolicy Bypass -File ./infrastructure/scripts/generate-ssl.ps1
else
	bash ./infrastructure/scripts/generate-ssl.sh
endif

build:
	@echo "Building microservices (Parallel)..."
	$(DC) build

up:
	@echo "Starting the platform..."
ifeq ($(OS),Windows_NT)
	@if not exist $(ENV_FILE) copy .env.example $(ENV_FILE)
else
	@if [ ! -f $(ENV_FILE) ]; then cp .env.example $(ENV_FILE); fi
endif
	$(DC) up -d --build

lite:
	@echo "Starting Core Services (Lite Mode)..."
ifeq ($(OS),Windows_NT)
	@if not exist $(ENV_FILE) copy .env.example $(ENV_FILE)
else
	@if [ ! -f $(ENV_FILE) ]; then cp .env.example $(ENV_FILE); fi
endif
	$(DC) up -d --build postgres redis mongodb localstack kafka maildev swagger-ui keycloak user-service product-catalog-service order-service shopping-cart-service notification-service kong nginx fe

down:
	@echo "Stopping the platform..."
	$(DC) down

reset:
	@echo "Hard resetting the platform (Deleting all volumes)..."
	$(DC) down -v
	$(MAKE) lite

logs:
	$(DC) logs -f

restart:
	$(DC) restart

ps:
	$(DC) ps

clean:
	@echo "Cleaning artifacts..."
	mvn clean
	$(DC) down --rmi all --volumes --remove-orphans
