@api
Feature: Categories API
  As a user
  I want to manage categories
  So that I can classify expenses consistently

  Background:
    Given the ExpenseTracker API is available

  @smoke @regression
  Scenario: Create and list categories
    When I create a category named "Utilities"
    Then the category is created successfully
    When I list categories
    Then the categories list contains "Utilities"

  @regression
  Scenario: Merge categories
    Given a category named "Taxi" exists
    And a category named "Transport" exists
    And an expense exists with amount 15.00 on date "2026-07-05" in category "Taxi" and note "Airport"
    When I merge category "Taxi" into "Transport"
    Then the merge is successful
    When I list categories
    Then the categories list should not contain "Taxi"

  @negative @regression
  Scenario: Delete a category in use without reassignment should fail
    Given a category named "Bills" exists
    And an expense exists with amount 30.00 on date "2026-07-06" in category "Bills" and note "Internet"
    When I delete category "Bills" without reassignment
    Then the API should respond with status 400
