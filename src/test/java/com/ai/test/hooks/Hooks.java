package com.ai.test.hooks;

import com.ai.test.clients.ApiClientFactory;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;

public class Hooks {

  @Before
  public void beforeScenario() {
    // Ensure request context is created
    ApiClientFactory.get();
  }

  @AfterAll
  public static void afterAll() {
    ApiClientFactory.close();
  }
}
