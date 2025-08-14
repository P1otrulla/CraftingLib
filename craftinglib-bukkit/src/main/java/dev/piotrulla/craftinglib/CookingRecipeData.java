package dev.piotrulla.craftinglib;

import org.jetbrains.annotations.Nullable;

public class CookingRecipeData {

    private final int cookingTime;
    private final float experience;
    private final String group;

    public CookingRecipeData(int cookingTime, float experience, @Nullable String group) {
        this.cookingTime = Math.max(1, cookingTime);
        this.experience = Math.max(0.0f, experience);
        this.group = group;
    }

    public CookingRecipeData(int cookingTime, float experience) {
        this(cookingTime, experience, null);
    }

    public int cookingTime() {
        return this.cookingTime;
    }

    public float experience() {
        return this.experience;
    }

    public String group() {
        return this.group;
    }

    public static CookingRecipeData furnace() {
        return new CookingRecipeData(200, 0.1f);
    }

    public static CookingRecipeData blasting() {
        return new CookingRecipeData(100, 0.1f);
    }

    public static CookingRecipeData smoking() {
        return new CookingRecipeData(100, 0.1f);
    }

    public static CookingRecipeData campfire() {
        return new CookingRecipeData(600, 0.35f);
    }
}