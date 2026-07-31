package com.gauravqa.expense.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class TestConfig {
  private static final Properties PROPS = new Properties();

  static {
    try (InputStream is = TestConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
      if (is != null) {
        PROPS.load(is);
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to load config.properties", e);
    }
  }

  private TestConfig() {}

  public static String baseUrl() {
    String sys = System.getProperty("baseUrl");
    if (sys != null && !sys.isBlank()) return sys;
    return PROPS.getProperty("baseUrl", "http://localhost:8080");
  }
}
