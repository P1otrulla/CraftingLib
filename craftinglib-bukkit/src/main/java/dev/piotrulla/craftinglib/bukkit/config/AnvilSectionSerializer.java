package dev.piotrulla.craftinglib.bukkit.config;

import dev.piotrulla.craftinglib.bukkit.BukkitRecipes;
import dev.piotrulla.craftinglib.bukkit.builder.BukkitAnvilRecipeBuilder;
import dev.piotrulla.craftinglib.recipe.AnvilRecipe;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * {@link AnvilRecipe}:
 * <pre>
 * sharpen-sword:
 *   left: IRON_SWORD
 *   right: {item stack}
 *   level-cost: 5            # optional, default 1
 *   result: {item stack}
 *   match-mode: EXACT
 * </pre>
 */
final class AnvilSectionSerializer extends RecipeSectionSerializer<AnvilRecipe<ItemStack>> {

    private static final String LEFT = "left";
    private static final String RIGHT = "right";
    private static final String LEVEL_COST = "level-cost";

    @Override
    protected void writeFields(@NotNull AnvilRecipe<ItemStack> recipe, @NotNull ConfigurationSection section) {
        section.set(LEFT, recipe.getLeft());
        section.set(RIGHT, recipe.getRight());
        section.set(LEVEL_COST, recipe.getLevelCost());
    }

    @NotNull
    @Override
    protected AnvilRecipe<ItemStack> readFields(@NotNull SectionReader reader) {
        BukkitAnvilRecipeBuilder builder = BukkitRecipes.anvil(reader.getKey())
                .withLeft(reader.requireItem(LEFT))
                .withRight(reader.requireItem(RIGHT));

        reader.findInt(LEVEL_COST).ifPresent(builder::withLevelCost);

        return this.buildWithSettings(builder, reader);
    }
}
