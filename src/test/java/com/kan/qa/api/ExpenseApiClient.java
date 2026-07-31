package com.kan.qa.api;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;

public class ExpenseApiClient {

    private final APIRequestContext request;

    public ExpenseApiClient(APIRequestContext request) {
        this.request = request;
    }

    public APIResponse createExpense(String jsonBody) {
        return request.post("/api/expenses", APIRequestContextOptions.json(jsonBody));
    }

    public APIResponse listExpenses() {
        return request.get("/api/expenses");
    }

    public APIResponse updateExpense(long id, String jsonBody) {
        return request.put("/api/expenses/" + id, APIRequestContextOptions.json(jsonBody));
    }

    public APIResponse deleteExpense(long id) {
        return request.delete("/api/expenses/" + id);
    }

    public APIResponse restoreExpense(long id) {
        return request.post("/api/expenses/" + id + "/restore");
    }

    /**
     * Small helper to pass JSON string as request body.
     */
    static final class APIRequestContextOptions {
        private APIRequestContextOptions() {}

        static com.microsoft.playwright.options.RequestOptions json(String json) {
            return com.microsoft.playwright.options.RequestOptions.create()
                    .setHeader("Content-Type", "application/json")
                    .setData(json);
        }
    }
}
