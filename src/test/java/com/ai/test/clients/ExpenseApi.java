package com.ai.test.clients;

import com.ai.test.model.ExpenseRequest;
import com.ai.test.utils.JsonUtils;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;

public class ExpenseApi {
  private final APIRequestContext request;

  public ExpenseApi(APIRequestContext request) {
    this.request = request;
  }

  public APIResponse createExpense(ExpenseRequest payload) {
    return request.post("/api/expenses",
        RequestOptions.create().setData(JsonUtils.toJson(payload)));
  }

  public APIResponse getExpense(long id) {
    return request.get("/api/expenses/" + id);
  }

  public APIResponse listExpenses() {
    return request.get("/api/expenses");
  }

  public APIResponse updateExpense(long id, ExpenseRequest payload) {
    return request.put("/api/expenses/" + id,
        RequestOptions.create().setData(JsonUtils.toJson(payload)));
  }

  public APIResponse deleteExpense(long id) {
    return request.delete("/api/expenses/" + id);
  }
}
