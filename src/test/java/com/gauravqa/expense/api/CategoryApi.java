package com.gauravqa.expense.api;

import com.gauravqa.expense.core.ApiContext;
import com.gauravqa.expense.core.JsonUtils;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;

import java.util.Map;

public class CategoryApi {
  private final APIRequestContext request;

  public CategoryApi() {
    this.request = ApiContext.get();
  }

  public APIResponse list() {
    return request.get("/api/categories");
  }

  public APIResponse create(String name) {
    String body = JsonUtils.toJson(Map.of("name", name));
    return request.post("/api/categories", APIRequestContextOptionsFactory.jsonBody(body));
  }

  public APIResponse rename(long id, String name) {
    String body = JsonUtils.toJson(Map.of("name", name));
    return request.put("/api/categories/" + id, APIRequestContextOptionsFactory.jsonBody(body));
  }

  public APIResponse deleteWithReassign(long id, long reassignToCategoryId) {
    String body = JsonUtils.toJson(Map.of("reassignToCategoryId", reassignToCategoryId));
    return request.delete("/api/categories/" + id, APIRequestContextOptionsFactory.jsonBody(body));
  }
}
