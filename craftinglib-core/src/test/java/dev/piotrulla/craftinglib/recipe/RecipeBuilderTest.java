package dev.piotrulla.craftinglib.recipe;

import dev.piotrulla.craftinglib.item.MatchMode;
import dev.piotrulla.craftinglib.support.TestItem;
import dev.piotrulla.craftinglib.support.TestRecipes;
import org.junit.jupiter.api.Test;

import static dev.piotrulla.craftinglib.support.TestRecipes.shaped;
import static dev.piotrulla.craftinglib.support.TestRecipes.shapeless;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecipeBuilderTest {

    private static final TestItem STICK = TestItem.of("stick");

    @Test
    void shouldApplyDefaults() {
        ShapedCraftingRecipe<TestItem> recipe = shaped("ladder")
                .withPattern("S S", "SSS", "S S")
                .withIngredient('S', STICK)
                .withResult(TestItem.of("ladder", 3))
                .build();

        assertEquals("ladder", recipe.getKey());
        assertEquals(RecipeType.SHAPED, recipe.getType());
        assertEquals(MatchMode.EXACT, recipe.getMatchMode());
        assertTrue(recipe.isMirrored());
        assertEquals(TestItem.of("ladder", 3), recipe.getResult());
    }

    @Test
    void shouldRejectInvalidKey() {
        assertThrows(IllegalArgumentException.class, () -> shaped("Super Pickaxe"));
        assertThrows(IllegalArgumentException.class, () -> shapeless(""));
    }

    @Test
    void shouldRejectUnmappedSymbol() {
        TestRecipes.ShapedBuilder builder = shaped("broken")
                .withPattern("SX")
                .withIngredient('S', STICK)
                .withResult(STICK);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    void shouldRejectUnusedIngredient() {
        TestRecipes.ShapedBuilder builder = shaped("typo")
                .withPattern("SS")
                .withIngredient('S', STICK)
                .withIngredient('X', STICK)
                .withResult(STICK);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    void shouldRejectMissingResultAndPattern() {
        assertThrows(IllegalStateException.class, () -> shaped("no-result").withPattern("S")
                .withIngredient('S', STICK).build());
        assertThrows(IllegalStateException.class, () -> shaped("no-pattern").withResult(STICK).build());
        assertThrows(IllegalStateException.class, () -> shapeless("no-ingredients").withResult(STICK).build());
    }

    @Test
    void shouldRejectEmptyItems() {
        assertThrows(IllegalArgumentException.class, () -> shaped("air").withIngredient('A', TestItem.of("air")));
        assertThrows(IllegalArgumentException.class, () -> shaped("zero").withResult(TestItem.of("stick", 0)));
        assertThrows(IllegalArgumentException.class, () -> shaped("space").withIngredient(' ', STICK));
    }

    @Test
    void shouldLimitShapelessIngredients() {
        TestRecipes.ShapelessBuilder builder = shapeless("too-many");
        for (int index = 0; index < ShapelessCraftingRecipe.MAX_INGREDIENTS; index++) {
            builder.withIngredient(STICK);
        }

        assertThrows(IllegalStateException.class, () -> builder.withIngredient(STICK));
    }
}
