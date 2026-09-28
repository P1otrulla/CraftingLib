package dev.piotrulla.craftinglib.recipe;

import dev.piotrulla.craftinglib.item.MatchMode;
import dev.piotrulla.craftinglib.recipe.smelting.CookingType;
import dev.piotrulla.craftinglib.recipe.smelting.SmeltingRecipe;
import dev.piotrulla.craftinglib.support.TestItem;
import org.junit.jupiter.api.Test;

import static dev.piotrulla.craftinglib.support.TestRecipes.brewing;
import static dev.piotrulla.craftinglib.support.TestRecipes.smelting;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SmeltingAndBrewingRecipeTest {

    private static final TestItem ORE = TestItem.of("ore");
    private static final TestItem INGOT = TestItem.of("ingot");
    private static final TestItem AWKWARD = TestItem.of("potion").named("awkward");
    private static final TestItem CARROT = TestItem.of("golden_carrot");

    @Test
    void shouldUseVanillaCookingTimeOfStation() {
        SmeltingRecipe<TestItem> furnace = smelting("furnace").withInput(ORE).withResult(INGOT).build();
        SmeltingRecipe<TestItem> blasting = smelting("blasting").withCookingType(CookingType.BLASTING)
                .withInput(ORE).withResult(INGOT).build();

        assertEquals(CookingType.FURNACE, furnace.getCookingType());
        assertEquals(200, furnace.getCookingTime());
        assertEquals(100, blasting.getCookingTime());
    }

    @Test
    void shouldKeepCustomCookingSettings() {
        SmeltingRecipe<TestItem> recipe = smelting("fast").withInput(ORE).withResult(INGOT.withAmount(2))
                .withCookingTime(40).withExperience(1.5F).build();

        assertEquals(40, recipe.getCookingTime());
        assertEquals(1.5F, recipe.getExperience(), 0.0001F);
        assertEquals(INGOT.withAmount(2), recipe.getResult());
    }

    @Test
    void shouldMatchSmeltingInputByMatchMode() {
        TestItem magicOre = ORE.named("Magic");
        SmeltingRecipe<TestItem> exact = smelting("exact").withInput(magicOre).withResult(INGOT).build();
        SmeltingRecipe<TestItem> typeOnly = smelting("type").withInput(magicOre).withResult(INGOT)
                .withMatchMode(MatchMode.TYPE).build();

        assertTrue(exact.matches(magicOre));
        assertFalse(exact.matches(ORE));
        assertTrue(typeOnly.matches(ORE));
        assertFalse(typeOnly.matches(null));
    }

    @Test
    void shouldRejectInvalidSmeltingRecipes() {
        assertThrows(IllegalArgumentException.class, () -> smelting("stack").withInput(ORE.withAmount(2)).withResult(INGOT).build());
        assertThrows(IllegalArgumentException.class, () -> smelting("xp").withInput(ORE).withResult(INGOT).withExperience(-1F).build());
        assertThrows(IllegalArgumentException.class, () -> smelting("time").withInput(ORE).withResult(INGOT).withCookingTime(0).build());
        assertThrows(IllegalStateException.class, () -> smelting("no-input").withResult(INGOT).build());
    }

    @Test
    void shouldMatchBrewingInputAndIngredient() {
        BrewingRecipe<TestItem> recipe = brewing("luck").withInput(AWKWARD).withIngredient(CARROT)
                .withResult(TestItem.of("potion").named("luck")).build();

        assertTrue(recipe.matches(AWKWARD, CARROT));
        assertFalse(recipe.matches(TestItem.of("potion"), CARROT));
        assertFalse(recipe.matches(AWKWARD, ORE));
    }

    @Test
    void shouldRejectInvalidBrewingRecipes() {
        assertThrows(IllegalArgumentException.class, () -> brewing("vanilla").withInput(AWKWARD).withIngredient(CARROT)
                .withResult(INGOT).replaceVanillaRecipes(true).build());
        assertThrows(IllegalStateException.class, () -> brewing("no-ingredient").withInput(AWKWARD).withResult(INGOT).build());
        assertThrows(IllegalArgumentException.class, () -> brewing("stack").withInput(AWKWARD).withIngredient(CARROT.withAmount(3))
                .withResult(INGOT).build());
    }
}