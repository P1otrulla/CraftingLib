package dev.piotrulla.craftinglib.bukkit.registry;

import dev.piotrulla.craftinglib.CraftingException;
import dev.piotrulla.craftinglib.bukkit.version.VersionAdapter;
import dev.piotrulla.craftinglib.recipe.manager.PlatformRecipeRegistrar;
import dev.piotrulla.craftinglib.recipe.CraftingRecipe;
import dev.piotrulla.craftinglib.recipe.ShapedCraftingRecipe;
import dev.piotrulla.craftinglib.recipe.ShapelessCraftingRecipe;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Registers crafting recipes as material-level server recipes, so the server fires crafting events.
 */
public final class BukkitRecipeRegistrar implements PlatformRecipeRegistrar<CraftingRecipe<ItemStack>> {

    private final VersionAdapter versionAdapter;
    private final ServerRecipeRegistry serverRecipes;

    public BukkitRecipeRegistrar(@NotNull RegistrarContext context) {
        Objects.requireNonNull(context, "context");
        this.versionAdapter = context.getVersionAdapter();
        this.serverRecipes = new ServerRecipeRegistry(context, BukkitRecipeRegistrar::isCraftingRecipe);
    }

    private static boolean isCraftingRecipe(Recipe serverRecipe) {
        return serverRecipe instanceof ShapedRecipe || serverRecipe instanceof ShapelessRecipe;
    }

    @Override
    public void register(@NotNull CraftingRecipe<ItemStack> recipe) {
        Objects.requireNonNull(recipe, "recipe");

        if (recipe.shouldReplaceVanilla()) {
            Material resultType = recipe.getResult().getType();
            this.serverRecipes.removeForeign(serverRecipe -> serverRecipe.getResult().getType() == resultType);
        }

        this.serverRecipes.add(recipe.getKey(), this.toServerRecipe(recipe), recipe.getRecipeBook().isDiscoverable());
    }

    @Override
    public void unregister(@NotNull CraftingRecipe<ItemStack> recipe) {
        Objects.requireNonNull(recipe, "recipe");
        this.serverRecipes.remove(recipe.getKey());
    }

    /**
     * @return true when the server recipe was registered by this registrar
     */
    public boolean isCustomRecipe(@Nullable Recipe serverRecipe) {
        return this.serverRecipes.isOwned(serverRecipe);
    }

    private Recipe toServerRecipe(CraftingRecipe<ItemStack> recipe) {
        if (recipe instanceof ShapedCraftingRecipe) {
            return this.toServerRecipe((ShapedCraftingRecipe<ItemStack>) recipe);
        }
        if (recipe instanceof ShapelessCraftingRecipe) {
            return this.toServerRecipe((ShapelessCraftingRecipe<ItemStack>) recipe);
        }

        throw new CraftingException("Unsupported recipe implementation: " + recipe.getClass().getName());
    }

    private Recipe toServerRecipe(ShapedCraftingRecipe<ItemStack> recipe) {
        ShapedRecipe serverRecipe = this.versionAdapter.createShapedRecipe(recipe.getKey(), recipe.getResult(), recipe.getRecipeBook());
        serverRecipe.shape(recipe.getPattern().getRows().toArray(new String[0]));
        recipe.getIngredients().forEach((symbol, ingredient) ->
                this.versionAdapter.setIngredient(serverRecipe, symbol, ingredient));

        return serverRecipe;
    }

    private Recipe toServerRecipe(ShapelessCraftingRecipe<ItemStack> recipe) {
        ShapelessRecipe serverRecipe = this.versionAdapter.createShapelessRecipe(recipe.getKey(), recipe.getResult(), recipe.getRecipeBook());
        for (ItemStack ingredient : recipe.getIngredients()) {
            this.versionAdapter.addIngredient(serverRecipe, ingredient);
        }

        return serverRecipe;
    }
}