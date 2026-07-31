@api
Feature: Expense API - Edit and safe delete with undo (US1)
  As a single user
  I want to update expenses and safely delete them with ability to restore
  So that I can correct mistakes without losing data

  Background:
    Given the Expense Tracker API is running

  @smoke @regression
  Scenario: Create expense then update it successfully
    When I create an expense with amount 10.50 date "2026-01-15" category "DINING" and note "coffee"
    Then the response status should be 201
    And the response should contain an expense id
    When I update the expense amount to 12.00 date "2026-01-16" category "DINING" and note "coffee and bagel"
    Then the response status should be 200
    And the expense in response should have amount 12.00 and date "2026-01-16" and category "DINING" and note "coffee and bagel"

  @smoke @regression
  Scenario: Soft delete removes expense from listing and restore brings it back
    When I create an expense with amount 25.00 date "2026-02-01" category "GROCERIES" and note "weekly"
    Then the response status should be 201
    And the response should contain an expense id
    When I delete the expense
    Then the response status should be 204
    When I list expenses
    Then the response status should be 200
    And the expenses list should not contain the created expense
    When I restore the expense
    Then the response status should be 200
    And the response should contain an expense id
    When I list expenses
    Then the response status should be 200
    And the expenses list should contain the created expense

  @regression @negative
  Scenario: Update fails with validation error when amount is missing
    When I create an expense with amount 9.99 date "2026-03-10" category "OTHER" and note "misc"
    Then the response status should be 201
    And the response should contain an expense id
    When I attempt to update the expense with invalid payload:
      | date     | 2026-03-11 |
      | category | OTHER      |
      | note     | updated    |
    Then the response status should be 400
    And the validation errors should contain field "amount"

  @regression @negative
  Scenario: Delete non-existent expense returns 404
    When I delete expense id 999999
    Then the response status should be 404

  @regression @negative
  Scenario: Restore non-existent expense returns 404
    When I restore expense id 999999
    Then the response status should be 404
