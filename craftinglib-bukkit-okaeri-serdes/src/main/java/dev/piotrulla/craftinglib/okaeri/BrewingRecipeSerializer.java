package dev.piotrulla.craftinglib.okaeri;

import dev.piotrulla.craftinglib.bukkit.BukkitRecipes;
import dev.piotrulla.craftinglib.recipe.BrewingRecipe;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.SerializationData;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Serializes {@link BrewingRecipe}:
 * <pre>
 * key: night-vision-plus
 * input: {item stack}
 * ingredient: {item stack}
 * result: {item stack}
 * match-mode: EXACT
 * </pre>
 */
public final class BrewingRecipeSerializer extends AbstractRecipeSerializer<BrewingRecipe<ItemStack>> {

    private static final String INPUT = "input";
    private static final String INGREDIENT = "ingredient";

    @Override
    public boolean supports(@NotNull Class<?> type) {
        return BrewingRecipe.class.isAssignableFrom(type);
    }

    @Override
    protected void serializeFields(@NotNull BrewingRecipe<ItemStack> recipe, @NotNull SerializationData data) {
        data.set(INPUT, recipe.getInput(), ItemStack.class);
        data.set(INGREDIENT, recipe.getIngredient(), ItemStack.class);
    }

    @NotNull
    @Override
    protected BrewingRecipe<ItemStack> deserializeFields(@NotNull String key, @NotNull DeserializationData data) {
        return this.buildWithSettings(key, BukkitRecipes.brewing(key)
                .withInput(this.require(data, key, INPUT, ItemStack.class))
                .withIngredient(this.require(data, key, INGREDIENT, ItemStack.class)), data);
    }
}
