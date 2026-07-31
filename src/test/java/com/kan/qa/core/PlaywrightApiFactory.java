package com.kan.qa.core;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;

import java.util.HashMap;
import java.util.Map;

public final class PlaywrightApiFactory {

    private static Playwright playwright;
    private static APIRequestContext request;

    private PlaywrightApiFactory() {
    }

    public static synchronized APIRequestContext getRequest() {
        if (request == null) {
            playwright = Playwright.create();

            Map<String, String> headers = new HashMap<>();
            headers.put("Accept", "application/json");
            headers.put("Content-Type", "application/json");

            request = playwright.request().newContext(new APIRequest.NewContextOptions()
                    .setBaseURL(Config.baseUrl())
                    .setExtraHTTPHeaders(headers));
        }
        return request;
    }

    public static synchronized void close() {
        if (request != null) {
            request.dispose();
            request = null;
        }
        if (playwright != null) {
            playwright.close();
            playwright = null;
        }
    }
}
