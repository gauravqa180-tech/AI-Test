package com.gauravqa.expense.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonUtils {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private JsonUtils() {}

  public static JsonNode toJsonNode(String body) {
    try {
      return MAPPER.readTree(body);
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse JSON", e);
    }
  }

  public static <T> T fromJson(String body, TypeReference<T> type) {
    try {
      return MAPPER.readValue(body, type);
    } catch (Exception e) {
      throw new RuntimeException("Failed to deserialize JSON", e);
    }
  }

  public static String toJson(Object obj) {
    try {
      return MAPPER.writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException("Failed to serialize JSON", e);
    }
  }
}
