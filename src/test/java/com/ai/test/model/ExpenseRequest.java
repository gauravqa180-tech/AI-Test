package com.ai.test.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExpenseRequest(
    BigDecimal amount,
    String date,
    String category,
    String note
) {}
