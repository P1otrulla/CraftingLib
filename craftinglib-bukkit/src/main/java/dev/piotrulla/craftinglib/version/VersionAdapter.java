package dev.piotrulla.craftinglib.version;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

/**
 * Version-specific adapter for different Minecraft versions.
 */
public interface VersionAdapter {

    /**
     * Creates a shaped recipe with the given result and key.
     */
    @NotNull
    ShapedRecipe createShapedRecipe(@NotNull ItemStack result, @NotNull String key);

    /**
     * Sets an ingredient in the shaped recipe.
     */
    void setIngredient(@NotNull ShapedRecipe recipe, char symbol, @NotNull ItemStack ingredient);

    /**
     * Gets the version string this adapter supports.
     */
    @NotNull
    String getVersion();
}
