package dev.piotrulla.craftinglib.bukkit.registry;

import dev.piotrulla.craftinglib.bukkit.version.VersionAdapter;
import dev.piotrulla.craftinglib.recipe.manager.PlatformRecipeRegistrar;
import dev.piotrulla.craftinglib.recipe.smelting.SmeltingRecipe;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Registers smelting recipes as native server cooking recipes. No listener is involved:
 * on 1.14+ the server itself handles match mode, experience and cooking time.
 */
public final class BukkitSmeltingRecipeRegistrar implements PlatformRecipeRegistrar<SmeltingRecipe<ItemStack>> {

    private final VersionAdapter versionAdapter;
    private final ServerRecipeRegistry serverRecipes;

    public BukkitSmeltingRecipeRegistrar(@NotNull RegistrarContext context) {
        Objects.requireNonNull(context, "context");
        this.versionAdapter = context.getVersionAdapter();
        this.serverRecipes = new ServerRecipeRegistry(context, this.versionAdapter::isCookingRecipe);
    }

    @Override
    public void register(@NotNull SmeltingRecipe<ItemStack> recipe) {
        Objects.requireNonNull(recipe, "recipe");

        if (recipe.shouldReplaceVanilla()) {
            this.serverRecipes.removeForeign(serverRecipe -> this.versionAdapter.isCookingRecipeFor(serverRecipe, recipe));
        }

        this.serverRecipes.add(recipe.getKey(), this.versionAdapter.createCookingRecipe(recipe),
                recipe.getRecipeBook().isDiscoverable());
    }

    @Override
    public void unregister(@NotNull SmeltingRecipe<ItemStack> recipe) {
        Objects.requireNonNull(recipe, "recipe");
        this.serverRecipes.remove(recipe.getKey());
    }
}