package dev.piotrulla.craftinglib.bukkit.config;

import dev.piotrulla.craftinglib.bukkit.BukkitRecipes;
import dev.piotrulla.craftinglib.bukkit.builder.BukkitSmeltingRecipeBuilder;
import dev.piotrulla.craftinglib.recipe.smelting.CookingType;
import dev.piotrulla.craftinglib.recipe.smelting.SmeltingRecipe;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * {@link SmeltingRecipe}:
 * <pre>
 * fast-iron:
 *   cooking-type: BLASTING   # optional, default FURNACE
 *   input: {item stack}
 *   experience: 0.7          # optional, default 0
 *   cooking-time: 50         # optional, default: vanilla time of the station
 *   result: {item stack}
 *   match-mode: EXACT
 * </pre>
 */
final class SmeltingSectionSerializer extends RecipeSectionSerializer<SmeltingRecipe<ItemStack>> {

    private static final String COOKING_TYPE = "cooking-type";
    private static final String INPUT = "input";
    private static final String EXPERIENCE = "experience";
    private static final String COOKING_TIME = "cooking-time";

    @Override
    protected void writeFields(@NotNull SmeltingRecipe<ItemStack> recipe, @NotNull ConfigurationSection section) {
        section.set(COOKING_TYPE, recipe.getCookingType().name());
        section.set(INPUT, recipe.getInput());
        // via text, so 0.7F is stored as 0.7 instead of 0.699999988
        section.set(EXPERIENCE, Double.parseDouble(Float.toString(recipe.getExperience())));
        section.set(COOKING_TIME, recipe.getCookingTime());
    }

    @NotNull
    @Override
    protected SmeltingRecipe<ItemStack> readFields(@NotNull SectionReader reader) {
        BukkitSmeltingRecipeBuilder builder = BukkitRecipes.smelting(reader.getKey())
                .withInput(reader.requireItem(INPUT));

        reader.findEnum(COOKING_TYPE, CookingType.class).ifPresent(builder::withCookingType);
        reader.findFloat(EXPERIENCE).ifPresent(builder::withExperience);
        reader.findInt(COOKING_TIME).ifPresent(builder::withCookingTime);

        return this.buildWithSettings(builder, reader);
    }
}
