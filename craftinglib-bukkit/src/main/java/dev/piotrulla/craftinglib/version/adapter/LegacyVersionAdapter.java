package dev.piotrulla.craftinglib.version.adapter;

import dev.piotrulla.craftinglib.version.VersionAdapter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

/**
 * Version adapter for legacy Minecraft versions (1.8-1.11).
 */
public class LegacyVersionAdapter implements VersionAdapter {

    private final String version;

    public LegacyVersionAdapter(@NotNull String version) {
        this.version = version;
    }

    @Override
    @NotNull
    @Deprecated
    public ShapedRecipe createShapedRecipe(@NotNull ItemStack result, @NotNull String key) {
        return new ShapedRecipe(result);
    }

    @Override
    public void setIngredient(@NotNull ShapedRecipe recipe, char symbol, @NotNull ItemStack ingredient) {
        recipe.setIngredient(symbol, ingredient.getData());
    }

    @Override
    @NotNull
    public String getVersion() {
        return this.version;
    }
}