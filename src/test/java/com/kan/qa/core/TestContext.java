package com.kan.qa.core;

import com.fasterxml.jackson.databind.JsonNode;

public class TestContext {
    private Long expenseId;
    private int lastStatus;
    private String lastResponseBody;
    private JsonNode lastJson;

    public Long getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(Long expenseId) {
        this.expenseId = expenseId;
    }

    public int getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(int lastStatus) {
        this.lastStatus = lastStatus;
    }

    public String getLastResponseBody() {
        return lastResponseBody;
    }

    public void setLastResponseBody(String lastResponseBody) {
        this.lastResponseBody = lastResponseBody;
    }

    public JsonNode getLastJson() {
        return lastJson;
    }

    public void setLastJson(JsonNode lastJson) {
        this.lastJson = lastJson;
    }
}
