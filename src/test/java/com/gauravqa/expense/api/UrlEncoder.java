package com.gauravqa.expense.api;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class UrlEncoder {
  private UrlEncoder() {}

  public static String encode(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }
}
