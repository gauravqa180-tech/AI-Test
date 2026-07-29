package com.ai.qa.config;

public final class TestConfig {
  private TestConfig() {}

  public static String baseUrl() {
    // Priority: JVM property -DbaseUrl > ENV BASE_URL > default
    String fromProp = System.getProperty("baseUrl");
    if (fromProp != null && !fromProp.isBlank()) return fromProp;

    String fromEnv = System.getenv("BASE_URL");
    if (fromEnv != null && !fromEnv.isBlank()) return fromEnv;

    return "http://localhost:8080";
  }
}
