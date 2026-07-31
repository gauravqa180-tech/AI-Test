package com.kan.qa.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kan.qa.api.ExpenseApiClient;
import com.kan.qa.core.PlaywrightApiFactory;
import com.kan.qa.core.TestContext;
import com.microsoft.playwright.APIResponse;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ExpenseSteps {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final TestContext ctx = new TestContext();
    private final ExpenseApiClient api = new ExpenseApiClient(PlaywrightApiFactory.getRequest());

    @Given("the Expense Tracker API is running")
    public void theApiIsRunning() {
        // Lightweight health check by calling list endpoint.
        APIResponse res = api.listExpenses();
        res.dispose();
        assertThat(res.status()).isIn(200, 204);
    }

    @When("I create an expense with amount {double} date {string} category {string} and note {string}")
    public void iCreateAnExpense(double amount, String date, String category, String note) {
        String body = String.format("{\"amount\":%.2f,\"date\":\"%s\",\"category\":\"%s\",\"note\":\"%s\"}", amount, date, category, escape(note));
        APIResponse res = api.createExpense(body);
        capture(res);
        if (ctx.getLastStatus() == 201 && ctx.getLastJson() != null && ctx.getLastJson().hasNonNull("id")) {
            ctx.setExpenseId(ctx.getLastJson().get("id").asLong());
        }
    }

    @When("I update the expense amount to {double} date {string} category {string} and note {string}")
    public void iUpdateTheExpense(double amount, String date, String category, String note) {
        assertThat(ctx.getExpenseId()).as("expenseId").isNotNull();
        String body = String.format("{\"amount\":%.2f,\"date\":\"%s\",\"category\":\"%s\",\"note\":\"%s\"}", amount, date, category, escape(note));
        APIResponse res = api.updateExpense(ctx.getExpenseId(), body);
        capture(res);
    }

    @When("I attempt to update the expense with invalid payload:")
    public void iAttemptToUpdateWithInvalidPayload(DataTable table) {
        assertThat(ctx.getExpenseId()).as("expenseId").isNotNull();

        Map<String, String> map = new HashMap<>();
        for (List<String> row : table.asLists()) {
            map.put(row.get(0), row.get(1));
        }

        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, String> e : map.entrySet()) {
            if (!first) sb.append(",");
            first = false;
            sb.append("\"").append(e.getKey()).append("\":");
            sb.append("\"").append(escape(e.getValue())).append("\"");
        }
        sb.append("}");

        APIResponse res = api.updateExpense(ctx.getExpenseId(), sb.toString());
        capture(res);
    }

    @When("I delete the expense")
    public void iDeleteTheExpense() {
        assertThat(ctx.getExpenseId()).as("expenseId").isNotNull();
        APIResponse res = api.deleteExpense(ctx.getExpenseId());
        capture(res);
    }

    @When("I delete expense id {long}")
    public void iDeleteExpenseId(long id) {
        APIResponse res = api.deleteExpense(id);
        capture(res);
    }

    @When("I restore the expense")
    public void iRestoreTheExpense() {
        assertThat(ctx.getExpenseId()).as("expenseId").isNotNull();
        APIResponse res = api.restoreExpense(ctx.getExpenseId());
        capture(res);
    }

    @When("I restore expense id {long}")
    public void iRestoreExpenseId(long id) {
        APIResponse res = api.restoreExpense(id);
        capture(res);
    }

    @When("I list expenses")
    public void iListExpenses() {
        APIResponse res = api.listExpenses();
        capture(res);
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int status) {
        assertThat(ctx.getLastStatus()).isEqualTo(status);
    }

    @Then("the response should contain an expense id")
    public void responseShouldContainId() {
        assertThat(ctx.getLastJson()).isNotNull();
        assertThat(ctx.getLastJson().hasNonNull("id")).isTrue();
        assertThat(ctx.getLastJson().get("id").asLong()).isPositive();
    }

    @Then("the expense in response should have amount {double} and date {string} and category {string} and note {string}")
    public void expenseInResponseShouldMatch(double amount, String date, String category, String note) {
        JsonNode json = ctx.getLastJson();
        assertThat(json).isNotNull();

        BigDecimal actualAmount = json.get("amount").decimalValue();
        assertThat(actualAmount).isEqualByComparingTo(BigDecimal.valueOf(amount).setScale(2));
        assertThat(json.get("date").asText()).isEqualTo(date);
        assertThat(json.get("category").asText()).isEqualTo(category);
        if (note == null) {
            assertThat(json.get("note").isNull()).isTrue();
        } else {
            assertThat(json.get("note").asText()).isEqualTo(note);
        }
    }

    @Then("the expenses list should not contain the created expense")
    public void listShouldNotContainCreatedExpense() {
        assertThat(ctx.getLastJson()).isNotNull();
        assertThat(ctx.getLastJson().isArray()).isTrue();
        long id = ctx.getExpenseId();
        boolean found = false;
        for (JsonNode n : ctx.getLastJson()) {
            if (n.hasNonNull("id") && n.get("id").asLong() == id) {
                found = true;
                break;
            }
        }
        assertThat(found).isFalse();
    }

    @Then("the expenses list should contain the created expense")
    public void listShouldContainCreatedExpense() {
        assertThat(ctx.getLastJson()).isNotNull();
        assertThat(ctx.getLastJson().isArray()).isTrue();
        long id = ctx.getExpenseId();
        boolean found = false;
        for (JsonNode n : ctx.getLastJson()) {
            if (n.hasNonNull("id") && n.get("id").asLong() == id) {
                found = true;
                break;
            }
        }
        assertThat(found).isTrue();
    }

    @Then("the validation errors should contain field {string}")
    public void validationErrorsShouldContainField(String field) {
        JsonNode json = ctx.getLastJson();
        assertThat(json).isNotNull();
        assertThat(json.has("errors")).isTrue();
        assertThat(json.get("errors").has(field)).isTrue();
    }

    private void capture(APIResponse res) {
        ctx.setLastStatus(res.status());
        String body = null;
        try {
            body = res.text();
        } catch (Exception ignored) {
            // 204 has no body
        }
        ctx.setLastResponseBody(body);

        if (body != null && !body.isBlank()) {
            try {
                ctx.setLastJson(MAPPER.readTree(body));
            } catch (IOException e) {
                ctx.setLastJson(null);
            }
        } else {
            ctx.setLastJson(null);
        }
        res.dispose();
    }

    private String escape(String value) {
        if (value == null) return null;
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
