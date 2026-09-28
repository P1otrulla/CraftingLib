package dev.piotrulla.craftinglib.okaeri;

import dev.piotrulla.craftinglib.bukkit.BukkitRecipes;
import dev.piotrulla.craftinglib.bukkit.builder.BukkitAnvilRecipeBuilder;
import dev.piotrulla.craftinglib.recipe.AnvilRecipe;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.SerializationData;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Serializes {@link AnvilRecipe}:
 * <pre>
 * key: sharpen-sword
 * left: {item stack}
 * right: {item stack}
 * level-cost: 5            # optional, default 1
 * result: {item stack}
 * match-mode: EXACT
 * </pre>
 */
public final class AnvilRecipeSerializer extends AbstractRecipeSerializer<AnvilRecipe<ItemStack>> {

    private static final String LEFT = "left";
    private static final String RIGHT = "right";
    private static final String LEVEL_COST = "level-cost";

    @Override
    public boolean supports(@NotNull Class<?> type) {
        return AnvilRecipe.class.isAssignableFrom(type);
    }

    @Override
    protected void serializeFields(@NotNull AnvilRecipe<ItemStack> recipe, @NotNull SerializationData data) {
        data.set(LEFT, recipe.getLeft(), ItemStack.class);
        data.set(RIGHT, recipe.getRight(), ItemStack.class);
        data.set(LEVEL_COST, recipe.getLevelCost());
    }

    @NotNull
    @Override
    protected AnvilRecipe<ItemStack> deserializeFields(@NotNull String key, @NotNull DeserializationData data) {
        BukkitAnvilRecipeBuilder builder = BukkitRecipes.anvil(key)
                .withLeft(this.require(data, key, LEFT, ItemStack.class))
                .withRight(this.require(data, key, RIGHT, ItemStack.class));

        this.find(data, LEVEL_COST, Integer.class).ifPresent(builder::withLevelCost);

        return this.buildWithSettings(key, builder, data);
    }
}
