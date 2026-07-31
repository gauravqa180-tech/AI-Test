package com.ai.test.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiError(
    String timestamp,
    int status,
    String error,
    String message,
    String path,
    List<FieldViolation> violations
) {
  @JsonIgnoreProperties(ignoreUnknown = true)
  public record FieldViolation(String field, String message) {}
}
