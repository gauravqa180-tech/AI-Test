package com.ai.qa.steps;

import com.ai.qa.api.CategoryApi;
import com.ai.qa.hooks.Hooks;
import com.ai.qa.models.CategoryDto;
import com.ai.qa.utils.JsonUtils;
import com.microsoft.playwright.APIResponse;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;

public class CategorySteps {

  private CategoryApi api() {
    return new CategoryApi(Hooks.api().request());
  }

  @Given("a category named {string} exists")
  public void categoryExists(String name) {
    APIResponse resp = api().create(name);
    Hooks.ctx().put("lastResponse", resp);
    if (resp.status() == 201 || resp.status() == 200) {
      CategoryDto dto = JsonUtils.fromJson(resp.text(), CategoryDto.class);
      Hooks.ctx().put("category:" + name, dto);
      return;
    }

    // If already exists, fetch it from list
    APIResponse list = api().list();
    Assertions.assertThat(list.status()).isEqualTo(200);
    CategoryDto[] cats = JsonUtils.fromJson(list.text(), CategoryDto[].class);
    for (CategoryDto c : cats) {
      if (c.name != null && c.name.equalsIgnoreCase(name)) {
        Hooks.ctx().put("category:" + name, c);
        return;
      }
    }
    Assertions.fail("Category not found/created: " + name + " status=" + resp.status() + " body=" + resp.text());
  }

  @When("I create a category named {string}")
  public void createCategory(String name) {
    APIResponse resp = api().create(name);
    Hooks.ctx().put("lastResponse", resp);
    if (resp.status() == 201 || resp.status() == 200) {
      CategoryDto dto = JsonUtils.fromJson(resp.text(), CategoryDto.class);
      Hooks.ctx().put("category:" + name, dto);
    }
  }

  @Then("the category is created successfully")
  public void categoryCreatedSuccessfully() {
    APIResponse resp = Hooks.ctx().get("lastResponse", APIResponse.class);
    Assertions.assertThat(resp.status()).isIn(200, 201);
  }

  @When("I list categories")
  public void listCategories() {
    APIResponse resp = api().list();
    Hooks.ctx().put("lastResponse", resp);
    Hooks.ctx().put("lastCategoriesJson", resp.text());
  }

  @Then("the categories list contains {string}")
  public void categoriesListContains(String name) {
    APIResponse resp = Hooks.ctx().get("lastResponse", APIResponse.class);
    Assertions.assertThat(resp.status()).isEqualTo(200);
    CategoryDto[] cats = JsonUtils.fromJson(resp.text(), CategoryDto[].class);
    Assertions.assertThat(cats).anyMatch(c -> c.name != null && c.name.equalsIgnoreCase(name));
  }

  @Then("the categories list should not contain {string}")
  public void categoriesListNotContains(String name) {
    APIResponse resp = Hooks.ctx().get("lastResponse", APIResponse.class);
    Assertions.assertThat(resp.status()).isEqualTo(200);
    CategoryDto[] cats = JsonUtils.fromJson(resp.text(), CategoryDto[].class);
    Assertions.assertThat(cats).noneMatch(c -> c.name != null && c.name.equalsIgnoreCase(name));
  }

  @When("I merge category {string} into {string}")
  public void mergeCategory(String source, String target) {
    CategoryDto s = Hooks.ctx().get("category:" + source, CategoryDto.class);
    CategoryDto t = Hooks.ctx().get("category:" + target, CategoryDto.class);
    Assertions.assertThat(s).isNotNull();
    Assertions.assertThat(t).isNotNull();

    APIResponse resp = api().merge(s.id, t.id);
    Hooks.ctx().put("lastResponse", resp);
  }

  @Then("the merge is successful")
  public void mergeSuccessful() {
    APIResponse resp = Hooks.ctx().get("lastResponse", APIResponse.class);
    Assertions.assertThat(resp.status()).isIn(200, 204);
  }

  @When("I delete category {string} without reassignment")
  public void deleteCategoryWithoutReassignment(String name) {
    CategoryDto c = Hooks.ctx().get("category:" + name, CategoryDto.class);
    Assertions.assertThat(c).isNotNull();
    APIResponse resp = api().delete(c.id, null);
    Hooks.ctx().put("lastResponse", resp);
  }
}
