package com.gauravqa.expense.core;

import com.gauravqa.expense.config.TestConfig;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;

import java.util.HashMap;
import java.util.Map;

public class ApiContext {
  private static Playwright playwright;
  private static APIRequestContext request;

  public static APIRequestContext get() {
    if (request == null) {
      playwright = Playwright.create();

      Map<String, String> headers = new HashMap<>();
      headers.put("Accept", "application/json");
      headers.put("Content-Type", "application/json");

      APIRequest.NewContextOptions opts = new APIRequest.NewContextOptions()
          .setBaseURL(TestConfig.baseUrl())
          .setExtraHTTPHeaders(headers);

      request = playwright.request().newContext(opts);
    }
    return request;
  }

  public static void close() {
    if (request != null) {
      request.dispose();
      request = null;
    }
    if (playwright != null) {
      playwright.close();
      playwright = null;
    }
  }
}
