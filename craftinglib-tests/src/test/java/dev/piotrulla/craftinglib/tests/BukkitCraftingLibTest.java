package dev.piotrulla.craftinglib.tests;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import dev.piotrulla.craftinglib.bukkit.BukkitCraftingLib;
import dev.piotrulla.craftinglib.bukkit.BukkitRecipes;
import dev.piotrulla.craftinglib.recipe.CraftingRecipe;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BukkitCraftingLibTest {

    private ServerMock server;
    private Plugin plugin;
    private BukkitCraftingLib craftingLib;

    @BeforeEach
    void setUp() {
        this.server = MockBukkit.mock();
        this.plugin = MockBukkit.createMockPlugin();
        this.craftingLib = BukkitCraftingLib.create(this.plugin);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void shouldRegisterServerRecipe() {
        this.craftingLib.getRecipeManager().registerRecipe(this.torchRecipe(false));

        assertTrue(this.serverRecipeKeys().contains(new NamespacedKey(this.plugin, "better-torches")));
    }

    @Test
    void shouldUnregisterServerRecipe() {
        this.craftingLib.getRecipeManager().registerRecipe(this.torchRecipe(false));

        assertTrue(this.craftingLib.getRecipeManager().unregisterRecipe("better-torches"));
        assertFalse(this.serverRecipeKeys().contains(new NamespacedKey(this.plugin, "better-torches")));
    }

    @Test
    void shouldRemoveForeignRecipesWhenReplacingVanilla() {
        NamespacedKey vanillaKey = NamespacedKey.minecraft("test_torch");
        ShapedRecipe vanillaTorch = new ShapedRecipe(vanillaKey, new ItemStack(Material.TORCH, 4));
        vanillaTorch.shape("C", "S");
        vanillaTorch.setIngredient('C', Material.COAL);
        vanillaTorch.setIngredient('S', Material.STICK);
        this.server.addRecipe(vanillaTorch);

        this.craftingLib.getRecipeManager().registerRecipe(this.torchRecipe(true));

        List<NamespacedKey> keys = this.serverRecipeKeys();
        assertFalse(keys.contains(vanillaKey));
        assertTrue(keys.contains(new NamespacedKey(this.plugin, "better-torches")));
    }

    @Test
    void shouldRegisterGrindstoneRecipe() {
        this.craftingLib.getGrindstoneManager().registerRecipe(BukkitRecipes.grindstone("scrap-torch")
                .withInput(Material.TORCH)
                .withResult(Material.STICK)
                .build());

        assertTrue(this.craftingLib.getGrindstoneManager().findRecipe("scrap-torch").isPresent());
    }

    @Test
    void shouldUnregisterEverythingOnShutdown() {
        this.craftingLib.getRecipeManager().registerRecipe(this.torchRecipe(false));

        this.craftingLib.shutdown();

        assertEquals(0, this.craftingLib.getRecipeManager().getRecipes().size());
        assertFalse(this.serverRecipeKeys().contains(new NamespacedKey(this.plugin, "better-torches")));
    }

    private CraftingRecipe<ItemStack> torchRecipe(boolean replaceVanilla) {
        return BukkitRecipes.shaped("better-torches")
                .withPattern("C", "S")
                .withIngredient('C', Material.COAL)
                .withIngredient('S', Material.STICK)
                .withResult(Material.TORCH, 8)
                .replaceVanillaRecipes(replaceVanilla)
                .build();
    }

    private List<NamespacedKey> serverRecipeKeys() {
        List<NamespacedKey> keys = new ArrayList<>();
        Iterator<Recipe> iterator = this.server.recipeIterator();

        while (iterator.hasNext()) {
            Recipe recipe = iterator.next();
            if (recipe instanceof Keyed) {
                keys.add(((Keyed) recipe).getKey());
            }
        }

        return keys;
    }
}
