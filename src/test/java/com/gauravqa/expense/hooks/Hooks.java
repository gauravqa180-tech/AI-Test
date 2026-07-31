package com.gauravqa.expense.hooks;

import com.gauravqa.expense.core.ApiContext;
import com.gauravqa.expense.core.ScenarioContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class Hooks {
  public static final ThreadLocal<ScenarioContext> CTX = new ThreadLocal<>();

  @Before
  public void before(Scenario scenario) {
    CTX.set(new ScenarioContext());
  }

  @After
  public void after() {
    CTX.remove();
    // Keep API context open across scenarios for speed. If needed, uncomment next line.
    // ApiContext.close();
  }

  public static ScenarioContext ctx() {
    return CTX.get();
  }
}
