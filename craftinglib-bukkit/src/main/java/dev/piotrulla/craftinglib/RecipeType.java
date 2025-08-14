package dev.piotrulla.craftinglib;

import org.jetbrains.annotations.NotNull;
import java.util.EnumSet;
import java.util.Set;

/**
 * Recipe type enum with categorization support.
 */
public enum RecipeType {
    // Crafting recipes
    SHAPED(RecipeCategory.CRAFTING),
    SHAPELESS(RecipeCategory.CRAFTING),

    // Cooking recipes
    FURNACE(RecipeCategory.COOKING),
    BLASTING(RecipeCategory.COOKING),
    SMOKING(RecipeCategory.COOKING),
    CAMPFIRE(RecipeCategory.COOKING),

    // Other recipes
    STONECUTTING(RecipeCategory.PROCESSING),
    SMITHING_TRANSFORM(RecipeCategory.SMITHING),
    SMITHING_TRIM(RecipeCategory.SMITHING);

    // BREWING(RecipeCategory.BREWING),
    // ENCHANTING(RecipeCategory.ENCHANTING);

    private final RecipeCategory category;

    RecipeType(@NotNull RecipeCategory category) {
        this.category = category;
    }

    @NotNull
    public RecipeCategory getCategory() {
        return this.category;
    }

    /**
     * Recipe categories for grouping related recipe types.
     */
    public enum RecipeCategory {
        CRAFTING,
        COOKING,
        SMITHING,
        PROCESSING
//        BREWING,
//        ENCHANTING
    }


    private static final Set<RecipeType> CRAFTING_RECIPES = EnumSet.of(
            SHAPED, SHAPELESS
    );

    private static final Set<RecipeType> COOKING_RECIPES = EnumSet.of(
            FURNACE, BLASTING, SMOKING, CAMPFIRE
    );

    private static final Set<RecipeType> SMITHING_RECIPES = EnumSet.of(
            SMITHING_TRANSFORM, SMITHING_TRIM
    );

    private static final Set<RecipeType> PROCESSING_RECIPES = EnumSet.of(
            STONECUTTING
    );

    private static final Set<RecipeType> SINGLE_INPUT_RECIPES = EnumSet.of(
            FURNACE, BLASTING, SMOKING, CAMPFIRE, STONECUTTING
    );

    private static final Set<RecipeType> COMPLEX_DATA_RECIPES = EnumSet.of(
            SMITHING_TRANSFORM, SMITHING_TRIM
    );

    private static final Set<RecipeType> EXPERIENCE_RECIPES = EnumSet.of(
            FURNACE, BLASTING, SMOKING, CAMPFIRE
    );

    private static final Set<RecipeType> TIMED_RECIPES = EnumSet.of(
            FURNACE, BLASTING, SMOKING, CAMPFIRE
    );

    /**
     * Checks if this recipe type is in the crafting category.
     */
    public boolean isCrafting() {
        return CRAFTING_RECIPES.contains(this);
    }

    /**
     * Checks if this recipe type is in the cooking category.
     */
    public boolean isCooking() {
        return COOKING_RECIPES.contains(this);
    }

    /**
     * Checks if this recipe type is in the smithing category.
     */
    public boolean isSmithing() {
        return SMITHING_RECIPES.contains(this);
    }

    /**
     * Checks if this recipe type is in the processing category.
     */
    public boolean isProcessing() {
        return PROCESSING_RECIPES.contains(this);
    }

    /**
     * Checks if this recipe type uses single input.
     */
    public boolean isSingleInput() {
        return SINGLE_INPUT_RECIPES.contains(this);
    }

    /**
     * Checks if this recipe type requires complex data structures.
     */
    public boolean requiresComplexData() {
        return COMPLEX_DATA_RECIPES.contains(this);
    }

    /**
     * Checks if this recipe type supports experience rewards.
     */
    public boolean supportsExperience() {
        return EXPERIENCE_RECIPES.contains(this);
    }

    /**
     * Checks if this recipe type supports cooking time.
     */
    public boolean supportsCookingTime() {
        return TIMED_RECIPES.contains(this);
    }

    /**
     * Checks if this recipe type belongs to specified category.
     */
    public boolean isInCategory(@NotNull RecipeCategory category) {
        return this.category == category;
    }

    /**
     * Gets all recipe types in the specified category.
     */
    @NotNull
    public static Set<RecipeType> getRecipesByCategory(@NotNull RecipeCategory category) {
        Set<RecipeType> result = EnumSet.noneOf(RecipeType.class);

        for (RecipeType type : values()) {
            if (type.isInCategory(category)) {
                result.add(type);
            }
        }
        return result;
    }

    /**
     * Gets all crafting recipe types.
     */
    @NotNull
    public static Set<RecipeType> getCraftingRecipes() {
        return EnumSet.copyOf(CRAFTING_RECIPES);
    }

    /**
     * Gets all cooking recipe types.
     */
    @NotNull
    public static Set<RecipeType> getCookingRecipes() {
        return EnumSet.copyOf(COOKING_RECIPES);
    }

    /**
     * Gets all smithing recipe types.
     */
    @NotNull
    public static Set<RecipeType> getSmithingRecipes() {
        return EnumSet.copyOf(SMITHING_RECIPES);
    }

    /**
     * Adds a new recipe type to a category at runtime (for future extensions).
     * WARNING: This should only be used for dynamic recipe type registration.
     */
    public static synchronized void addToCookingRecipes(@NotNull RecipeType recipeType) {
        COOKING_RECIPES.add(recipeType);
        SINGLE_INPUT_RECIPES.add(recipeType);

        if (recipeType.supportsExperience()) {
            EXPERIENCE_RECIPES.add(recipeType);
        }
        if (recipeType.supportsCookingTime()) {
            TIMED_RECIPES.add(recipeType);
        }
    }
}
