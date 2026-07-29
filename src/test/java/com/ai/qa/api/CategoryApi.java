package com.ai.qa.api;

import com.ai.qa.utils.JsonUtils;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;

import java.util.Map;

public class CategoryApi {
  private final APIRequestContext request;

  public CategoryApi(APIRequestContext request) {
    this.request = request;
  }

  public APIResponse list() {
    return request.get("/api/categories");
  }

  public APIResponse create(String name) {
    String body = JsonUtils.toJson(Map.of("name", name));
    return request.post("/api/categories", APIRequestContextOptions.json(body));
  }

  public APIResponse update(long id, String name) {
    String body = JsonUtils.toJson(Map.of("name", name));
    return request.put("/api/categories/" + id, APIRequestContextOptions.json(body));
  }

  public APIResponse delete(long id, Long reassignToCategoryId) {
    String url = "/api/categories/" + id;
    if (reassignToCategoryId != null) {
      url += "?reassignToCategoryId=" + reassignToCategoryId;
    }
    return request.delete(url);
  }

  public APIResponse merge(long sourceCategoryId, long targetCategoryId) {
    String body = JsonUtils.toJson(Map.of(
        "sourceCategoryId", sourceCategoryId,
        "targetCategoryId", targetCategoryId
    ));
    return request.post("/api/categories/merge", APIRequestContextOptions.json(body));
  }

  /**
   * Tiny helper to set JSON post data in a Playwright-friendly way.
   */
  static class APIRequestContextOptions {
    static com.microsoft.playwright.options.RequestOptions json(String rawJson) {
      return com.microsoft.playwright.options.RequestOptions.create().setData(rawJson);
    }
  }
}
