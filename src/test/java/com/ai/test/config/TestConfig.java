package com.ai.test.config;

public final class TestConfig {
  private TestConfig() {}

  public static String baseUrl() {
    String fromSysProp = System.getProperty("api.baseUrl");
    if (fromSysProp != null && !fromSysProp.isBlank()) {
      return stripTrailingSlash(fromSysProp);
    }

    String fromEnv = System.getenv("API_BASE_URL");
    if (fromEnv != null && !fromEnv.isBlank()) {
      return stripTrailingSlash(fromEnv);
    }

    // Default for local dev
    return "http://localhost:8080";
  }

  private static String stripTrailingSlash(String url) {
    if (url.endsWith("/")) return url.substring(0, url.length() - 1);
    return url;
  }
}
