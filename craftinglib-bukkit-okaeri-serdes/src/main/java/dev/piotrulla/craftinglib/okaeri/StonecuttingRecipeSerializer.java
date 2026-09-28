package dev.piotrulla.craftinglib.okaeri;

import dev.piotrulla.craftinglib.bukkit.BukkitRecipes;
import dev.piotrulla.craftinglib.recipe.StonecuttingRecipe;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.SerializationData;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Serializes {@link StonecuttingRecipe}:
 * <pre>
 * key: cheap-bricks
 * input: {item stack}
 * result: {item stack}
 * match-mode: EXACT
 * </pre>
 */
public final class StonecuttingRecipeSerializer extends AbstractRecipeSerializer<StonecuttingRecipe<ItemStack>> {

    private static final String INPUT = "input";

    @Override
    public boolean supports(@NotNull Class<?> type) {
        return StonecuttingRecipe.class.isAssignableFrom(type);
    }

    @Override
    protected void serializeFields(@NotNull StonecuttingRecipe<ItemStack> recipe, @NotNull SerializationData data) {
        data.set(INPUT, recipe.getInput(), ItemStack.class);
    }

    @NotNull
    @Override
    protected StonecuttingRecipe<ItemStack> deserializeFields(@NotNull String key, @NotNull DeserializationData data) {
        return this.buildWithSettings(key, BukkitRecipes.stonecutting(key)
                .withInput(this.require(data, key, INPUT, ItemStack.class)), data);
    }
}
