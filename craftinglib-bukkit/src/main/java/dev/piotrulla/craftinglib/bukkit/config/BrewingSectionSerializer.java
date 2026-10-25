package dev.piotrulla.craftinglib.bukkit.config;

import dev.piotrulla.craftinglib.bukkit.BukkitRecipes;
import dev.piotrulla.craftinglib.recipe.BrewingRecipe;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * {@link BrewingRecipe}:
 * <pre>
 * night-vision-plus:
 *   input: {item stack}
 *   ingredient: GLOW_BERRIES
 *   result: {item stack}
 *   match-mode: EXACT
 * </pre>
 */
final class BrewingSectionSerializer extends RecipeSectionSerializer<BrewingRecipe<ItemStack>> {

    private static final String INPUT = "input";
    private static final String INGREDIENT = "ingredient";

    @Override
    protected void writeFields(@NotNull BrewingRecipe<ItemStack> recipe, @NotNull ConfigurationSection section) {
        section.set(INPUT, recipe.getInput());
        section.set(INGREDIENT, recipe.getIngredient());
    }

    @NotNull
    @Override
    protected BrewingRecipe<ItemStack> readFields(@NotNull SectionReader reader) {
        return this.buildWithSettings(BukkitRecipes.brewing(reader.getKey())
                .withInput(reader.requireItem(INPUT))
                .withIngredient(reader.requireItem(INGREDIENT)), reader);
    }
}
