package dev.piotrulla.craftinglib.recipe;

import dev.piotrulla.craftinglib.item.MatchMode;
import dev.piotrulla.craftinglib.support.TestItem;
import org.junit.jupiter.api.Test;

import static dev.piotrulla.craftinglib.support.TestRecipes.grid;
import static dev.piotrulla.craftinglib.support.TestRecipes.shaped;
import static dev.piotrulla.craftinglib.support.TestRecipes.shapeless;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShapelessCraftingRecipeTest {

    private static final TestItem WHEAT = TestItem.of("wheat");
    private static final TestItem SUGAR = TestItem.of("sugar");
    private static final TestItem EGG = TestItem.of("egg");

    private final ShapelessCraftingRecipe<TestItem> dough = shapeless("dough")
            .withIngredient(WHEAT)
            .withIngredient(SUGAR)
            .withIngredient(EGG)
            .withResult(TestItem.of("dough"))
            .build();

    @Test
    void shouldIgnoreIngredientPositions() {
        assertTrue(this.dough.match(grid(
                null, null, EGG,
                null, WHEAT, null,
                SUGAR, null, null
        )).isPresent());
        assertTrue(this.dough.match(grid(SUGAR, EGG, null, WHEAT)).isPresent());
    }

    @Test
    void shouldRejectMissingOrExtraItems() {
        assertFalse(this.dough.match(grid(SUGAR, EGG, null, null)).isPresent());
        assertFalse(this.dough.match(grid(SUGAR, EGG, WHEAT, WHEAT)).isPresent());
    }

    @Test
    void shouldBacktrackWhenIngredientsOverlap() {
        TestItem namedDiamond = TestItem.of("diamond").named("Magic");
        ShapelessCraftingRecipe<TestItem> recipe = shapeless("overlap")
                .withIngredient(TestItem.of("diamond"))
                .withIngredient(namedDiamond)
                .withResult(TestItem.of("gem"))
                .withMatchMode(MatchMode.EXACT)
                .build();

        assertTrue(recipe.match(grid(namedDiamond, TestItem.of("diamond"), null, null)).isPresent());
    }

    @Test
    void shouldReportConsumptionPerSlot() {
        ShapelessCraftingRecipe<TestItem> recipe = shapeless("bread")
                .withIngredient(TestItem.of("wheat", 2))
                .withIngredient(EGG)
                .withResult(TestItem.of("bread"))
                .build();

        RecipeMatch<TestItem> match = recipe.match(grid(EGG.withAmount(5), null, null, TestItem.of("wheat", 4))).orElseThrow();

        assertEquals(1, match.getConsumption(0));
        assertEquals(2, match.getConsumption(3));
        assertEquals(2, match.getMaxCrafts());
    }

    @Test
    void shouldFitPlayerInventoryUpToFourIngredients() {
        assertTrue(this.dough.fitsGrid(2));
        assertFalse(shaped("big").withPattern("AAA").withIngredient('A', WHEAT)
                .withResult(SUGAR).build().fitsGrid(2));
    }
}
