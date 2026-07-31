# Playwright + Cucumber API Tests (Expense Tracker Backend)

API test suite using **Playwright (Java)** + **Cucumber** against the Expense Tracker backend.

## Prerequisites

- **Java 17**
- **Maven 3.9+**
- **Expense Tracker backend running** and reachable (locally or remotely)

## Configure API Base URL

Set the backend base URL using either:

- JVM system property:
  - `-Dapi.baseUrl=http://localhost:8080`
- Environment variable:
  - `API_BASE_URL=http://localhost:8080`

If both are set, `-Dapi.baseUrl` takes precedence.

## Run Tests

### Run all tests

```bash
mvn test
```

With explicit base URL:

```bash
mvn test -Dapi.baseUrl=http://localhost:8080
```

Or using environment variable:

```bash
API_BASE_URL=http://localhost:8080 mvn test
```

### Run by tags

Run a specific tag:

```bash
mvn test -Dcucumber.filter.tags="@smoke"
```

Combine tags:

```bash
mvn test -Dcucumber.filter.tags="@smoke and not @wip"
```

## Reports

Test reports are generated under:

- `target/cucumber-reports/`
- `target/surefire-reports/`