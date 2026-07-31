package com.ai.test.steps;

import com.ai.test.clients.ApiClientFactory;
import com.ai.test.clients.ReportApi;
import com.microsoft.playwright.APIResponse;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class ReportSteps {

  private final ReportApi reportApi = new ReportApi(ApiClientFactory.get());

  // Shared with ExpenseSteps via Cucumber object factory is not set up.
  // We keep a local lastResponse for report-specific scenarios.
  private APIResponse lastResponse;

  @When("I request monthly total for month {string}")
  public void request_monthly_total(String month) {
    lastResponse = reportApi.getMonthlyTotal(month);
  }

  @Then("the monthly total should be {double}")
  public void monthly_total_should_be(double expected) {
    assertThat(lastResponse).isNotNull();
    assertThat(lastResponse.status()).isEqualTo(200);

    String body = lastResponse.text();
    // Backend might return a raw number or a JSON object. Handle both.
    BigDecimal total;
    String trimmed = body.trim();
    if (trimmed.startsWith("{")) {
      // Try to read total field in case it returns an object later.
      int idx = trimmed.indexOf("\"total\"");
      assertThat(idx).isGreaterThan(0);
      String after = trimmed.substring(idx);
      // naive parse: "total": 25.50
      String num = after.replaceAll(".*\\"total\\"\\s*:\\s*([0-9.]+).*", "$1");
      total = new BigDecimal(num);
    } else {
      total = new BigDecimal(trimmed);
    }

    assertThat(total).isEqualByComparingTo(BigDecimal.valueOf(expected).setScale(2, BigDecimal.ROUND_HALF_UP));
  }
}
