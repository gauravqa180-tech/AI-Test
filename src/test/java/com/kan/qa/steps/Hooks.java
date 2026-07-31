package com.kan.qa.steps;

import com.kan.qa.core.PlaywrightApiFactory;
import io.cucumber.java.AfterAll;

public class Hooks {

    @AfterAll
    public static void afterAll() {
        PlaywrightApiFactory.close();
    }
}
