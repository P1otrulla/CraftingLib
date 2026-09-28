package dev.piotrulla.craftinglib.bukkit;

import dev.piotrulla.craftinglib.bukkit.builder.BukkitAnvilRecipeBuilder;
import dev.piotrulla.craftinglib.bukkit.builder.BukkitBrewingRecipeBuilder;
import dev.piotrulla.craftinglib.bukkit.builder.BukkitGrindstoneRecipeBuilder;
import dev.piotrulla.craftinglib.bukkit.builder.BukkitShapedRecipeBuilder;
import dev.piotrulla.craftinglib.bukkit.builder.BukkitShapelessRecipeBuilder;
import dev.piotrulla.craftinglib.bukkit.builder.BukkitSmeltingRecipeBuilder;
import dev.piotrulla.craftinglib.bukkit.builder.BukkitSmithingRecipeBuilder;
import dev.piotrulla.craftinglib.bukkit.builder.BukkitStonecuttingRecipeBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * Entry point for building Bukkit recipes. Keys must match {@code [a-z0-9/._-]}.
 */
public final class BukkitRecipes {

    private BukkitRecipes() {
    }

    @NotNull
    public static BukkitShapedRecipeBuilder shaped(@NotNull String key) {
        return new BukkitShapedRecipeBuilder(key);
    }

    @NotNull
    public static BukkitShapelessRecipeBuilder shapeless(@NotNull String key) {
        return new BukkitShapelessRecipeBuilder(key);
    }

    /**
     * Furnace, blast furnace, smoker or campfire recipe.
     */
    @NotNull
    public static BukkitSmeltingRecipeBuilder smelting(@NotNull String key) {
        return new BukkitSmeltingRecipeBuilder(key);
    }

    /**
     * Brewing stand recipe, Paper only.
     */
    @NotNull
    public static BukkitBrewingRecipeBuilder brewing(@NotNull String key) {
        return new BukkitBrewingRecipeBuilder(key);
    }

    /**
     * Stonecutter recipe, 1.14+.
     */
    @NotNull
    public static BukkitStonecuttingRecipeBuilder stonecutting(@NotNull String key) {
        return new BukkitStonecuttingRecipeBuilder(key);
    }

    /**
     * Smithing table recipe, 1.16+ (template required on 1.20+).
     */
    @NotNull
    public static BukkitSmithingRecipeBuilder smithing(@NotNull String key) {
        return new BukkitSmithingRecipeBuilder(key);
    }

    /**
     * Anvil recipe, 1.11+.
     */
    @NotNull
    public static BukkitAnvilRecipeBuilder anvil(@NotNull String key) {
        return new BukkitAnvilRecipeBuilder(key);
    }

    /**
     * Grindstone recipe, Spigot/Paper 1.19.3+.
     */
    @NotNull
    public static BukkitGrindstoneRecipeBuilder grindstone(@NotNull String key) {
        return new BukkitGrindstoneRecipeBuilder(key);
    }
}
