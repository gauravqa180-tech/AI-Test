package com.ai.test.clients;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ReportApi {
  private final APIRequestContext request;

  public ReportApi(APIRequestContext request) {
    this.request = request;
  }

  public APIResponse getMonthlyTotal(String monthYYYYMM) {
    String encoded = URLEncoder.encode(monthYYYYMM, StandardCharsets.UTF_8);
    return request.get("/api/reports/monthly-total?month=" + encoded);
  }
}
