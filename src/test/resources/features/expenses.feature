@api
Feature: Expense management (edit, soft delete/undo, list filters, monthly total)
  As a single user
  I want to manage my expenses via API
  So that I can track my monthly spending accurately

  Background:
    Given the Expense Tracker API is available
    And at least one category exists

  @smoke @regression
  Scenario: Create an expense and verify it appears in monthly list and total
    When I create an expense with amount 12.50 date "2026-07-15" category "Food" note "Lunch"
    Then the response status should be 201
    When I list expenses for month "2026-07"
    Then the response status should be 200
    And the expense list should contain the created expense
    When I fetch monthly total for "2026-07"
    Then the response status should be 200
    And the monthly total should be greater than 0

  @regression
  Scenario: Edit an expense updates the list and monthly total
    Given I have created an expense with amount 10.00 date "2026-07-10" category "Food" note "Coffee"
    When I update the expense to amount 20.00 date "2026-07-10" category "Food" note "Coffee + Snack"
    Then the response status should be 200
    When I list expenses for month "2026-07"
    Then the expense list should contain the updated expense

  @smoke @regression
  Scenario: Soft delete removes expense from list and undo restores it
    Given I have created an expense with amount 7.00 date "2026-07-11" category "Transport" note "Bus"
    When I delete the expense
    Then the response status should be 204
    When I list expenses for month "2026-07"
    Then the expense list should not contain the deleted expense
    When I undo delete for the expense
    Then the response status should be 200
    When I list expenses for month "2026-07"
    Then the expense list should contain the restored expense

  @negative @regression
  Scenario: Creating an expense with non-positive amount is rejected
    When I create an expense with amount 0.00 date "2026-07-15" category "Food" note "Invalid"
    Then the response status should be 400

  @negative @regression
  Scenario: Updating an expense with invalid categoryId is rejected
    Given I have created an expense with amount 5.00 date "2026-07-12" category "Food" note "Tea"
    When I update the expense to amount 5.00 date "2026-07-12" categoryId 999999 note "Tea"
    Then the response status should be 404
