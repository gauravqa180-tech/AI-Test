@api @regression
Feature: Edit Expense (PUT /api/expenses/{id})
  As a user
  I want to update an existing expense
  So that I can correct mistakes without deleting it

  Background:
    Given the Expense Tracker API is running

  @smoke
  Scenario: Update an existing expense successfully
    Given an expense exists with amount 12.50, date "2026-07-01", category "Food", note "Lunch"
    When I update that expense to amount 18.75, date "2026-07-02", category "Food", note "Lunch with dessert"
    Then the response status should be 200
    And the expense id should be the same
    And the expense should have amount 18.75, date "2026-07-02", category "Food", note "Lunch with dessert"

  @negative
  Scenario Outline: Validation errors when updating an expense
    Given an expense exists with amount 10.00, date "2026-07-10", category "Travel", note "Bus"
    When I update that expense with invalid payload:
      | amount   | date   | category   | note   |
      | <amount> | <date> | <category> | <note> |
    Then the response status should be 400
    And the error response should contain message "Validation"

    Examples:
      | amount | date       | category | note |
      | -1     | 2026-07-11 | Travel   | Bus  |
      | 0      | 2026-07-11 | Travel   | Bus  |
      | 10     |            | Travel   | Bus  |
      | 10     | 2026-07-11 |          | Bus  |

  @negative
  Scenario: Update a non-existing expense returns 404
    When I update expense id 9999999 to amount 11.00, date "2026-07-03", category "Food", note "Not found"
    Then the response status should be 404

  @regression
  Scenario: Update does not create a new expense (list count unchanged)
    Given an expense exists with amount 5.00, date "2026-07-15", category "Coffee", note "Latte"
    And I note the current expense count
    When I update that expense to amount 6.00, date "2026-07-15", category "Coffee", note "Latte grande"
    Then the response status should be 200
    And the expense count should be unchanged
