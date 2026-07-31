@api @regression
Feature: Monthly total report (GET /api/reports/monthly-total)
  As a user
  I want to see the monthly total spending
  So that I can understand my spend in a month

  Background:
    Given the Expense Tracker API is running

  Scenario: Monthly total aggregates expenses for the given month
    Given an expense exists with amount 10.00, date "2026-07-01", category "Food", note "A"
    And an expense exists with amount 15.50, date "2026-07-02", category "Food", note "B"
    And an expense exists with amount 99.99, date "2026-08-01", category "Food", note "Next month"
    When I request monthly total for month "2026-07"
    Then the response status should be 200
    And the monthly total should be 25.50
