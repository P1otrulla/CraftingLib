package dev.piotrulla.craftinglib.okaeri;

import dev.piotrulla.craftinglib.CraftingException;
import dev.piotrulla.craftinglib.bukkit.BukkitRecipes;
import dev.piotrulla.craftinglib.bukkit.builder.BukkitShapedRecipeBuilder;
import dev.piotrulla.craftinglib.bukkit.builder.BukkitShapelessRecipeBuilder;
import dev.piotrulla.craftinglib.recipe.CraftingRecipe;
import dev.piotrulla.craftinglib.recipe.RecipeType;
import dev.piotrulla.craftinglib.recipe.ShapedCraftingRecipe;
import dev.piotrulla.craftinglib.recipe.ShapelessCraftingRecipe;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.SerializationData;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Serializes {@link CraftingRecipe} (shaped and shapeless):
 * <pre>
 * key: super-pickaxe
 * type: SHAPED
 * pattern: ["DDD", " S ", " S "]
 * ingredients:
 *   D: {item stack}
 *   S: {item stack}
 * mirrored: true
 * result: {item stack}
 * match-mode: EXACT
 * </pre>
 * Shapeless recipes have {@code ingredients} as a list and no pattern. Shared settings: {@link AbstractRecipeSerializer}.
 */
public final class CraftingRecipeSerializer extends AbstractRecipeSerializer<CraftingRecipe<ItemStack>> {

    private static final String TYPE = "type";
    private static final String PATTERN = "pattern";
    private static final String INGREDIENTS = "ingredients";
    private static final String MIRRORED = "mirrored";
    private static final int SYMBOL_LENGTH = 1;

    @Override
    public boolean supports(@NotNull Class<?> type) {
        return CraftingRecipe.class.isAssignableFrom(type);
    }

    @Override
    protected void serializeFields(@NotNull CraftingRecipe<ItemStack> recipe, @NotNull SerializationData data) {
        data.set(TYPE, recipe.getType());

        if (recipe instanceof ShapedCraftingRecipe) {
            this.serializeShaped((ShapedCraftingRecipe<ItemStack>) recipe, data);
        }
        else if (recipe instanceof ShapelessCraftingRecipe) {
            this.serializeShapeless((ShapelessCraftingRecipe<ItemStack>) recipe, data);
        }
        else {
            throw new CraftingException("Unsupported recipe implementation: " + recipe.getClass().getName());
        }
    }

    @NotNull
    @Override
    protected CraftingRecipe<ItemStack> deserializeFields(@NotNull String key, @NotNull DeserializationData data) {
        RecipeType type = this.require(data, key, TYPE, RecipeType.class);

        switch (type) {
            case SHAPED:
                return this.deserializeShaped(key, data);
            case SHAPELESS:
                return this.deserializeShapeless(key, data);
            default:
                throw new CraftingException("Recipe '" + key + "': unsupported type " + type);
        }
    }

    private void serializeShaped(ShapedCraftingRecipe<ItemStack> recipe, SerializationData data) {
        Map<String, ItemStack> ingredients = new LinkedHashMap<>();
        recipe.getIngredients().forEach((symbol, ingredient) -> ingredients.put(String.valueOf(symbol), ingredient));

        data.setCollection(PATTERN, recipe.getPattern().getRows(), String.class);
        data.setMap(INGREDIENTS, ingredients, String.class, ItemStack.class);
        data.set(MIRRORED, recipe.isMirrored());
    }

    private void serializeShapeless(ShapelessCraftingRecipe<ItemStack> recipe, SerializationData data) {
        data.setCollection(INGREDIENTS, recipe.getIngredients(), ItemStack.class);
    }

    private ShapedCraftingRecipe<ItemStack> deserializeShaped(String key, DeserializationData data) {
        this.requirePresent(data, key, PATTERN);
        this.requirePresent(data, key, INGREDIENTS);

        List<String> pattern = data.getAsList(PATTERN, String.class);
        Map<String, ItemStack> ingredients = data.getAsMap(INGREDIENTS, String.class, ItemStack.class);

        BukkitShapedRecipeBuilder builder = BukkitRecipes.shaped(key).withPattern(pattern.toArray(new String[0]));
        ingredients.forEach((symbol, ingredient) -> builder.withIngredient(this.toSymbol(key, symbol), ingredient));
        this.find(data, MIRRORED, Boolean.class).ifPresent(builder::withMirroring);

        return this.buildWithSettings(key, builder, data);
    }

    private ShapelessCraftingRecipe<ItemStack> deserializeShapeless(String key, DeserializationData data) {
        this.requirePresent(data, key, INGREDIENTS);

        BukkitShapelessRecipeBuilder builder = BukkitRecipes.shapeless(key);
        for (ItemStack ingredient : data.getAsList(INGREDIENTS, ItemStack.class)) {
            builder.withIngredient(ingredient);
        }

        return this.buildWithSettings(key, builder, data);
    }

    private char toSymbol(String key, String symbol) {
        if (symbol == null || symbol.length() != SYMBOL_LENGTH) {
            throw new CraftingException("Recipe '" + key + "': ingredient symbol must be a single character, got: '" + symbol + "'");
        }

        return symbol.charAt(0);
    }
}
