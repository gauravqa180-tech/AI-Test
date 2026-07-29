package com.ai.qa.core;

import java.util.HashMap;
import java.util.Map;

public class ScenarioContext {
  private final Map<String, Object> data = new HashMap<>();

  public void put(String key, Object value) {
    data.put(key, value);
  }

  @SuppressWarnings("unchecked")
  public <T> T get(String key, Class<T> clazz) {
    Object val = data.get(key);
    if (val == null) return null;
    return (T) val;
  }

  public Object get(String key) {
    return data.get(key);
  }
}
