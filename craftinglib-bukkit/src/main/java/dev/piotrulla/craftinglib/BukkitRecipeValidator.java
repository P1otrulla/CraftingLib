package dev.piotrulla.craftinglib;

import dev.piotrulla.craftinglib.exception.RecipeException;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Validator for Bukkit recipes
 */
public final class BukkitRecipeValidator {

    private static final String RECIPE_NULL = "Recipe cannot be null";
    private static final String RESULT_NULL = "Result cannot be null";
    private static final String PATTERN_REQUIRED = "Pattern is required for crafting recipes";
    private static final String COOKING_DATA_REQUIRED = "Cooking data and input data are required for cooking recipes";
    private static final String SMITHING_DATA_REQUIRED = "Smithing data is required for smithing recipes";
    private static final String INPUT_DATA_REQUIRED = "Input data is required for stonecutting recipes";
    private static final String RESULT_NOT_AIR = "Result cannot be AIR";

    private BukkitRecipeValidator() {
        // Utility class
    }

    /**
     * Validates a complete recipe
     */
    public static void validateRecipe(@NotNull BukkitRecipe recipe) {
        Objects.requireNonNull(recipe, RECIPE_NULL);

        validateResult(recipe.result().item());
        validateRecipeData(recipe);
    }

    /**
     * Validates recipe data based on type
     */
    private static void validateRecipeData(@NotNull BukkitRecipe recipe) {
        RecipeType type = recipe.getRecipeType();

        switch (type) {
            case SHAPED:
            case SHAPELESS: {
                validateCraftingRecipe(recipe);
                break;
            }
            case FURNACE:
            case BLASTING:
            case SMOKING:
            case CAMPFIRE: {
                validateCookingRecipe(recipe);
                break;
            }
            case SMITHING_TRANSFORM:
            case SMITHING_TRIM: {
                validateSmithingRecipe(recipe);
                break;
            }
            case STONECUTTING: {
                validateStonecuttingRecipe(recipe);
                break;
            }
            default: {
                throw new RecipeException("Unknown recipe type: " + type);
            }
        }
    }

    private static void validateCraftingRecipe(@NotNull BukkitRecipe recipe) {
        if (recipe.getPattern() == null) {
            throw new RecipeException(PATTERN_REQUIRED);
        }
    }

    private static void validateCookingRecipe(@NotNull BukkitRecipe recipe) {
        CookingRecipeData cookingData = recipe.getCookingData();
        SingleInputRecipeData inputData = recipe.getSingleInputData();

        if (cookingData == null || inputData == null) {
            throw new RecipeException(COOKING_DATA_REQUIRED);
        }

        validateCookingData(cookingData);
        validateSingleInputData(inputData);
    }

    private static void validateSmithingRecipe(@NotNull BukkitRecipe recipe) {
        SmithingRecipeData smithingData = recipe.getSmithingData();

        if (smithingData == null) {
            throw new RecipeException(SMITHING_DATA_REQUIRED);
        }

        validateSmithingData(smithingData);
    }

    private static void validateStonecuttingRecipe(@NotNull BukkitRecipe recipe) {
        SingleInputRecipeData inputData = recipe.getSingleInputData();

        if (inputData == null) {
            throw new RecipeException(INPUT_DATA_REQUIRED);
        }

        validateSingleInputData(inputData);
    }

    /**
     * Validates cooking data
     */
    public static void validateCookingData(@NotNull CookingRecipeData data) {
        Objects.requireNonNull(data, "Cooking data cannot be null");

        if (data.cookingTime() < 1) {
            throw new RecipeException("Cooking time must be at least 1 tick");
        }

        if (data.experience() < 0) {
            throw new RecipeException("Experience cannot be negative");
        }
    }

    /**
     * Validates smithing data
     */
    public static void validateSmithingData(@NotNull SmithingRecipeData data) {
        Objects.requireNonNull(data, "Smithing data cannot be null");

        validateItem(data.template(), "Template");
        validateItem(data.base(), "Base");
        validateItem(data.addition(), "Addition");
    }

    /**
     * Validates single input data
     */
    public static void validateSingleInputData(@NotNull SingleInputRecipeData data) {
        Objects.requireNonNull(data, "Input data cannot be null");
        validateItem(data.input(), "Input");
    }

    /**
     * Validates result item
     */
    public static void validateResult(@NotNull ItemStack result) {
        Objects.requireNonNull(result, RESULT_NULL);

        if (result.getType() == Material.AIR) {
            throw new RecipeException(RESULT_NOT_AIR);
        }

        if (result.getAmount() < 1) {
            throw new RecipeException("Result amount must be at least 1");
        }
    }

    /**
     * Validates any item
     */
    public static void validateItem(@NotNull ItemStack item, @NotNull String itemName) {
        Objects.requireNonNull(item, itemName + " cannot be null");

        if (item.getType() == Material.AIR) {
            throw new RecipeException(itemName + " cannot be AIR");
        }
    }
}
