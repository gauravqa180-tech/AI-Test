@api @smoke
Feature: Expense CRUD smoke
  Basic checks to ensure baseline v1 endpoints are working end-to-end

  Background:
    Given the Expense Tracker API is running

  Scenario: Create -> Get by id -> Delete an expense
    When I create an expense with amount 25.00, date "2026-07-20", category "Groceries", note "Milk"
    Then the response status should be 201
    And I store the created expense id

    When I fetch the stored expense by id
    Then the response status should be 200
    And the expense should have category "Groceries"

    When I delete the stored expense
    Then the response status should be 204

    When I fetch the stored expense by id
    Then the response status should be 404
