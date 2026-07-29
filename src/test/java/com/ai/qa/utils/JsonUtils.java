package com.ai.qa.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonUtils {
  private JsonUtils() {}

  private static final ObjectMapper MAPPER = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  public static String toJson(Object obj) {
    try {
      return MAPPER.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to serialize JSON", e);
    }
  }

  public static <T> T fromJson(String json, Class<T> clazz) {
    try {
      return MAPPER.readValue(json, clazz);
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse JSON: " + json, e);
    }
  }
}
