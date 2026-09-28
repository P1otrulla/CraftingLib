package dev.piotrulla.craftinglib.bukkit.version.adapter;

import dev.piotrulla.craftinglib.recipe.book.RecipeBookSettings;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * 1.12: crafting recipe keys ({@link NamespacedKey}). Ingredients and furnace recipes are inherited from legacy
 * (1.12 furnace recipes have no key).
 */
public class NamespacedVersionAdapter extends LegacyVersionAdapter {

    private static final String NAMESPACE_PROBE_KEY = "craftinglib";

    private final Plugin plugin;
    private final String namespace;

    public NamespacedVersionAdapter(@NotNull Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.namespace = new NamespacedKey(plugin, NAMESPACE_PROBE_KEY).getNamespace();
    }

    @NotNull
    @Override
    public ShapedRecipe createShapedRecipe(@NotNull String key, @NotNull ItemStack result, @NotNull RecipeBookSettings recipeBook) {
        return new ShapedRecipe(this.createKey(key), result);
    }

    @NotNull
    @Override
    public ShapelessRecipe createShapelessRecipe(@NotNull String key, @NotNull ItemStack result, @NotNull RecipeBookSettings recipeBook) {
        return new ShapelessRecipe(this.createKey(key), result);
    }

    @Override
    public boolean isKeyed(@NotNull Recipe serverRecipe) {
        return serverRecipe instanceof Keyed;
    }

    @Nullable
    @Override
    public String getOwnedKey(@NotNull Recipe serverRecipe) {
        if (!(serverRecipe instanceof Keyed)) {
            return null;
        }

        NamespacedKey key = ((Keyed) serverRecipe).getKey();
        return this.isOwnedNamespace(key.getNamespace()) ? key.getKey() : null;
    }

    @Override
    public boolean isOwnedNamespace(@NotNull String namespace) {
        return this.namespace.equals(namespace);
    }

    @NotNull
    @Override
    public String getName() {
        return "namespaced (1.12)";
    }

    @NotNull
    protected NamespacedKey createKey(@NotNull String key) {
        return new NamespacedKey(this.plugin, key);
    }
}