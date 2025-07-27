
COMPOSE_FILE=docker-compose.yml

GRADLE=./gradlew

.PHONY: build up down destroy

## 🧼 Run ktlint check (no changes)
lint:
	$(GRADLE) ktlintCheck

## 🧽 Run ktlint and auto-fix formatting
lint-fix:
	$(GRADLE) ktlintFormat

## 🔨 Build the project (compile + test)
build: lint-fix
	$(GRADLE) clean build

## 📦 Run Flyway migrations for Postgres module
migrate:
	$(GRADLE) --no-configuration-cache :output:postgres:flywayMigrate

## ⏳ Wait for Postgres to become ready
wait-db:
	@echo "⏳ Waiting for Postgres to be ready..."
	@until docker exec recipes-cart-db pg_isready -U postgres > /dev/null 2>&1; do \
		sleep 1; \
	done
	@echo "✅ Postgres is ready!"

## 🚀 Start PostgreSQL container (in background)
up: build
	docker compose -f $(COMPOSE_FILE) up -d
	make wait-db
	make migrate

## 🛑 Stop PostgreSQL container (but keep volumes)
down:
	docker compose -f $(COMPOSE_FILE) down

## 💣 Stop and remove containers, **volumes**, and network
destroy: down
	docker compose -f $(COMPOSE_FILE) down -v

## 🆕 Generate a timestamped empty migration file
new-migration:
	@if [ -z "$(name)" ]; then \
		echo "❌ Please provide a migration name: make new-migration name=add_cart_table"; \
		exit 1; \
	fi; \
	timestamp=$$(date +"%Y%m%d%H%M%S"); \
	filename="output/postgres/src/main/resources/db/migration/V$${timestamp}__$(name).sql"; \
	mkdir -p output/postgres/src/main/resources/db/migration; \
	echo "-- Migration: $(name)" > $$filename; \
	echo "✅ Created migration: $$filename"
