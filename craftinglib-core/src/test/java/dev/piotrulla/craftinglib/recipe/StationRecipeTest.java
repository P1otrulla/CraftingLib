package dev.piotrulla.craftinglib.recipe;

import dev.piotrulla.craftinglib.support.TestItem;
import org.junit.jupiter.api.Test;

import static dev.piotrulla.craftinglib.support.TestRecipes.anvil;
import static dev.piotrulla.craftinglib.support.TestRecipes.grindstone;
import static dev.piotrulla.craftinglib.support.TestRecipes.smithing;
import static dev.piotrulla.craftinglib.support.TestRecipes.stonecutting;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StationRecipeTest {

    private static final TestItem STONE = TestItem.of("stone");
    private static final TestItem TEMPLATE = TestItem.of("template");
    private static final TestItem SWORD = TestItem.of("diamond_sword");
    private static final TestItem INGOT = TestItem.of("mithril_ingot");
    private static final TestItem MITHRIL_SWORD = TestItem.of("sword").named("Mithril Sword");

    @Test
    void shouldMatchStonecuttingInput() {
        StonecuttingRecipe<TestItem> recipe = stonecutting("bricks").withInput(STONE)
                .withResult(TestItem.of("brick", 4)).build();

        assertTrue(recipe.matches(STONE.withAmount(10)));
        assertFalse(recipe.matches(TestItem.of("dirt")));
        assertEquals(TestItem.of("brick", 4), recipe.getResult());
    }

    @Test
    void shouldMatchSmithingWithTemplate() {
        SmithingRecipe<TestItem> recipe = smithing("mithril").withTemplate(TEMPLATE).withBase(SWORD)
                .withAddition(INGOT).withResult(MITHRIL_SWORD).build();

        assertTrue(recipe.getTemplate().isPresent());
        assertTrue(recipe.matches(TEMPLATE, SWORD, INGOT));
        assertFalse(recipe.matches(null, SWORD, INGOT));
        assertFalse(recipe.matches(TEMPLATE, SWORD, STONE));
    }

    @Test
    void shouldMatchSmithingWithoutTemplate() {
        SmithingRecipe<TestItem> recipe = smithing("legacy").withBase(SWORD).withAddition(INGOT)
                .withResult(MITHRIL_SWORD).build();

        assertFalse(recipe.getTemplate().isPresent());
        assertTrue(recipe.matches(null, SWORD, INGOT));
        assertFalse(recipe.matches(TEMPLATE, SWORD, INGOT));
    }

    @Test
    void shouldMatchAnvilWithAmounts() {
        AnvilRecipe<TestItem> recipe = anvil("upgrade").withLeft(SWORD).withRight(INGOT.withAmount(3))
                .withResult(MITHRIL_SWORD).withLevelCost(10).build();

        assertEquals(10, recipe.getLevelCost());
        assertEquals(3, recipe.getRight().getAmount());
        assertTrue(recipe.matches(SWORD, INGOT.withAmount(5)));
        assertFalse(recipe.matches(SWORD, INGOT.withAmount(2)));
        assertFalse(recipe.matches(INGOT, SWORD));
    }

    @Test
    void shouldRejectInvalidStationRecipes() {
        assertThrows(IllegalArgumentException.class, () -> stonecutting("stack").withInput(STONE.withAmount(2))
                .withResult(STONE).build());
        assertThrows(IllegalStateException.class, () -> smithing("no-base").withAddition(INGOT).withResult(SWORD).build());
        assertThrows(IllegalArgumentException.class, () -> anvil("free").withLeft(SWORD).withRight(INGOT)
                .withResult(SWORD).withLevelCost(0).build());
        assertThrows(IllegalArgumentException.class, () -> anvil("vanilla").withLeft(SWORD).withRight(INGOT)
                .withResult(SWORD).replaceVanillaRecipes(true).build());
    }

    @Test
    void shouldMatchGrindstoneWithSecondInput() {
        GrindstoneRecipe<TestItem> recipe = grindstone("cleanse").withInput(MITHRIL_SWORD).withSecondInput(INGOT.withAmount(2))
                .withResult(SWORD).withExperience(15).build();

        assertEquals(15, recipe.getExperience());
        assertTrue(recipe.getSecondInput().isPresent());
        assertTrue(recipe.matches(MITHRIL_SWORD, INGOT.withAmount(2)));
        assertFalse(recipe.matches(MITHRIL_SWORD, INGOT));
        assertFalse(recipe.matches(INGOT.withAmount(2), MITHRIL_SWORD));
        assertFalse(recipe.matches(MITHRIL_SWORD, null));
    }

    @Test
    void shouldMatchGrindstoneWithEmptySecondSlot() {
        GrindstoneRecipe<TestItem> recipe = grindstone("scrap").withInput(SWORD).withResult(INGOT).build();

        assertEquals(GrindstoneRecipe.NO_EXPERIENCE, recipe.getExperience());
        assertFalse(recipe.getSecondInput().isPresent());
        assertTrue(recipe.matches(SWORD, null));
        assertFalse(recipe.matches(SWORD, STONE));
    }

    @Test
    void shouldRejectInvalidGrindstoneRecipes() {
        assertThrows(IllegalStateException.class, () -> grindstone("no-input").withResult(SWORD).build());
        assertThrows(IllegalArgumentException.class, () -> grindstone("negative").withInput(SWORD)
                .withResult(INGOT).withExperience(-1).build());
        assertThrows(IllegalArgumentException.class, () -> grindstone("vanilla").withInput(SWORD)
                .withResult(INGOT).replaceVanillaRecipes(true).build());
    }
}
