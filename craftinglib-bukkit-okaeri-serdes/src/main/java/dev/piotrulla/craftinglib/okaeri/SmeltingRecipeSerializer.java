package dev.piotrulla.craftinglib.okaeri;

import dev.piotrulla.craftinglib.bukkit.BukkitRecipes;
import dev.piotrulla.craftinglib.bukkit.builder.BukkitSmeltingRecipeBuilder;
import dev.piotrulla.craftinglib.recipe.smelting.CookingType;
import dev.piotrulla.craftinglib.recipe.smelting.SmeltingRecipe;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.SerializationData;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Serializes {@link SmeltingRecipe}:
 * <pre>
 * key: fast-iron
 * cooking-type: BLASTING   # optional, default FURNACE
 * input: {item stack}
 * experience: 0.7          # optional, default 0
 * cooking-time: 50         # optional, default: vanilla time of the station
 * result: {item stack}
 * match-mode: EXACT
 * </pre>
 */
public final class SmeltingRecipeSerializer extends AbstractRecipeSerializer<SmeltingRecipe<ItemStack>> {

    private static final String COOKING_TYPE = "cooking-type";
    private static final String INPUT = "input";
    private static final String EXPERIENCE = "experience";
    private static final String COOKING_TIME = "cooking-time";

    @Override
    public boolean supports(@NotNull Class<?> type) {
        return SmeltingRecipe.class.isAssignableFrom(type);
    }

    @Override
    protected void serializeFields(@NotNull SmeltingRecipe<ItemStack> recipe, @NotNull SerializationData data) {
        data.set(COOKING_TYPE, recipe.getCookingType());
        data.set(INPUT, recipe.getInput(), ItemStack.class);
        data.set(EXPERIENCE, recipe.getExperience());
        data.set(COOKING_TIME, recipe.getCookingTime());
    }

    @NotNull
    @Override
    protected SmeltingRecipe<ItemStack> deserializeFields(@NotNull String key, @NotNull DeserializationData data) {
        BukkitSmeltingRecipeBuilder builder = BukkitRecipes.smelting(key)
                .withInput(this.require(data, key, INPUT, ItemStack.class));

        this.find(data, COOKING_TYPE, CookingType.class).ifPresent(builder::withCookingType);
        this.find(data, EXPERIENCE, Float.class).ifPresent(builder::withExperience);
        this.find(data, COOKING_TIME, Integer.class).ifPresent(builder::withCookingTime);

        return this.buildWithSettings(key, builder, data);
    }
}
