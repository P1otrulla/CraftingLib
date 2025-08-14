package dev.piotrulla.craftinglib;

import dev.piotrulla.craftinglib.pattern.RecipePattern;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Collections;
import java.util.Arrays;
import java.util.stream.Collectors;

public class BukkitRecipe implements Recipe<ItemStack> {

    private final String name;
    private final String id;
    private final RecipePattern<ItemStack> pattern;
    private final BukkitRecipeIngredient result;
    private final boolean exactMatch;
    private final boolean replaceVanilla;
    private final InventoryType inventoryType;
    private final RecipeType recipeType;

    private final CookingRecipeData cookingData;
    private final SmithingRecipeData smithingData;
    private final SingleInputRecipeData singleInputData;

    // Constructor for crafting recipes (shaped/shapeless)
    public BukkitRecipe(
            @NotNull String name, @NotNull String id,
            @NotNull RecipePattern<ItemStack> pattern,
            @NotNull BukkitRecipeIngredient result,
            boolean exactMatch, boolean replaceVanilla,
            @NotNull InventoryType inventoryType,
            @NotNull RecipeType recipeType
    ) {
        this(name, id, pattern, result, exactMatch, replaceVanilla, inventoryType, recipeType, null, null, null);
    }

    // Constructor for cooking recipes
    public BukkitRecipe(
            @NotNull String name, @NotNull String id,
            @NotNull SingleInputRecipeData inputData,
            @NotNull BukkitRecipeIngredient result,
            @NotNull InventoryType inventoryType,
            @NotNull RecipeType recipeType,
            @NotNull CookingRecipeData cookingData
    ) {
        this(name, id, null, result, false, false, inventoryType, recipeType, cookingData, null, inputData);
    }

    // Constructor for smithing recipes
    public BukkitRecipe(
            @NotNull String name, @NotNull String id,
            @NotNull SmithingRecipeData smithingData,
            @NotNull BukkitRecipeIngredient result,
            @NotNull InventoryType inventoryType,
            @NotNull RecipeType recipeType
    ) {
        this(name, id, null, result, false, false, inventoryType, recipeType, null, smithingData, null);
    }

    // Constructor for stonecutting recipes
    public BukkitRecipe(
            @NotNull String name, @NotNull String id,
            @NotNull SingleInputRecipeData inputData,
            @NotNull BukkitRecipeIngredient result,
            @NotNull InventoryType inventoryType,
            @NotNull RecipeType recipeType
    ) {
        this(name, id, null, result, false, false, inventoryType, recipeType, null, null, inputData);
    }

    // Master constructor
    private BukkitRecipe(
            @NotNull String name, @NotNull String id,
            @Nullable RecipePattern<ItemStack> pattern,
            @NotNull BukkitRecipeIngredient result,
            boolean exactMatch, boolean replaceVanilla,
            @NotNull InventoryType inventoryType,
            @NotNull RecipeType recipeType,
            @Nullable CookingRecipeData cookingData,
            @Nullable SmithingRecipeData smithingData,
            @Nullable SingleInputRecipeData singleInputData
    ) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.pattern = pattern;
        this.result = Objects.requireNonNull(result, "Result cannot be null");
        this.exactMatch = exactMatch;
        this.replaceVanilla = replaceVanilla;
        this.inventoryType = Objects.requireNonNull(inventoryType, "Crafting type cannot be null");
        this.recipeType = Objects.requireNonNull(recipeType, "Recipe type cannot be null");
        this.cookingData = cookingData;
        this.smithingData = smithingData;
        this.singleInputData = singleInputData;

        this.validateRecipeData();
    }

    private void validateRecipeData() {
        switch (this.recipeType) {
            case SHAPED:
            case SHAPELESS:
                if (this.pattern == null) {
                    throw new IllegalArgumentException("Pattern is required for crafting recipes");
                }
                break;
            case FURNACE:
            case BLASTING:
            case SMOKING:
            case CAMPFIRE:
                if (this.cookingData == null || this.singleInputData == null) {
                    throw new IllegalArgumentException("Cooking data and input data are required for cooking recipes");
                }
                break;
            case SMITHING_TRANSFORM:
            case SMITHING_TRIM:
                if (this.smithingData == null) {
                    throw new IllegalArgumentException("Smithing data is required for smithing recipes");
                }
                break;
            case STONECUTTING:
                if (this.singleInputData == null) {
                    throw new IllegalArgumentException("Input data is required for stonecutting recipes");
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown recipe type: " + this.recipeType);
        }
    }

    @Override
    @NotNull
    public String name() {
        return this.name;
    }

    @Override
    @NotNull
    public String id() {
        return this.id;
    }

    @Override
    @NotNull
    public List<RecipeIngredient<ItemStack>> ingredients() {
        if (this.pattern != null) {
            return this.pattern.getIngredients();
        }
        // For single input recipes - JAVA 11 COMPATIBLE
        if (this.singleInputData != null) {
            return Collections.singletonList(new BukkitRecipeIngredient(this.singleInputData.input()));
        }
        // For smithing recipes - JAVA 11 COMPATIBLE
        if (this.smithingData != null) {
            return Arrays.asList(
                    new BukkitRecipeIngredient(this.smithingData.template()),
                    new BukkitRecipeIngredient(this.smithingData.base()),
                    new BukkitRecipeIngredient(this.smithingData.addition())
            );
        }
        return Collections.emptyList(); // Instead of List.of()
    }

    @Override
    @NotNull
    public RecipeIngredient<ItemStack> result() {
        return this.result;
    }

    @Override
    public boolean matches(@NotNull List<RecipeIngredient<ItemStack>> input) {
        if (this.pattern != null) {
            return this.pattern.matches(input);
        }
        // For single input recipes, just check if input matches
        if (this.singleInputData != null && !input.isEmpty()) {
            RecipeIngredient<ItemStack> required = new BukkitRecipeIngredient(this.singleInputData.input());
            return required.matches(input.get(0), this.exactMatch);
        }
        return false;
    }

    @Override
    public boolean requiresExactMatch() {
        return this.exactMatch;
    }

    public boolean shouldReplaceVanilla() {
        return this.replaceVanilla;
    }

    @NotNull
    public InventoryType getInventoryType() {
        return this.inventoryType;
    }

    @NotNull
    public RecipeType getRecipeType() {
        return this.recipeType;
    }

    @Nullable
    public RecipePattern<ItemStack> getPattern() {
        return this.pattern;
    }

    @Nullable
    public CookingRecipeData getCookingData() {
        return this.cookingData;
    }

    @Nullable
    public SmithingRecipeData getSmithingData() {
        return this.smithingData;
    }

    @Nullable
    public SingleInputRecipeData getSingleInputData() {
        return this.singleInputData;
    }

    // Helper methods
    public boolean isCraftingRecipe() {
        return this.recipeType == RecipeType.SHAPED || this.recipeType == RecipeType.SHAPELESS;
    }

    public boolean isCookingRecipe() {
        return this.recipeType == RecipeType.FURNACE || this.recipeType == RecipeType.BLASTING ||
                this.recipeType == RecipeType.SMOKING || this.recipeType == RecipeType.CAMPFIRE;
    }

    public boolean isSmithingRecipe() {
        return this.recipeType == RecipeType.SMITHING_TRANSFORM || this.recipeType == RecipeType.SMITHING_TRIM;
    }

    public boolean isStonecuttingRecipe() {
        return this.recipeType == RecipeType.STONECUTTING;
    }

    /**
     * Legacy method for backwards compatibility
     */
    public boolean matchesCraftingMatrix(@NotNull ItemStack[] craftingMatrix) {
        if (!this.isCraftingRecipe()) {
            return false;
        }

        List<RecipeIngredient<ItemStack>> matrixIngredients = convertToIngredients(craftingMatrix);
        return this.matches(matrixIngredients);
    }

    private List<RecipeIngredient<ItemStack>> convertToIngredients(ItemStack[] matrix) {
        return java.util.Arrays.stream(matrix)
                .map(item -> item != null ? new BukkitRecipeIngredient(item) : null)
                .filter(Objects::nonNull)
                .map(ingredient -> (RecipeIngredient<ItemStack>) ingredient)
                .collect(Collectors.toList());
    }
}