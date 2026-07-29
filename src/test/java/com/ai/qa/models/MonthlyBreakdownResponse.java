package com.ai.qa.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MonthlyBreakdownResponse {
  public String month; // yyyy-MM
  public BigDecimal overallTotal;
  public List<CategoryTotal> categoryTotals;

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class CategoryTotal {
    public Long categoryId;
    public String categoryName;
    public BigDecimal total;
  }
}
