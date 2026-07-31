package com.ai.test.clients;

import com.ai.test.config.TestConfig;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;

import java.util.HashMap;
import java.util.Map;

public final class ApiClientFactory {
  private static Playwright playwright;
  private static APIRequestContext request;

  private ApiClientFactory() {}

  public static synchronized APIRequestContext get() {
    if (request != null) return request;

    playwright = Playwright.create();
    APIRequest apiRequest = playwright.request();

    Map<String, String> headers = new HashMap<>();
    headers.put("Accept", "application/json");
    headers.put("Content-Type", "application/json");

    request = apiRequest.newContext(
        new APIRequest.NewContextOptions()
            .setBaseURL(TestConfig.baseUrl())
            .setExtraHTTPHeaders(headers)
    );

    return request;
  }

  public static synchronized void close() {
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
