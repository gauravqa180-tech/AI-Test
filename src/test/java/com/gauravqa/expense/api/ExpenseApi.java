package com.gauravqa.expense.api;

import com.gauravqa.expense.core.ApiContext;
import com.gauravqa.expense.core.JsonUtils;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ExpenseApi {
  private final APIRequestContext request;

  public ExpenseApi() {
    this.request = ApiContext.get();
  }

  public APIResponse create(BigDecimal amount, String date, long categoryId, String note) {
    Map<String, Object> payload = new HashMap<>();
    payload.put("amount", amount);
    payload.put("date", date);
    payload.put("categoryId", categoryId);
    payload.put("note", note);

    return request.post("/api/expenses", APIRequestContextOptionsFactory.jsonBody(JsonUtils.toJson(payload)));
  }

  public APIResponse update(long id, BigDecimal amount, String date, long categoryId, String note) {
    Map<String, Object> payload = new HashMap<>();
    payload.put("amount", amount);
    payload.put("date", date);
    payload.put("categoryId", categoryId);
    payload.put("note", note);

    return request.put("/api/expenses/" + id, APIRequestContextOptionsFactory.jsonBody(JsonUtils.toJson(payload)));
  }

  public APIResponse delete(long id) {
    return request.delete("/api/expenses/" + id);
  }

  public APIResponse undoDelete(long id) {
    return request.post("/api/expenses/" + id + "/undo-delete");
  }

  public APIResponse list(Map<String, String> queryParams) {
    String url = "/api/expenses";
    if (queryParams != null && !queryParams.isEmpty()) {
      StringBuilder sb = new StringBuilder(url).append("?");
      boolean first = true;
      for (Map.Entry<String, String> e : queryParams.entrySet()) {
        if (!first) sb.append("&");
        first = false;
        sb.append(e.getKey()).append("=").append(UrlEncoder.encode(e.getValue()));
      }
      url = sb.toString();
    }
    return request.get(url);
  }

  public APIResponse monthlyTotal(String month) {
    return request.get("/api/expenses/monthly-total?month=" + UrlEncoder.encode(month));
  }
}
