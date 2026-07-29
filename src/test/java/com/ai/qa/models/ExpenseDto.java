package com.ai.qa.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExpenseDto {
  public Long id;
  public BigDecimal amount;
  public String date; // yyyy-MM-dd
  public Long categoryId;
  public String categoryName;
  public String note;
}
