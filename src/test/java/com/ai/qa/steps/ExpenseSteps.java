package com.ai.qa.steps;

import com.ai.qa.api.ExpenseApi;
import com.ai.qa.hooks.Hooks;
import com.ai.qa.models.CategoryDto;
import com.ai.qa.models.ExpenseDto;
import com.ai.qa.utils.JsonUtils;
import com.microsoft.playwright.APIResponse;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ExpenseSteps {

  private ExpenseApi api() {
    return new ExpenseApi(Hooks.api().request());
  }

  @When("I create an expense with amount {double} on date {string} in category {string} and note {string}")
  public void createExpense(double amount, String date, String categoryName, String note) {
    APIResponse resp = api().create(BigDecimal.valueOf(amount), date, null, categoryName, note);
    Hooks.ctx().put("lastResponse", resp);
    if (resp.status() == 201 || resp.status() == 200) {
      ExpenseDto dto = JsonUtils.fromJson(resp.text(), ExpenseDto.class);
      Hooks.ctx().put("expense", dto);
    }
  }

  @Then("the expense is created successfully")
  public void expenseCreatedSuccessfully() {
    APIResponse resp = Hooks.ctx().get("lastResponse", APIResponse.class);
    Assertions.assertThat(resp.status()).isIn(200, 201);
    ExpenseDto dto = Hooks.ctx().get("expense", ExpenseDto.class);
    Assertions.assertThat(dto).isNotNull();
    Assertions.assertThat(dto.id).isNotNull();
  }

  @Given("an expense exists with amount {double} on date {string} in category {string} and note {string}")
  public void expenseExists(double amount, String date, String categoryName, String note) {
    APIResponse resp = api().create(BigDecimal.valueOf(amount), date, null, categoryName, note);
    Hooks.ctx().put("lastResponse", resp);
    Assertions.assertThat(resp.status()).isIn(200, 201);
    ExpenseDto dto = JsonUtils.fromJson(resp.text(), ExpenseDto.class);
    Hooks.ctx().put("expense", dto);
    Hooks.ctx().put("expenseId", dto.id);
    // store category if returned
    if (dto.categoryId != null) {
      CategoryDto c = new CategoryDto();
      c.id = dto.categoryId;
      c.name = dto.categoryName;
      Hooks.ctx().put("category:" + categoryName, c);
    }
  }

  @When("I update the expense amount to {double} and note to {string}")
  public void updateExpense(double amount, String note) {
    Long id = Hooks.ctx().get("expenseId", Long.class);
    if (id == null) {
      ExpenseDto dto = Hooks.ctx().get("expense", ExpenseDto.class);
      Assertions.assertThat(dto).isNotNull();
      id = dto.id;
    }

    APIResponse resp = api().update(id, BigDecimal.valueOf(amount), null, null, null, note);
    Hooks.ctx().put("lastResponse", resp);
    if (resp.status() == 200) {
      ExpenseDto updated = JsonUtils.fromJson(resp.text(), ExpenseDto.class);
      Hooks.ctx().put("expense", updated);
    }
  }

  @Then("the expense is updated successfully")
  public void expenseUpdatedSuccessfully() {
    APIResponse resp = Hooks.ctx().get("lastResponse", APIResponse.class);
    Assertions.assertThat(resp.status()).isEqualTo(200);
  }

  @When("I delete the expense")
  public void deleteExpense() {
    ExpenseDto dto = Hooks.ctx().get("expense", ExpenseDto.class);
    Assertions.assertThat(dto).isNotNull();
    APIResponse resp = api().delete(dto.id);
    Hooks.ctx().put("lastResponse", resp);
  }

  @When("I search expenses with query {string}")
  public void searchExpenses(String q) {
    Map<String, String> params = new HashMap<>();
    params.put("q", q);
    APIResponse resp = api().list(params);
    Hooks.ctx().put("lastResponse", resp);
    Hooks.ctx().put("lastExpensesJson", resp.text());
  }

  @Then("the expenses list contains the created expense")
  public void listContainsCreatedExpense() {
    APIResponse resp = Hooks.ctx().get("lastResponse", APIResponse.class);
    Assertions.assertThat(resp.status()).isEqualTo(200);

    ExpenseDto created = Hooks.ctx().get("expense", ExpenseDto.class);
    Assertions.assertThat(created).isNotNull();

    ExpenseDto[] list = JsonUtils.fromJson(resp.text(), ExpenseDto[].class);
    Assertions.assertThat(list).anyMatch(e -> e.id != null && e.id.equals(created.id));
  }

  @Then("the expenses list should not contain the created expense")
  public void listNotContainCreatedExpense() {
    APIResponse resp = Hooks.ctx().get("lastResponse", APIResponse.class);
    Assertions.assertThat(resp.status()).isEqualTo(200);

    ExpenseDto created = Hooks.ctx().get("expense", ExpenseDto.class);
    Assertions.assertThat(created).isNotNull();

    ExpenseDto[] list = JsonUtils.fromJson(resp.text(), ExpenseDto[].class);
    Assertions.assertThat(list).noneMatch(e -> e.id != null && e.id.equals(created.id));
  }

  @Then("the error response should contain field {string}")
  public void errorResponseShouldContainField(String field) {
    APIResponse resp = Hooks.ctx().get("lastResponse", APIResponse.class);
    Assertions.assertThat(resp.status()).isEqualTo(400);

    // Be lenient: check raw body contains the field name.
    String body = resp.text();
    Assertions.assertThat(body).contains(field);
  }
}
