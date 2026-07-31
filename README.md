# Expense Tracker API Automation (Playwright + Cucumber + Java)

API automation framework for the Expense Tracker service using **Playwright (APIRequest)**, **Cucumber (BDD)**, and **Java**.

---

## Prerequisites

- **Java 17+**
- **Maven 3.9+**
- (Optional) **IntelliJ IDEA / VS Code**
- Network access to the Expense Tracker API environment you want to test

---

## Base URL Configuration

The framework uses a default `baseUrl` for API requests. You can override it at runtime using a JVM system property:

- **`-DbaseUrl=<YOUR_API_BASE_URL>`**

Example:

mvn test -DbaseUrl=https://your-env.example.com

---

## Running Tests

### Run All Tests

mvn test

### Run Smoke Tests

mvn test -Dcucumber.filter.tags="@smoke"

### Run Regression Tests

mvn test -Dcucumber.filter.tags="@regression"

### Run API-Tagged Tests

mvn test -Dcucumber.filter.tags="@api"

---

## Reports

After execution, the Cucumber HTML report is generated at:

target/cucumber-reports/cucumber.html