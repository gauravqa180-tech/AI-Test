package com.gauravqa.expense.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.gauravqa.expense.api.CategoryApi;
import com.gauravqa.expense.core.JsonUtils;
import com.gauravqa.expense.hooks.Hooks;
import com.microsoft.playwright.APIResponse;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.Locale;

public class CategorySteps {

  private final CategoryApi categoryApi = new CategoryApi();

  @And("at least one category exists")
  public void ensureCategoryExists() {
    APIResponse resp = categoryApi.list();
    Hooks.ctx().put("lastResponse", resp);
    Assertions.assertEquals(200, resp.status(), "Categories list must be reachable");

    JsonNode json = JsonUtils.toJsonNode(resp.text());
    if (!json.isArray() || json.size() == 0) {
      // create a default category if none exist
      APIResponse create = categoryApi.create("Food");
      Hooks.ctx().put("lastResponse", create);
      Assertions.assertTrue(create.status() == 201 || create.status() == 400,
          "Expected create category to succeed or already exist; status=" + create.status());
    }
  }

  @When("I list categories")
  public void listCategories() {
    APIResponse resp = categoryApi.list();
    Hooks.ctx().put("lastResponse", resp);
    if (resp.status() == 200) {
      Hooks.ctx().put("categoriesJson", JsonUtils.toJsonNode(resp.text()));
    }
  }

  @When("I create a category named {string}")
  public void createCategory(String name) {
    APIResponse resp = categoryApi.create(name);
    Hooks.ctx().put("lastResponse", resp);
    if (resp.status() == 201) {
      JsonNode json = JsonUtils.toJsonNode(resp.text());
      Hooks.ctx().put("categoryId", json.get("id").asLong());
      Hooks.ctx().put("categoryName", json.get("name").asText());
    }
  }

  @Given("I have a category named {string}")
  public void iHaveCategoryNamed(String name) {
    Long id = findCategoryIdByName(name);
    if (id == null) {
      APIResponse create = categoryApi.create(name);
      Hooks.ctx().put("lastResponse", create);
      Assertions.assertTrue(create.status() == 201 || create.status() == 400,
          "Create category expected 201 or 400, got " + create.status());
      id = findCategoryIdByName(name);
    }
    Assertions.assertNotNull(id, "Unable to find or create category: " + name);
    Hooks.ctx().put("categoryId", id);
    Hooks.ctx().put("categoryName", name);
  }

  @When("I rename the category to {string}")
  public void renameCategoryTo(String newName) {
    Long id = Hooks.ctx().get("categoryId");
    Assertions.assertNotNull(id, "categoryId is required");
    APIResponse resp = categoryApi.rename(id, newName);
    Hooks.ctx().put("lastResponse", resp);
  }

  @When("I delete category {string} and reassign expenses to {string}")
  public void deleteCategoryAndReassign(String deleteName, String reassignName) {
    Long deleteId = findCategoryIdByName(deleteName);
    Long reassignId = findCategoryIdByName(reassignName);
    Assertions.assertNotNull(deleteId, "Delete category not found: " + deleteName);
    Assertions.assertNotNull(reassignId, "Reassign category not found: " + reassignName);

    APIResponse resp = categoryApi.deleteWithReassign(deleteId, reassignId);
    Hooks.ctx().put("lastResponse", resp);

    // store for later checks
    Hooks.ctx().put("deletedCategoryId", deleteId);
    Hooks.ctx().put("reassignCategoryId", reassignId);
    Hooks.ctx().put("reassignCategoryName", reassignName);
  }

  @Then("the category list should contain {string}")
  public void categoryListShouldContain(String expectedName) {
    APIResponse resp = Hooks.ctx().get("lastResponse");
    if (resp == null || resp.status() != 200) {
      resp = categoryApi.list();
    }
    Assertions.assertEquals(200, resp.status(), "Expected list categories to return 200");

    JsonNode json = JsonUtils.toJsonNode(resp.text());
    boolean found = false;
    for (JsonNode n : json) {
      String name = n.get("name").asText();
      if (name.equalsIgnoreCase(expectedName)) {
        found = true;
        break;
      }
    }
    Assertions.assertTrue(found, "Expected category list to contain: " + expectedName + " but was: " + json);
  }

  private Long findCategoryIdByName(String name) {
    APIResponse resp = categoryApi.list();
    if (resp.status() != 200) return null;

    JsonNode json = JsonUtils.toJsonNode(resp.text());
    for (JsonNode n : json) {
      String currentName = n.get("name").asText();
      if (currentName != null && currentName.toLowerCase(Locale.ROOT).equals(name.toLowerCase(Locale.ROOT))) {
        return n.get("id").asLong();
      }
    }
    return null;
  }
}
