package dev.piotrulla.craftinglib;

import org.jetbrains.annotations.Nullable;

import org.jetbrains.annotations.NotNull;

/**
 * Immutable record for cooking recipe data
 */
public record CookingRecipeData(
        int cookingTime,
        float experience,
        @Nullable String group
) {

    public CookingRecipeData {
        if (cookingTime < 1) {
            throw new IllegalArgumentException("Cooking time must be at least 1 tick");
        }
        if (experience < 0.0f) {
            throw new IllegalArgumentException("Experience cannot be negative");
        }
    }

    /**
     * Creates cooking data without group
     */
    public CookingRecipeData(int cookingTime, float experience) {
        this(cookingTime, experience, null);
    }

    /**
     * Factory method for furnace defaults
     */
    @NotNull
    public static CookingRecipeData furnace() {
        return new CookingRecipeData(200, 0.1f);
    }

    /**
     * Factory method for blast furnace defaults
     */
    @NotNull
    public static CookingRecipeData blasting() {
        return new CookingRecipeData(100, 0.1f);
    }

    /**
     * Factory method for smoker defaults
     */
    @NotNull
    public static CookingRecipeData smoking() {
        return new CookingRecipeData(100, 0.1f);
    }

    /**
     * Factory method for campfire defaults
     */
    @NotNull
    public static CookingRecipeData campfire() {
        return new CookingRecipeData(600, 0.35f);
    }
}