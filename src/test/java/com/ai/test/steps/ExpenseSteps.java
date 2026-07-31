package com.ai.test.steps;

import com.ai.test.clients.ApiClientFactory;
import com.ai.test.clients.ExpenseApi;
import com.ai.test.model.ApiError;
import com.ai.test.model.ExpenseRequest;
import com.ai.test.model.ExpenseResponse;
import com.ai.test.utils.JsonUtils;
import com.microsoft.playwright.APIResponse;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ExpenseSteps {

  private final ExpenseApi expenseApi = new ExpenseApi(ApiClientFactory.get());

  private APIResponse lastResponse;
  private long storedExpenseId;
  private ExpenseResponse lastExpense;
  private int storedCount;

  @Given("the Expense Tracker API is running")
  public void the_api_is_running() {
    // Lightweight check: list endpoint should be reachable (200)
    APIResponse response = expenseApi.listExpenses();
    assertThat(response.status()).isEqualTo(200);
    response.dispose();
  }

  @When("I create an expense with amount {double}, date {string}, category {string}, note {string}")
  public void create_expense(double amount, String date, String category, String note) {
    ExpenseRequest req = new ExpenseRequest(bd(amount), date, category, note);
    lastResponse = expenseApi.createExpense(req);
  }

  @Then("I store the created expense id")
  public void store_created_id() {
    String body = lastResponse.text();
    ExpenseResponse created = JsonUtils.fromJson(body, ExpenseResponse.class);
    assertThat(created.id()).isNotNull();
    storedExpenseId = created.id();
    lastExpense = created;
  }

  @When("I fetch the stored expense by id")
  public void fetch_stored_expense() {
    lastResponse = expenseApi.getExpense(storedExpenseId);
    if (lastResponse.status() == 200) {
      lastExpense = JsonUtils.fromJson(lastResponse.text(), ExpenseResponse.class);
    }
  }

  @When("I delete the stored expense")
  public void delete_stored_expense() {
    lastResponse = expenseApi.deleteExpense(storedExpenseId);
  }

  @Given("an expense exists with amount {double}, date {string}, category {string}, note {string}")
  public void an_expense_exists(double amount, String date, String category, String note) {
    ExpenseRequest req = new ExpenseRequest(bd(amount), date, category, note);
    APIResponse response = expenseApi.createExpense(req);
    assertThat(response.status()).isEqualTo(201);
    ExpenseResponse created = JsonUtils.fromJson(response.text(), ExpenseResponse.class);
    assertThat(created.id()).isNotNull();
    storedExpenseId = created.id();
    lastExpense = created;
    response.dispose();
  }

  @When("I update that expense to amount {double}, date {string}, category {string}, note {string}")
  public void update_expense(double amount, String date, String category, String note) {
    ExpenseRequest req = new ExpenseRequest(bd(amount), date, category, note);
    lastResponse = expenseApi.updateExpense(storedExpenseId, req);
    if (lastResponse.status() == 200) {
      lastExpense = JsonUtils.fromJson(lastResponse.text(), ExpenseResponse.class);
    }
  }

  @When("I update expense id {long} to amount {double}, date {string}, category {string}, note {string}")
  public void update_non_existing(long id, double amount, String date, String category, String note) {
    ExpenseRequest req = new ExpenseRequest(bd(amount), date, category, note);
    lastResponse = expenseApi.updateExpense(id, req);
  }

  @When("I update that expense with invalid payload:")
  public void update_with_invalid_payload(DataTable table) {
    List<Map<String, String>> rows = table.asMaps(String.class, String.class);
    Map<String, String> row = rows.get(0);

    ExpenseRequest req = new ExpenseRequest(
        parseNullableBigDecimal(row.get("amount")),
        emptyToNull(row.get("date")),
        emptyToNull(row.get("category")),
        emptyToNull(row.get("note"))
    );

    lastResponse = expenseApi.updateExpense(storedExpenseId, req);
  }

  @Given("I note the current expense count")
  public void note_current_count() {
    APIResponse response = expenseApi.listExpenses();
    assertThat(response.status()).isEqualTo(200);
    // Response is expected to be a JSON array
    String body = response.text();
    // Simple count by parsing as list of maps
    List<?> list = JsonUtils.fromJson(body, List.class);
    storedCount = list.size();
    response.dispose();
  }

  @Then("the expense count should be unchanged")
  public void count_should_be_unchanged() {
    APIResponse response = expenseApi.listExpenses();
    assertThat(response.status()).isEqualTo(200);
    List<?> list = JsonUtils.fromJson(response.text(), List.class);
    assertThat(list.size()).isEqualTo(storedCount);
    response.dispose();
  }

  @Then("the response status should be {int}")
  public void response_status_should_be(int status) {
    assertThat(lastResponse).isNotNull();
    assertThat(lastResponse.status()).isEqualTo(status);
  }

  @Then("the expense id should be the same")
  public void expense_id_same() {
    assertThat(lastExpense).isNotNull();
    assertThat(lastExpense.id()).isEqualTo(storedExpenseId);
  }

  @Then("the expense should have amount {double}, date {string}, category {string}, note {string}")
  public void expense_should_have_all_fields(double amount, String date, String category, String note) {
    assertThat(lastExpense).isNotNull();
    assertThat(lastExpense.amount()).isEqualByComparingTo(bd(amount));
    assertThat(lastExpense.date()).isEqualTo(date);
    assertThat(lastExpense.category()).isEqualTo(category);
    assertThat(lastExpense.note()).isEqualTo(note);
  }

  @Then("the expense should have category {string}")
  public void expense_should_have_category(String category) {
    assertThat(lastExpense).isNotNull();
    assertThat(lastExpense.category()).isEqualTo(category);
  }

  @Then("the error response should contain message {string}")
  public void error_response_should_contain_message(String msg) {
    String body = lastResponse.text();
    ApiError error = JsonUtils.fromJson(body, ApiError.class);
    assertThat(error.message()).isNotBlank();
    assertThat(error.message()).containsIgnoringCase(msg);
  }

  private static BigDecimal bd(double val) {
    return BigDecimal.valueOf(val).setScale(2, BigDecimal.ROUND_HALF_UP);
  }

  private static BigDecimal parseNullableBigDecimal(String raw) {
    String v = emptyToNull(raw);
    if (v == null) return null;
    return new BigDecimal(v);
  }

  private static String emptyToNull(String s) {
    if (s == null) return null;
    String t = s.trim();
    return t.isEmpty() ? null : t;
  }
}
