@api
Feature: Monthly totals and breakdown
  As a user
  I want to see monthly total and category breakdown
  So that I understand my spending

  Background:
    Given the ExpenseTracker API is available

  @smoke @regression
  Scenario: Monthly total reflects created expenses
    Given an expense exists with amount 10.00 on date "2026-07-10" in category "Food" and note "Lunch"
    And an expense exists with amount 5.00 on date "2026-07-11" in category "Food" and note "Coffee"
    When I request monthly total for month "2026-07"
    Then the API should respond with status 200
    And the monthly total should be 15.00

  @regression
  Scenario: Monthly breakdown sums to overall total
    Given an expense exists with amount 40.00 on date "2026-07-12" in category "Groceries" and note "Weekly"
    And an expense exists with amount 60.00 on date "2026-07-13" in category "Transport" and note "Bus pass"
    When I request monthly breakdown for month "2026-07"
    Then the API should respond with status 200
    And the monthly breakdown overall total should be 100.00
