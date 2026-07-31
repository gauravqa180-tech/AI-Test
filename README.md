# Expense Tracker API — BDD Tests (Cucumber)

This repository contains Behavior-Driven Development (BDD) API tests for the **Expense Tracker Spring Boot API**, implemented using **Cucumber**.

## Prerequisites

- **Java 17+**
- **Maven 3.8+**
- Access to a running instance of the **Expense Tracker Spring Boot API**
- Network access from your machine/CI runner to the API host

## How the tests work

- Tests are executed via Maven and Cucumber.
- The API base URL is configured using a JVM system property: `-DbaseUrl`.
- You can filter which scenarios run using Cucumber **tags**.

## Configuration

### Base URL

Provide the API base URL using the `baseUrl` system property:

- `-DbaseUrl=http://localhost:8080`
- `-DbaseUrl=https://your-env.example.com`

If your API is not running locally, ensure the provided URL is reachable and points to the Expense Tracker API root.

## Running tests

### Run all BDD tests

mvn test -DbaseUrl=http://localhost:8080

### Run with a specific environment URL

mvn test -DbaseUrl=https://staging.example.com

## Running tests by tags

Use `-Dcucumber.filter.tags` to include/exclude scenarios by tags.

### Run only smoke tests

mvn test -DbaseUrl=http://localhost:8080 -Dcucumber.filter.tags="@smoke"

### Run multiple tags (AND)

mvn test -DbaseUrl=http://localhost:8080 -Dcucumber.filter.tags="@smoke and @api"

### Run either tag (OR)

mvn test -DbaseUrl=http://localhost:8080 -Dcucumber.filter.tags="@smoke or @regression"

### Exclude a tag (NOT)

mvn test -DbaseUrl=http://localhost:8080 -Dcucumber.filter.tags="not @wip"

### Combine include + exclude

mvn test -DbaseUrl=http://localhost:8080 -Dcucumber.filter.tags="@regression and not @flaky"

## Reports

After execution, Cucumber reports are generated under:

- `target/cucumber-reports/`

If the project is configured to produce an HTML report, it is typically available at:

- `target/cucumber-reports/index.html`

(Exact filenames may vary depending on the configured Cucumber plugins.)

## Example commands (copy/paste)

mvn clean test -DbaseUrl=http://localhost:8080

mvn clean test -DbaseUrl=http://localhost:8080 -Dcucumber.filter.tags="@smoke"

mvn clean test -DbaseUrl=https://staging.example.com -Dcucumber.filter.tags="@regression and not @wip"