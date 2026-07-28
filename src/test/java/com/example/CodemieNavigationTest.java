package com.example;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class CodemieNavigationTest {
  static Playwright playwright;
  static Browser browser;

  BrowserContext context;
  Page page;

  private static final String BASE_URL = "https://codemie.lab.epam.com/";

  @BeforeAll
  static void beforeAll() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
      .setHeadless(false)); // headed as requested
  }

  @AfterAll
  static void afterAll() {
    if (browser != null) browser.close();
    if (playwright != null) playwright.close();
  }

  @BeforeEach
  void beforeEach() {
    context = browser.newContext(new Browser.NewContextOptions()
      .setViewportSize(1365, 768));
    page = context.newPage();
  }

  @AfterEach
  void afterEach() {
    if (context != null) context.close();
  }

  @Test
  void openCodemie_verifyNavigation_takeScreenshot_andDescribePage() throws Exception {
    // 1) Navigate to Codemie
    Response response = page.navigate(BASE_URL, new Page.NavigateOptions()
      .setWaitUntil(LoadState.DOMCONTENTLOADED));

    // 2) Verify successful navigation
    assertNotNull(response, "No main-document response returned from navigation.");
    assertTrue(response.ok(), "Navigation response not OK. Status: " + response.status());
    assertTrue(page.url().startsWith(BASE_URL), "Did not navigate to expected URL. Actual: " + page.url());

    // Additional checkpoint: ensure the page has a non-empty title
    page.waitForLoadState(LoadState.NETWORKIDLE);
    String title = page.title();
    assertNotNull(title, "Page title is null.");
    assertFalse(title.trim().isEmpty(), "Page title is empty; page may not have loaded correctly.");

    // 3) Create screenshot
    Path outDir = Paths.get("target", "artifacts");
    Files.createDirectories(outDir);

    Path screenshotPath = outDir.resolve("codemie-home.png");
    page.screenshot(new Page.ScreenshotOptions()
      .setPath(screenshotPath)
      .setFullPage(true));

    assertTrue(Files.exists(screenshotPath), "Screenshot was not created: " + screenshotPath);

    // 4) Describe page content via accessibility snapshot (best-effort)
    Object snapshot = page.accessibility().snapshot();
    String a11ySnapshot = snapshot != null ? snapshot.toString() : "Accessibility snapshot not available.";

    System.out.println("==== Navigation Confirmed ====");
    System.out.println("URL: " + page.url());
    System.out.println("Title: " + title);
    System.out.println("Screenshot: " + screenshotPath.toAbsolutePath());

    System.out.println("==== Page Description (Accessibility Snapshot) ====");
    System.out.println(a11ySnapshot);

    // Optional sanity assertion that some body content exists
    int bodyTextLen = page.locator("body").innerText().trim().length();
    assertTrue(bodyTextLen > 0, "Body text appears empty; page content may not have rendered.");
  }
}
