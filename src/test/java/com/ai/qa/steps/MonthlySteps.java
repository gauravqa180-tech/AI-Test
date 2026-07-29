package com.ai.qa.steps;

import com.ai.qa.api.ExpenseApi;
import com.ai.qa.hooks.Hooks;
import com.ai.qa.models.MonthlyBreakdownResponse;
import com.ai.qa.utils.JsonUtils;
import com.microsoft.playwright.APIResponse;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MonthlySteps {

  private ExpenseApi api() {
    return new ExpenseApi(Hooks.api().request());
  }

  @When("I request monthly total for month {string}")
  public void requestMonthlyTotal(String month) {
    APIResponse resp = api().monthlyTotal(month);
    Hooks.ctx().put("lastResponse", resp);
    Hooks.ctx().put("lastMonthlyTotalBody", resp.text());
  }

  @Then("the monthly total should be {double}")
  public void monthlyTotalShouldBe(double expected) {
    APIResponse resp = Hooks.ctx().get("lastResponse", APIResponse.class);
    Assertions.assertThat(resp.status()).isEqualTo(200);

    String body = resp.text();

    // Handle either: {"month":"yyyy-MM","total":15.00} OR plain number OR {"total":15.00}
    BigDecimal total;
    if (body.trim().startsWith("{")) {
      try {
        // attempt common DTO shapes
        var tree = new com.fasterxml.jackson.databind.ObjectMapper().readTree(body);
        if (tree.has("overallTotal")) total = new BigDecimal(tree.get("overallTotal").asText());
        else if (tree.has("total")) total = new BigDecimal(tree.get("total").asText());
        else total = new BigDecimal(tree.elements().next().asText());
      } catch (Exception e) {
        throw new RuntimeException("Unable to parse monthly total response: " + body, e);
      }
    } else {
      total = new BigDecimal(body.trim());
    }

    BigDecimal expectedScaled = BigDecimal.valueOf(expected).setScale(2, RoundingMode.HALF_UP);
    Assertions.assertThat(total).isEqualByComparingTo(expectedScaled);
  }

  @When("I request monthly breakdown for month {string}")
  public void requestMonthlyBreakdown(String month) {
    APIResponse resp = api().monthlyBreakdown(month);
    Hooks.ctx().put("lastResponse", resp);
    Hooks.ctx().put("lastMonthlyBreakdownBody", resp.text());
  }

  @Then("the monthly breakdown overall total should be {double}")
  public void monthlyBreakdownOverallTotalShouldBe(double expected) {
    APIResponse resp = Hooks.ctx().get("lastResponse", APIResponse.class);
    Assertions.assertThat(resp.status()).isEqualTo(200);

    MonthlyBreakdownResponse dto = JsonUtils.fromJson(resp.text(), MonthlyBreakdownResponse.class);
    Assertions.assertThat(dto.overallTotal).isNotNull();
    Assertions.assertThat(dto.overallTotal).isEqualByComparingTo(BigDecimal.valueOf(expected));
  }
}
