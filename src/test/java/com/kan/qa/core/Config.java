package com.kan.qa.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Config {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream is = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) {
                PROPS.load(is);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    private Config() {
    }

    public static String baseUrl() {
        return System.getProperty("baseUrl", PROPS.getProperty("baseUrl", "http://localhost:8080")).trim();
    }
}
