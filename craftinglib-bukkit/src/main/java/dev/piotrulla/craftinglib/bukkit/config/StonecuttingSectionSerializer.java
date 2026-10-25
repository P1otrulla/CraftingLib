package dev.piotrulla.craftinglib.bukkit.config;

import dev.piotrulla.craftinglib.bukkit.BukkitRecipes;
import dev.piotrulla.craftinglib.recipe.StonecuttingRecipe;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * {@link StonecuttingRecipe}:
 * <pre>
 * cheap-bricks:
 *   input: STONE
 *   result: {item stack}
 *   match-mode: EXACT
 * </pre>
 */
final class StonecuttingSectionSerializer extends RecipeSectionSerializer<StonecuttingRecipe<ItemStack>> {

    private static final String INPUT = "input";

    @Override
    protected void writeFields(@NotNull StonecuttingRecipe<ItemStack> recipe, @NotNull ConfigurationSection section) {
        section.set(INPUT, recipe.getInput());
    }

    @NotNull
    @Override
    protected StonecuttingRecipe<ItemStack> readFields(@NotNull SectionReader reader) {
        return this.buildWithSettings(BukkitRecipes.stonecutting(reader.getKey())
                .withInput(reader.requireItem(INPUT)), reader);
    }
}
