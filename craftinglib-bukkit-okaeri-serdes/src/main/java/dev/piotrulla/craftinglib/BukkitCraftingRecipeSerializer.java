package dev.piotrulla.craftinglib;

import dev.piotrulla.craftinglib.builder.BukkitCraftingBuilder;
import dev.piotrulla.craftinglib.pattern.BukkitCraftingPattern;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BukkitCraftingRecipeSerializer implements ObjectSerializer<BukkitCraftingRecipe> {

    private static final String ID_KEY = "id";
    private static final String NAME_KEY = "name";
    private static final String TYPE_KEY = "type";
    private static final String PATTERN_KEY = "pattern";
    private static final String INGREDIENTS_KEY = "ingredients";
    private static final String RESULT_KEY = "result";
    private static final String AMOUNT_KEY = "amount";
    private static final String EXACT_MATCH_KEY = "exact-match";
    private static final String REPLACE_VANILLA_KEY = "replace-vanilla";

    @Override
    public boolean supports(@NotNull Class<? super BukkitCraftingRecipe> type) {
        return BukkitCraftingRecipe.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(
            @NotNull BukkitCraftingRecipe recipe,
            @NotNull SerializationData data,
            @NotNull GenericsDeclaration generics
    ) {
        data.add(ID_KEY, recipe.id());
        data.add(NAME_KEY, recipe.name());
        data.add(TYPE_KEY, recipe.getInventoryType().name());

        data.add(RESULT_KEY, recipe.result().item(), ItemStack.class);

        if (recipe.result().amount() > 1) {
            data.add(AMOUNT_KEY, recipe.result().amount());
        }

        if (recipe.getInventoryType() == BukkitCraftingRecipe.InventoryType.CRAFTING_TABLE_3X3) {
            this.serializeShapedRecipe(recipe, data);
        }
        else {
            this.serializeShapelessRecipe(recipe, data);
        }

        if (recipe.requiresExactMatch()) {
            data.add(EXACT_MATCH_KEY, true);
        }

        if (recipe.shouldReplaceVanilla()) {
            data.add(REPLACE_VANILLA_KEY, true);
        }
    }

    @Override
    public BukkitCraftingRecipe deserialize(
            @NotNull DeserializationData data,
            @NotNull GenericsDeclaration generics
    ) {
        String id = data.get(ID_KEY, String.class);
        String name = data.get(NAME_KEY, String.class);
        String typeStr = data.get(TYPE_KEY, String.class);
        ItemStack result = data.get(RESULT_KEY, ItemStack.class);

        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Recipe ID cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Recipe name cannot be null or empty");
        }
        if (typeStr == null) {
            throw new IllegalArgumentException("Recipe type cannot be null");
        }
        if (result == null) {
            throw new IllegalArgumentException("Recipe result cannot be null");
        }

        BukkitCraftingRecipe.InventoryType type;
        try {
            type = BukkitCraftingRecipe.InventoryType.valueOf(typeStr.toUpperCase());
        }
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid recipe type: " + typeStr);
        }

        int amount = data.get(AMOUNT_KEY, Integer.class);
        boolean exactMatch = data.get(EXACT_MATCH_KEY, Boolean.class);
        boolean replaceVanilla = data.get(REPLACE_VANILLA_KEY, Boolean.class);

        ItemStack cloned = result.clone();
        cloned.setAmount(amount);

        BukkitCraftingBuilder builder = BukkitCraftingBuilder.create(name)
                .withResult(cloned)
                .requireExactMatch(exactMatch)
                .replaceVanillaRecipes(replaceVanilla);

        if (type == BukkitCraftingRecipe.InventoryType.CRAFTING_TABLE_3X3) {
            this.deserializeShapedRecipe(data, builder);
        }
        else {
            this.deserializeShapelessRecipe(data, builder);
        }

        return builder.build();
    }

    private void serializeShapedRecipe(
            @NotNull BukkitCraftingRecipe recipe,
            @NotNull SerializationData data
    ) {
        BukkitCraftingPattern recipePattern = recipe.getPattern();
        List<String> pattern = recipePattern.rows();
        if (pattern != null && !pattern.isEmpty()) {
            data.add(PATTERN_KEY, pattern);
        }

        // Ingredients mapping
        Map<Character, ItemStack> ingredientMap = recipePattern.getIngredientMap();
        if (ingredientMap != null && !ingredientMap.isEmpty()) {
            Map<String, ItemStack> stringKeyMap = new LinkedHashMap<>();
            for (Map.Entry<Character, ItemStack> entry : ingredientMap.entrySet()) {
                stringKeyMap.put(String.valueOf(entry.getKey()), entry.getValue());
            }
            data.add(INGREDIENTS_KEY, stringKeyMap);
        }
    }

    private void serializeShapelessRecipe(@NotNull BukkitCraftingRecipe recipe,
                                          @NotNull SerializationData data) {
        List<CraftingRecipeIngredient<ItemStack>> ingredients = recipe.getPattern().getIngredients();
        if (ingredients != null && !ingredients.isEmpty()) {
            List<ItemStack> items = new ArrayList<>();
            for (CraftingRecipeIngredient<ItemStack> ingredient : ingredients) {
                items.add(ingredient.item());
            }
            data.add(INGREDIENTS_KEY, items);
        }
    }

    private void deserializeShapedRecipe(@NotNull DeserializationData data,
                                         @NotNull BukkitCraftingBuilder builder) {
        // Pattern
        List<String> pattern = data.getAsList(PATTERN_KEY, String.class);
        if (pattern == null || pattern.isEmpty()) {
            throw new IllegalArgumentException("Shaped recipe must have a pattern");
        }

        // Validate pattern dimensions
        int rows = pattern.size();
        if (rows < 1 || rows > 3) {
            throw new IllegalArgumentException("Pattern must have 1-3 rows, got: " + rows);
        }

        int expectedWidth = -1;
        for (int i = 0; i < pattern.size(); i++) {
            String row = pattern.get(i);
            if (row == null) {
                throw new IllegalArgumentException("Pattern row " + (i + 1) + " cannot be null");
            }

            if (expectedWidth == -1) {
                expectedWidth = row.length();
                if (expectedWidth < 1 || expectedWidth > 3) {
                    throw new IllegalArgumentException("Pattern width must be 1-3, got: " + expectedWidth);
                }
            } else if (row.length() != expectedWidth) {
                throw new IllegalArgumentException("All pattern rows must have same width. " +
                        "Expected: " + expectedWidth + ", got: " + row.length() + " at row " + (i + 1));
            }
        }

        builder.withPattern(pattern.toArray(new String[0]));

        // Ingredients
        Map<String, ItemStack> ingredients = data.getAsMap(INGREDIENTS_KEY, String.class, ItemStack.class);
        if (ingredients != null) {
            for (Map.Entry<String, ItemStack> entry : ingredients.entrySet()) {
                String key = entry.getKey();
                if (key.length() != 1) {
                    throw new IllegalArgumentException("Ingredient key must be single character: " + key);
                }

                char symbol = key.charAt(0);
                ItemStack item = entry.getValue();
                if (item == null) {
                    throw new IllegalArgumentException("Ingredient for '" + symbol + "' cannot be null");
                }

                builder.withIngredient(symbol, item);
            }
        }
    }

    private void deserializeShapelessRecipe(@NotNull DeserializationData data,
                                            @NotNull BukkitCraftingBuilder builder) {
        List<ItemStack> ingredients = data.getAsList(INGREDIENTS_KEY, ItemStack.class);
        if (ingredients == null || ingredients.isEmpty()) {
            throw new IllegalArgumentException("Shapeless recipe must have ingredients");
        }

        for (ItemStack item : ingredients) {
            if (item == null) {
                throw new IllegalArgumentException("Shapeless ingredient cannot be null");
            }
            builder.addIngredient(item);
        }
    }
}