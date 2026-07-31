package com.gauravqa.expense.api;

import com.microsoft.playwright.options.RequestOptions;

public final class APIRequestContextOptionsFactory {
  private APIRequestContextOptionsFactory() {}

  public static RequestOptions jsonBody(String json) {
    return RequestOptions.create().setData(json);
  }
}
