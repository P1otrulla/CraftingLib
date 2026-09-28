package dev.piotrulla.craftinglib.bukkit.registry;

import dev.piotrulla.craftinglib.bukkit.item.RecipeChoices;
import dev.piotrulla.craftinglib.recipe.StonecuttingRecipe;
import dev.piotrulla.craftinglib.recipe.book.RecipeBookSettings;
import dev.piotrulla.craftinglib.recipe.manager.PlatformRecipeRegistrar;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Registers stonecutting recipes as native server recipes (1.14+).
 * Create it only when {@code VersionDetector.isStonecuttingSupported()} returns true.
 * <p>
 * {@code replaceVanilla} removes other stonecutter recipes with the same input and the same result type.
 */
public final class BukkitStonecuttingRecipeRegistrar implements PlatformRecipeRegistrar<StonecuttingRecipe<ItemStack>> {

    private static final boolean NOT_IN_RECIPE_BOOK = false;

    private final Plugin plugin;
    private final ServerRecipeRegistry serverRecipes;

    public BukkitStonecuttingRecipeRegistrar(@NotNull RegistrarContext context) {
        Objects.requireNonNull(context, "context");
        this.plugin = context.getPlugin();
        this.serverRecipes = new ServerRecipeRegistry(context,
                serverRecipe -> serverRecipe instanceof org.bukkit.inventory.StonecuttingRecipe);
    }

    @Override
    public void register(@NotNull StonecuttingRecipe<ItemStack> recipe) {
        Objects.requireNonNull(recipe, "recipe");

        if (recipe.shouldReplaceVanilla()) {
            this.serverRecipes.removeForeign(serverRecipe -> this.isSameCut(serverRecipe, recipe));
        }

        this.serverRecipes.add(recipe.getKey(), this.toServerRecipe(recipe), NOT_IN_RECIPE_BOOK);
    }

    @Override
    public void unregister(@NotNull StonecuttingRecipe<ItemStack> recipe) {
        Objects.requireNonNull(recipe, "recipe");
        this.serverRecipes.remove(recipe.getKey());
    }

    private Recipe toServerRecipe(StonecuttingRecipe<ItemStack> recipe) {
        org.bukkit.inventory.StonecuttingRecipe serverRecipe = new org.bukkit.inventory.StonecuttingRecipe(
                new NamespacedKey(this.plugin, recipe.getKey()),
                recipe.getResult(),
                RecipeChoices.of(recipe.getInput(), recipe.getMatchMode())
        );

        RecipeBookSettings recipeBook = recipe.getRecipeBook();
        if (recipeBook.hasGroup()) {
            serverRecipe.setGroup(recipeBook.getGroup());
        }

        return serverRecipe;
    }

    private boolean isSameCut(Recipe serverRecipe, StonecuttingRecipe<ItemStack> recipe) {
        org.bukkit.inventory.StonecuttingRecipe stonecutting = (org.bukkit.inventory.StonecuttingRecipe) serverRecipe;

        return stonecutting.getInputChoice().test(recipe.getInput())
                && stonecutting.getResult().getType() == recipe.getResult().getType();
    }
}