package dev.piotrulla.craftinglib.recipe;

import dev.piotrulla.craftinglib.recipe.book.RecipeBookCategory;
import dev.piotrulla.craftinglib.recipe.book.RecipeBookSettings;
import dev.piotrulla.craftinglib.recipe.smelting.SmeltingRecipe;
import dev.piotrulla.craftinglib.support.TestItem;
import org.junit.jupiter.api.Test;

import static dev.piotrulla.craftinglib.support.TestRecipes.anvil;
import static dev.piotrulla.craftinglib.support.TestRecipes.shapeless;
import static dev.piotrulla.craftinglib.support.TestRecipes.smelting;
import static dev.piotrulla.craftinglib.support.TestRecipes.stonecutting;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecipeBookTest {

    private static final TestItem ORE = TestItem.of("ore");
    private static final TestItem INGOT = TestItem.of("ingot");
    private static final TestItem SWORD = TestItem.of("sword");

    @Test
    void shouldUseDefaultsWhenNotConfigured() {
        CraftingRecipe<TestItem> recipe = shapeless("plain").withIngredient(ORE).withResult(INGOT).build();

        assertEquals(RecipeBookSettings.DEFAULT, recipe.getRecipeBook());
        assertTrue(recipe.getRecipeBook().isDiscoverable());
        assertFalse(recipe.getRecipeBook().hasDisplaySettings());
    }

    @Test
    void shouldKeepCraftingRecipeBookSettings() {
        CraftingRecipe<TestItem> recipe = shapeless("grouped").withIngredient(ORE).withResult(INGOT)
                .withRecipeBookGroup("ingots").withRecipeBookCategory(RecipeBookCategory.EQUIPMENT)
                .discoverable(false).build();

        RecipeBookSettings recipeBook = recipe.getRecipeBook();
        assertFalse(recipeBook.isDiscoverable());
        assertEquals("ingots", recipeBook.getGroup());
        assertEquals(RecipeBookCategory.EQUIPMENT, recipeBook.getCategory().orElse(null));
    }

    @Test
    void shouldAcceptCookingCategoryForSmelting() {
        SmeltingRecipe<TestItem> recipe = smelting("food").withInput(ORE).withResult(INGOT)
                .withRecipeBookCategory(RecipeBookCategory.FOOD).build();

        assertEquals(RecipeBookCategory.FOOD, recipe.getRecipeBook().getCategory().orElse(null));
    }

    @Test
    void shouldRejectCategoryOfOtherRecipeBook() {
        assertThrows(IllegalArgumentException.class, () -> shapeless("food").withIngredient(ORE).withResult(INGOT)
                .withRecipeBookCategory(RecipeBookCategory.FOOD).build());
        assertThrows(IllegalArgumentException.class, () -> smelting("redstone").withInput(ORE).withResult(INGOT)
                .withRecipeBookCategory(RecipeBookCategory.REDSTONE).build());
    }

    @Test
    void shouldRejectRecipeBookOnStationsWithoutIt() {
        assertThrows(IllegalArgumentException.class, () -> stonecutting("category").withInput(ORE).withResult(INGOT)
                .withRecipeBookCategory(RecipeBookCategory.MISC).build());
        assertThrows(IllegalArgumentException.class, () -> anvil("group").withLeft(SWORD).withRight(INGOT)
                .withResult(SWORD).withRecipeBookGroup("swords").build());
    }

    @Test
    void shouldAllowGroupForStonecutting() {
        StonecuttingRecipe<TestItem> recipe = stonecutting("grouped").withInput(ORE).withResult(INGOT)
                .withRecipeBookGroup("ores").build();

        assertEquals("ores", recipe.getRecipeBook().getGroup());
    }
}
