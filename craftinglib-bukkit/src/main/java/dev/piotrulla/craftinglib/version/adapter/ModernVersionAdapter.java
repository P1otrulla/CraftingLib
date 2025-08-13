package dev.piotrulla.craftinglib.version.adapter;

import dev.piotrulla.craftinglib.version.VersionAdapter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * Version adapter for modern Minecraft versions (1.13+).
 */
public class ModernVersionAdapter implements VersionAdapter {

    private final Plugin plugin;
    private final String version;

    public ModernVersionAdapter(@NotNull Plugin plugin, @NotNull String version) {
        this.plugin = plugin;
        this.version = version;
    }

    @Override
    @NotNull
    public ShapedRecipe createShapedRecipe(@NotNull ItemStack result, @NotNull String key) {
        NamespacedKey namespacedKey = new NamespacedKey(this.plugin, sanitizeKey(key));
        return new ShapedRecipe(namespacedKey, result);
    }

    @Override
    public void setIngredient(@NotNull ShapedRecipe recipe, char symbol, @NotNull ItemStack ingredient) {
        if (ingredient.getType() == Material.AIR) {
            return;
        }
        recipe.setIngredient(symbol, new RecipeChoice.ExactChoice(ingredient));
    }

    @Override
    @NotNull
    public String getVersion() {
        return this.version;
    }

    private String sanitizeKey(String key) {
        return key.toLowerCase()
                .replaceAll("[^a-z0-9_.-/]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");
    }
}