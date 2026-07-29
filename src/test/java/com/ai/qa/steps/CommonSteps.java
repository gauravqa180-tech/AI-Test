package com.ai.qa.steps;

import com.ai.qa.hooks.Hooks;
import com.microsoft.playwright.APIResponse;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.assertj.core.api.Assertions;

public class CommonSteps {

  @Given("the ExpenseTracker API is available")
  public void apiIsAvailable() {
    // Lightweight health check via listing categories (should be 200)
    APIResponse resp = Hooks.api().request().get("/api/categories");
    Assertions.assertThat(resp.status()).isBetween(200, 499);
  }

  @Then("the API should respond with status {int}")
  public void apiShouldRespondWithStatus(int status) {
    APIResponse resp = Hooks.ctx().get("lastResponse", APIResponse.class);
    Assertions.assertThat(resp).as("lastResponse should be present").isNotNull();
    Assertions.assertThat(resp.status()).isEqualTo(status);
  }
}
