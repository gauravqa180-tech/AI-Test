package com.gauravqa.expense.steps;

import com.gauravqa.expense.api.CategoryApi;
import com.gauravqa.expense.core.JsonUtils;
import com.gauravqa.expense.hooks.Hooks;
import com.microsoft.playwright.APIResponse;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.junit.jupiter.api.Assertions;

public class CommonSteps {

  @Given("the Expense Tracker API is available")
  public void apiIsAvailable() {
    // A lightweight health check: categories list should respond (even if empty)
    CategoryApi api = new CategoryApi();
    APIResponse resp = api.list();
    Assertions.assertTrue(resp.status() == 200 || resp.status() == 500,
        "Expected API to be reachable, got: " + resp.status() + " body=" + resp.text());
    // store last response for subsequent status assertions if needed
    Hooks.ctx().put("lastResponse", resp);
  }

  @Then("the response status should be {int}")
  public void responseStatusShouldBe(int expected) {
    APIResponse resp = Hooks.ctx().get("lastResponse");
    Assertions.assertNotNull(resp, "No lastResponse found in context");
    Assertions.assertEquals(expected, resp.status(), () -> {
      String body;
      try {
        body = resp.text();
      } catch (Exception e) {
        body = "<unreadable>";
      }
      return "Expected status " + expected + " but got " + resp.status() + ". Body=" + body;
    });
  }

  protected static String safeText(APIResponse resp) {
    try {
      return resp.text();
    } catch (Exception e) {
      return "";
    }
  }
}
