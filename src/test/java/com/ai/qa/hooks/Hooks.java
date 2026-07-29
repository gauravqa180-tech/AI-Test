package com.ai.qa.hooks;

import com.ai.qa.core.PlaywrightApiFactory;
import com.ai.qa.core.ScenarioContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;

public class Hooks {
  private static final ThreadLocal<PlaywrightApiFactory> API_FACTORY = new ThreadLocal<>();
  private static final ThreadLocal<ScenarioContext> CTX = new ThreadLocal<>();

  @Before
  public void beforeScenario() {
    API_FACTORY.set(new PlaywrightApiFactory());
    CTX.set(new ScenarioContext());
  }

  @After
  public void afterScenario() {
    PlaywrightApiFactory f = API_FACTORY.get();
    if (f != null) f.close();
    API_FACTORY.remove();
    CTX.remove();
  }

  public static PlaywrightApiFactory api() {
    return API_FACTORY.get();
  }

  public static ScenarioContext ctx() {
    return CTX.get();
  }
}
