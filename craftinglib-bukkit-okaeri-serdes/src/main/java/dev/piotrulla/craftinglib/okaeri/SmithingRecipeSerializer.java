package dev.piotrulla.craftinglib.okaeri;

import dev.piotrulla.craftinglib.bukkit.BukkitRecipes;
import dev.piotrulla.craftinglib.bukkit.builder.BukkitSmithingRecipeBuilder;
import dev.piotrulla.craftinglib.recipe.SmithingRecipe;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.SerializationData;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Serializes {@link SmithingRecipe}:
 * <pre>
 * key: mithril-sword
 * template: {item stack}   # required on 1.20+, must be absent on 1.16-1.19
 * base: {item stack}
 * addition: {item stack}
 * result: {item stack}
 * match-mode: EXACT
 * </pre>
 */
public final class SmithingRecipeSerializer extends AbstractRecipeSerializer<SmithingRecipe<ItemStack>> {

    private static final String TEMPLATE = "template";
    private static final String BASE = "base";
    private static final String ADDITION = "addition";

    @Override
    public boolean supports(@NotNull Class<?> type) {
        return SmithingRecipe.class.isAssignableFrom(type);
    }

    @Override
    protected void serializeFields(@NotNull SmithingRecipe<ItemStack> recipe, @NotNull SerializationData data) {
        this.setOptionalItem(data, TEMPLATE, recipe.getTemplate());
        data.set(BASE, recipe.getBase(), ItemStack.class);
        data.set(ADDITION, recipe.getAddition(), ItemStack.class);
    }

    @NotNull
    @Override
    protected SmithingRecipe<ItemStack> deserializeFields(@NotNull String key, @NotNull DeserializationData data) {
        BukkitSmithingRecipeBuilder builder = BukkitRecipes.smithing(key)
                .withBase(this.require(data, key, BASE, ItemStack.class))
                .withAddition(this.require(data, key, ADDITION, ItemStack.class));

        this.find(data, TEMPLATE, ItemStack.class).ifPresent(builder::withTemplate);

        return this.buildWithSettings(key, builder, data);
    }
}
