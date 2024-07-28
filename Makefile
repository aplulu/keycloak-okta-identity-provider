.PHONY: up # Run the application
up: build
	@echo "Running the application..."
	docker compose build
	docker compose up

.PHONY: build # Build the application
build:
	mvn clean package
