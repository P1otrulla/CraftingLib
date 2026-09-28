package dev.piotrulla.craftinglib.tests;

import be.seeseemelk.mockbukkit.MockBukkit;
import dev.piotrulla.craftinglib.bukkit.BukkitRecipes;
import dev.piotrulla.craftinglib.grid.CraftingGrid;
import dev.piotrulla.craftinglib.recipe.GrindstoneRecipe;
import dev.piotrulla.craftinglib.recipe.RecipeMatch;
import dev.piotrulla.craftinglib.recipe.ShapedCraftingRecipe;
import dev.piotrulla.craftinglib.recipe.ShapelessCraftingRecipe;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BukkitRecipesTest {

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void shouldBuildShapedRecipeFromMaterials() {
        ShapedCraftingRecipe<ItemStack> recipe = BukkitRecipes.shaped("compressed-cobblestone")
                .withPattern("CCC", "CCC", "CCC")
                .withIngredient('C', Material.COBBLESTONE, 2)
                .withResult(Material.STONE, 4)
                .build();

        assertEquals(new ItemStack(Material.STONE, 4), recipe.getResult());
        assertEquals(2, recipe.getIngredients().get('C').getAmount());
        assertFalse(recipe.fitsGrid(CraftingGrid.INVENTORY_SIZE));
    }

    @Test
    void shouldMatchTorchRecipeInPlayerInventory() {
        ShapedCraftingRecipe<ItemStack> recipe = BukkitRecipes.shaped("better-torches")
                .withPattern("C", "S")
                .withIngredient('C', Material.COAL)
                .withIngredient('S', Material.STICK)
                .withResult(Material.TORCH, 8)
                .build();

        RecipeMatch<ItemStack> match = recipe.match(CraftingGrid.of(Arrays.asList(
                null, new ItemStack(Material.COAL, 5),
                null, new ItemStack(Material.STICK, 3)
        ))).orElseThrow();

        assertEquals(3, match.getMaxCrafts());
        assertEquals(1, match.getConsumption(1));
        assertEquals(1, match.getConsumption(3));
    }

    @Test
    void shouldBuildShapelessRecipe() {
        ShapelessCraftingRecipe<ItemStack> recipe = BukkitRecipes.shapeless("quick-bread")
                .withIngredient(Material.WHEAT)
                .withIngredient(Material.WHEAT)
                .withResult(Material.BREAD)
                .build();

        assertTrue(recipe.match(CraftingGrid.of(Arrays.asList(
                new ItemStack(Material.WHEAT), null,
                null, new ItemStack(Material.WHEAT)
        ))).isPresent());
    }

    @Test
    void shouldBuildGrindstoneRecipeFromMaterials() {
        GrindstoneRecipe<ItemStack> recipe = BukkitRecipes.grindstone("scrap-sword")
                .withInput(Material.DIAMOND_SWORD)
                .withSecondInput(Material.FLINT, 2)
                .withResult(Material.DIAMOND)
                .withExperience(5)
                .build();

        assertTrue(recipe.matches(new ItemStack(Material.DIAMOND_SWORD), new ItemStack(Material.FLINT, 2)));
        assertFalse(recipe.matches(new ItemStack(Material.DIAMOND_SWORD), null));
        assertEquals(5, recipe.getExperience());
    }

    @Test
    void shouldFailFastOnInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> BukkitRecipes.shaped("Invalid Key"));
        assertThrows(IllegalArgumentException.class, () -> BukkitRecipes.shaped("air").withResult(Material.AIR));
        assertThrows(IllegalArgumentException.class, () -> BukkitRecipes.shaped("typo")
                .withPattern("SX")
                .withIngredient('S', Material.STICK)
                .withResult(Material.STICK)
                .build());
    }
}
