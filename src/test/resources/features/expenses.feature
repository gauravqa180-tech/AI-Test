@api
Feature: Expenses API
  As a user of ExpenseTracker
  I want to create, update, search and delete expenses
  So that I can manage my spending

  Background:
    Given the ExpenseTracker API is available

  @smoke @regression
  Scenario: Create an expense and verify it appears in list
    When I create an expense with amount 12.50 on date "2026-07-01" in category "Groceries" and note "Milk"
    Then the expense is created successfully
    When I search expenses with query "Milk"
    Then the expenses list contains the created expense

  @regression
  Scenario: Update an existing expense
    Given an expense exists with amount 20.00 on date "2026-07-02" in category "Transport" and note "Taxi"
    When I update the expense amount to 25.00 and note to "Taxi - updated"
    Then the expense is updated successfully
    When I search expenses with query "Taxi - updated"
    Then the expenses list contains the created expense

  @negative @regression
  Scenario: Validation fails when creating expense with invalid amount
    When I create an expense with amount -1.00 on date "2026-07-01" in category "Groceries" and note "Bad"
    Then the API should respond with status 400
    And the error response should contain field "amount"

  @regression
  Scenario: Delete an expense
    Given an expense exists with amount 9.99 on date "2026-07-03" in category "Food" and note "Snack"
    When I delete the expense
    Then the API should respond with status 204
    When I search expenses with query "Snack"
    Then the expenses list should not contain the created expense
