
COMPOSE_FILE=docker-compose.yml

GRADLE=./gradlew

.PHONY: build up down destroy

lint:
	$(GRADLE) ktlintCheck

lint-fix:
	$(GRADLE) ktlintFormat

coverage:
	$(GRADLE) test jacocoRootReport

test:
	$(GRADLE) test --parallel

pre-commit: lint-fix test

install-hooks:
	cp scripts/hooks/pre-commit .git/hooks/pre-commit
	chmod +x .git/hooks/pre-commit

dev:
	docker compose -f $(COMPOSE_FILE) up -d db migrator
	$(GRADLE) :app:bootRun

up:
	docker compose up --build --remove-orphans

down:
	docker compose down

destroy:
	docker compose down -v

build:
	docker compose build

