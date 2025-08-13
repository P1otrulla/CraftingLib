package dev.piotrulla.craftinglib.version.adapter;

import dev.piotrulla.craftinglib.version.VersionAdapter;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * Version adapter for Minecraft 1.12.
 */
public class NamespacedVersionAdapter implements VersionAdapter {

    private final Plugin plugin;
    private final String version;

    public NamespacedVersionAdapter(@NotNull Plugin plugin, @NotNull String version) {
        this.plugin = plugin;
        this.version = version;
    }

    @Override
    @NotNull
    public ShapedRecipe createShapedRecipe(@NotNull ItemStack result, @NotNull String key) {
        NamespacedKey namespacedKey = new NamespacedKey(this.plugin, this.sanitizeKey(key));
        return new ShapedRecipe(namespacedKey, result);
    }

    @Override
    public void setIngredient(@NotNull ShapedRecipe recipe, char symbol, @NotNull ItemStack ingredient) {
        //TODO: TEST IT
//        if (ingredient.getType() == Material.AIR) {
//            return;
//        }
        recipe.setIngredient(symbol, ingredient.getData());
    }

    @Override
    @NotNull
    public String getVersion() {
        return version;
    }

    private String sanitizeKey(String key) {
        return key.toLowerCase()
                .replaceAll("[^a-z0-9_.-/]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");
    }
}