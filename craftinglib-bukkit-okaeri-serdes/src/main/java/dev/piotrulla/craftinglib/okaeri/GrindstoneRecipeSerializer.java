package dev.piotrulla.craftinglib.okaeri;

import dev.piotrulla.craftinglib.bukkit.BukkitRecipes;
import dev.piotrulla.craftinglib.bukkit.builder.BukkitGrindstoneRecipeBuilder;
import dev.piotrulla.craftinglib.recipe.GrindstoneRecipe;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.SerializationData;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Serializes {@link GrindstoneRecipe}:
 * <pre>
 * key: scrap-sword
 * input: {item stack}
 * second-input: {item stack}   # optional, other slot must be empty when absent
 * experience: 3                # optional, default 0
 * result: {item stack}
 * match-mode: EXACT
 * </pre>
 */
public final class GrindstoneRecipeSerializer extends AbstractRecipeSerializer<GrindstoneRecipe<ItemStack>> {

    private static final String INPUT = "input";
    private static final String SECOND_INPUT = "second-input";
    private static final String EXPERIENCE = "experience";

    @Override
    public boolean supports(@NotNull Class<?> type) {
        return GrindstoneRecipe.class.isAssignableFrom(type);
    }

    @Override
    protected void serializeFields(@NotNull GrindstoneRecipe<ItemStack> recipe, @NotNull SerializationData data) {
        data.set(INPUT, recipe.getInput(), ItemStack.class);
        this.setOptionalItem(data, SECOND_INPUT, recipe.getSecondInput());
        data.set(EXPERIENCE, recipe.getExperience());
    }

    @NotNull
    @Override
    protected GrindstoneRecipe<ItemStack> deserializeFields(@NotNull String key, @NotNull DeserializationData data) {
        BukkitGrindstoneRecipeBuilder builder = BukkitRecipes.grindstone(key)
                .withInput(this.require(data, key, INPUT, ItemStack.class));

        this.find(data, SECOND_INPUT, ItemStack.class).ifPresent(builder::withSecondInput);
        this.find(data, EXPERIENCE, Integer.class).ifPresent(builder::withExperience);

        return this.buildWithSettings(key, builder, data);
    }
}
