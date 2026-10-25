package dev.piotrulla.craftinglib.bukkit.config;

import dev.piotrulla.craftinglib.bukkit.BukkitRecipes;
import dev.piotrulla.craftinglib.bukkit.builder.BukkitGrindstoneRecipeBuilder;
import dev.piotrulla.craftinglib.recipe.GrindstoneRecipe;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * {@link GrindstoneRecipe}:
 * <pre>
 * scrap-sword:
 *   input: DIAMOND_SWORD
 *   second-input: IRON_SWORD # optional
 *   experience: 10           # optional, default 0
 *   result: {item stack}
 *   match-mode: EXACT
 * </pre>
 */
final class GrindstoneSectionSerializer extends RecipeSectionSerializer<GrindstoneRecipe<ItemStack>> {

    private static final String INPUT = "input";
    private static final String SECOND_INPUT = "second-input";
    private static final String EXPERIENCE = "experience";

    @Override
    protected void writeFields(@NotNull GrindstoneRecipe<ItemStack> recipe, @NotNull ConfigurationSection section) {
        section.set(INPUT, recipe.getInput());
        this.setOptionalItem(section, SECOND_INPUT, recipe.getSecondInput());
        section.set(EXPERIENCE, recipe.getExperience());
    }

    @NotNull
    @Override
    protected GrindstoneRecipe<ItemStack> readFields(@NotNull SectionReader reader) {
        BukkitGrindstoneRecipeBuilder builder = BukkitRecipes.grindstone(reader.getKey())
                .withInput(reader.requireItem(INPUT));

        reader.findItem(SECOND_INPUT).ifPresent(builder::withSecondInput);
        reader.findInt(EXPERIENCE).ifPresent(builder::withExperience);

        return this.buildWithSettings(builder, reader);
    }
}
