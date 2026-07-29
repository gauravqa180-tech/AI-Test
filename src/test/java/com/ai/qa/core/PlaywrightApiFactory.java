package com.ai.qa.core;

import com.ai.qa.config.TestConfig;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;

import java.util.HashMap;
import java.util.Map;

public class PlaywrightApiFactory {
  private final Playwright playwright;
  private final APIRequestContext request;

  public PlaywrightApiFactory() {
    this.playwright = Playwright.create();
    Map<String, String> headers = new HashMap<>();
    headers.put("Accept", "application/json");
    headers.put("Content-Type", "application/json");

    this.request = playwright.request().newContext(new APIRequest.NewContextOptions()
        .setBaseURL(TestConfig.baseUrl())
        .setExtraHTTPHeaders(headers));
  }

  public APIRequestContext request() {
    return request;
  }

  public void close() {
    try {
      if (request != null) request.dispose();
    } finally {
      if (playwright != null) playwright.close();
    }
  }
}
