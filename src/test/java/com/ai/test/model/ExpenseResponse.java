package com.ai.test.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ExpenseResponse(
    Long id,
    BigDecimal amount,
    String date,
    String category,
    String note
) {}
