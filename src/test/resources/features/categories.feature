@api
Feature: Category management (create/rename/delete with reassign)
  As a single user
  I want to manage categories
  So that expenses remain categorized correctly

  Background:
    Given the Expense Tracker API is available

  @smoke @regression
  Scenario: Create a new category and verify it appears in list
    When I create a category named "Subscriptions"
    Then the response status should be 201
    When I list categories
    Then the response status should be 200
    And the category list should contain "Subscriptions"

  @regression
  Scenario: Rename a category
    Given I have a category named "Food"
    When I rename the category to "Dining"
    Then the response status should be 200
    When I list categories
    Then the category list should contain "Dining"

  @regression
  Scenario: Delete a category with reassignment
    Given I have a category named "Transport"
    And I have a category named "Other"
    And I have created an expense with amount 9.00 date "2026-07-20" category "Transport" note "Taxi"
    When I delete category "Transport" and reassign expenses to "Other"
    Then the response status should be 204
    When I list expenses for month "2026-07"
    Then the expense list should contain an expense with note "Taxi" and category "Other"

  @negative @regression
  Scenario: Deleting a category by reassigning to itself is rejected
    Given I have a category named "Health"
    When I delete category "Health" and reassign expenses to "Health"
    Then the response status should be 400
