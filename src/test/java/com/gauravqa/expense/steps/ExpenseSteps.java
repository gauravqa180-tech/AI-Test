package com.gauravqa.expense.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.gauravqa.expense.api.CategoryApi;
import com.gauravqa.expense.api.ExpenseApi;
import com.gauravqa.expense.core.JsonUtils;
import com.gauravqa.expense.hooks.Hooks;
import com.microsoft.playwright.APIResponse;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ExpenseSteps {

  private final ExpenseApi expenseApi = new ExpenseApi();
  private final CategoryApi categoryApi = new CategoryApi();

  @When("I create an expense with amount {double} date {string} category {string} note {string}")
  public void createExpense(double amount, String date, String categoryName, String note) {
    long categoryId = ensureCategory(categoryName);
    APIResponse resp = expenseApi.create(BigDecimal.valueOf(amount), date, categoryId, note);
    Hooks.ctx().put("lastResponse", resp);

    if (resp.status() == 201) {
      JsonNode json = JsonUtils.toJsonNode(resp.text());
      Hooks.ctx().put("expenseId", json.get("id").asLong());
      Hooks.ctx().put("expenseAmount", json.get("amount").asText());
      Hooks.ctx().put("expenseDate", json.get("date").asText());
      Hooks.ctx().put("expenseNote", json.get("note").asText());
      Hooks.ctx().put("expenseCategoryId", json.get("categoryId").asLong());
    }
  }

  @Given("I have created an expense with amount {double} date {string} category {string} note {string}")
  public void givenExpenseCreated(double amount, String date, String categoryName, String note) {
    createExpense(amount, date, categoryName, note);
    APIResponse resp = Hooks.ctx().get("lastResponse");
    Assertions.assertEquals(201, resp.status(), "Expense should be created in precondition");
  }

  @When("I update the expense to amount {double} date {string} category {string} note {string}")
  public void updateExpense(double amount, String date, String categoryName, String note) {
    Long expenseId = Hooks.ctx().get("expenseId");
    Assertions.assertNotNull(expenseId, "expenseId is required");
    long categoryId = ensureCategory(categoryName);

    APIResponse resp = expenseApi.update(expenseId, BigDecimal.valueOf(amount), date, categoryId, note);
    Hooks.ctx().put("lastResponse", resp);

    if (resp.status() == 200) {
      JsonNode json = JsonUtils.toJsonNode(resp.text());
      Hooks.ctx().put("expenseAmount", json.get("amount").asText());
      Hooks.ctx().put("expenseDate", json.get("date").asText());
      Hooks.ctx().put("expenseNote", json.get("note").asText());
      Hooks.ctx().put("expenseCategoryId", json.get("categoryId").asLong());
    }
  }

  @When("I update the expense to amount {double} date {string} categoryId {long} note {string}")
  public void updateExpenseWithCategoryId(double amount, String date, long categoryId, String note) {
    Long expenseId = Hooks.ctx().get("expenseId");
    Assertions.assertNotNull(expenseId, "expenseId is required");

    APIResponse resp = expenseApi.update(expenseId, BigDecimal.valueOf(amount), date, categoryId, note);
    Hooks.ctx().put("lastResponse", resp);
  }

  @When("I delete the expense")
  public void deleteExpense() {
    Long expenseId = Hooks.ctx().get("expenseId");
    Assertions.assertNotNull(expenseId, "expenseId is required");
    APIResponse resp = expenseApi.delete(expenseId);
    Hooks.ctx().put("lastResponse", resp);
  }

  @When("I undo delete for the expense")
  public void undoDeleteExpense() {
    Long expenseId = Hooks.ctx().get("expenseId");
    Assertions.assertNotNull(expenseId, "expenseId is required");
    APIResponse resp = expenseApi.undoDelete(expenseId);
    Hooks.ctx().put("lastResponse", resp);
  }

  @When("I list expenses for month {string}")
  public void listExpensesForMonth(String month) {
    Map<String, String> qp = new HashMap<>();
    qp.put("month", month);
    APIResponse resp = expenseApi.list(qp);
    Hooks.ctx().put("lastResponse", resp);
    if (resp.status() == 200) {
      Hooks.ctx().put("expensesJson", JsonUtils.toJsonNode(resp.text()));
    }
  }

  @When("I fetch monthly total for {string}")
  public void fetchMonthlyTotal(String month) {
    APIResponse resp = expenseApi.monthlyTotal(month);
    Hooks.ctx().put("lastResponse", resp);
    if (resp.status() == 200) {
      Hooks.ctx().put("monthlyTotalJson", JsonUtils.toJsonNode(resp.text()));
    }
  }

  @Then("the monthly total should be greater than {double}")
  public void monthlyTotalShouldBeGreaterThan(double min) {
    APIResponse resp = Hooks.ctx().get("lastResponse");
    Assertions.assertNotNull(resp);
    Assertions.assertEquals(200, resp.status(), "Expected 200 for monthly total");

    JsonNode json = JsonUtils.toJsonNode(resp.text());
    // Try common shapes: {"month":"2026-07","total":123.45} or {"total":123.45}
    JsonNode totalNode = json.get("total");
    if (totalNode == null) totalNode = json.get("monthlyTotal");
    Assertions.assertNotNull(totalNode, "Monthly total response must contain 'total' (or 'monthlyTotal') but was: " + json);

    double total = totalNode.asDouble();
    Assertions.assertTrue(total > min, "Expected total > " + min + " but got " + total);
  }

  @And("the expense list should contain the created expense")
  public void expenseListShouldContainCreated() {
    assertExpenseListContainsById(true, Hooks.ctx().get("expenseId"));
  }

  @And("the expense list should contain the updated expense")
  public void expenseListShouldContainUpdated() {
    Long id = Hooks.ctx().get("expenseId");
    Assertions.assertNotNull(id);

    JsonNode list = Hooks.ctx().get("expensesJson");
    Assertions.assertNotNull(list, "expensesJson not found");

    JsonNode found = findExpenseById(list, id);
    Assertions.assertNotNull(found, "Expected to find expense id=" + id + " but list was: " + list);

    String expectedNote = Hooks.ctx().get("expenseNote");
    Assertions.assertEquals(expectedNote, found.get("note").asText(), "Updated note should match");
  }

  @Then("the expense list should not contain the deleted expense")
  public void expenseListShouldNotContainDeleted() {
    assertExpenseListContainsById(false, Hooks.ctx().get("expenseId"));
  }

  @Then("the expense list should contain the restored expense")
  public void expenseListShouldContainRestored() {
    assertExpenseListContainsById(true, Hooks.ctx().get("expenseId"));
  }

  @Then("the expense list should contain an expense with note {string} and category {string}")
  public void expenseListShouldContainExpenseWithNoteAndCategory(String note, String categoryName) {
    JsonNode list = Hooks.ctx().get("expensesJson");
    Assertions.assertNotNull(list, "expensesJson not found");

    Long categoryId = findCategoryIdByName(categoryName);
    Assertions.assertNotNull(categoryId, "Category not found: " + categoryName);

    boolean found = false;
    for (JsonNode e : list) {
      String n = e.hasNonNull("note") ? e.get("note").asText() : "";
      long cid = e.hasNonNull("categoryId") ? e.get("categoryId").asLong() : -1;
      if (n.equals(note) && cid == categoryId) {
        found = true;
        break;
      }
    }
    Assertions.assertTrue(found, "Expected an expense with note='" + note + "' and category='" + categoryName + "' in list: " + list);
  }

  private void assertExpenseListContainsById(boolean expectedPresent, Long id) {
    Assertions.assertNotNull(id, "expenseId is required");
    JsonNode list = Hooks.ctx().get("expensesJson");
    Assertions.assertNotNull(list, "expensesJson not found");

    JsonNode found = findExpenseById(list, id);
    if (expectedPresent) {
      Assertions.assertNotNull(found, "Expected expense id=" + id + " to be present in list but it was not");
    } else {
      Assertions.assertNull(found, "Expected expense id=" + id + " to be absent in list but it was present");
    }
  }

  private JsonNode findExpenseById(JsonNode list, long id) {
    for (JsonNode e : list) {
      if (e.hasNonNull("id") && e.get("id").asLong() == id) {
        return e;
      }
    }
    return null;
  }

  private long ensureCategory(String categoryName) {
    Long id = findCategoryIdByName(categoryName);
    if (id != null) return id;

    APIResponse create = categoryApi.create(categoryName);
    if (create.status() == 201) {
      return JsonUtils.toJsonNode(create.text()).get("id").asLong();
    }

    // if already exists or race condition, fetch again
    id = findCategoryIdByName(categoryName);
    Assertions.assertNotNull(id, "Unable to resolve category id for: " + categoryName + " status=" + create.status());
    return id;
  }

  private Long findCategoryIdByName(String name) {
    APIResponse resp = categoryApi.list();
    if (resp.status() != 200) return null;

    JsonNode json = JsonUtils.toJsonNode(resp.text());
    for (JsonNode n : json) {
      String currentName = n.get("name").asText();
      if (currentName != null && currentName.toLowerCase(Locale.ROOT).equals(name.toLowerCase(Locale.ROOT))) {
        return n.get("id").asLong();
      }
    }
    return null;
  }
}
