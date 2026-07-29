# ExpenseTracker API Automation (Playwright + Cucumber + Java)

This repository contains **REST API** automation for ExpenseTracker v1 backend implemented in Spring Boot.

## Prerequisites
- Java 17+
- Maven 3.9+
- Running ExpenseTracker backend (locally or in an environment)

## Configure base URL
Default is `http://localhost:8080`.

Override using:
- Maven property: `-DbaseUrl=http://host:port`
- Or environment variable: `BASE_URL`

## Run tests
```bash
mvn test -DbaseUrl=http://localhost:8080
```

## Reports
- Cucumber HTML report: `target/cucumber-report.html`

## Tags
- `@smoke` basic happy paths
- `@regression` full suite
- `@negative` validation/error paths
- `@api` API tests