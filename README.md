# Codemie Navigation — Playwright (Java) + JUnit 5

This project contains a Playwright Java + JUnit 5 UI test that navigates to Codemie and captures a screenshot.

## Prerequisites

- Java 17+ (recommended)
- Maven 3.8+

## Install Playwright browsers (Chromium)

This project installs Playwright and downloads the Chromium browser via the Playwright CLI using `exec-maven-plugin`.

Run:

mvn -q exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install chromium"

## Run the test

Run:

mvn test

## Outputs

- Screenshot is saved to:
  target/artifacts/codemie-home.png
- Console logs are printed to the test output in your terminal during `mvn test`.