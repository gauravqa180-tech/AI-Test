package com.ai.qa.api;

import com.ai.qa.utils.JsonUtils;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ExpenseApi {
  private final APIRequestContext request;

  public ExpenseApi(APIRequestContext request) {
    this.request = request;
  }

  public APIResponse list(Map<String, String> queryParams) {
    StringBuilder url = new StringBuilder("/api/expenses");
    if (queryParams != null && !queryParams.isEmpty()) {
      url.append("?");
      boolean first = true;
      for (Map.Entry<String, String> e : queryParams.entrySet()) {
        if (!first) url.append("&");
        first = false;
        url.append(e.getKey()).append("=").append(Url.encode(e.getValue()));
      }
    }
    return request.get(url.toString());
  }

  public APIResponse create(BigDecimal amount, String date, Long categoryId, String categoryName, String note) {
    Map<String, Object> payload = new HashMap<>();
    if (amount != null) payload.put("amount", amount);
    if (date != null) payload.put("date", date);
    if (categoryId != null) payload.put("categoryId", categoryId);
    if (categoryName != null) payload.put("categoryName", categoryName);
    if (note != null) payload.put("note", note);

    return request.post("/api/expenses", Request.json(JsonUtils.toJson(payload)));
  }

  public APIResponse update(long id, BigDecimal amount, String date, Long categoryId, String categoryName, String note) {
    Map<String, Object> payload = new HashMap<>();
    if (amount != null) payload.put("amount", amount);
    if (date != null) payload.put("date", date);
    if (categoryId != null) payload.put("categoryId", categoryId);
    if (categoryName != null) payload.put("categoryName", categoryName);
    if (note != null) payload.put("note", note);

    return request.put("/api/expenses/" + id, Request.json(JsonUtils.toJson(payload)));
  }

  public APIResponse delete(long id) {
    return request.delete("/api/expenses/" + id);
  }

  public APIResponse monthlyTotal(String month) {
    return request.get("/api/expenses/monthly-total?month=" + Url.encode(month));
  }

  public APIResponse monthlyBreakdown(String month) {
    return request.get("/api/expenses/monthly-breakdown?month=" + Url.encode(month));
  }

  static class Request {
    static com.microsoft.playwright.options.RequestOptions json(String rawJson) {
      return com.microsoft.playwright.options.RequestOptions.create().setData(rawJson);
    }
  }

  static class Url {
    static String encode(String s) {
      return java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8);
    }
  }
}
