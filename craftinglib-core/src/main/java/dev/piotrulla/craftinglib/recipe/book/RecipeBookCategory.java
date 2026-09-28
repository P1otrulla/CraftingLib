package dev.piotrulla.craftinglib.recipe.book;

/**
 * Recipe book tab of a recipe. Crafting and cooking books have different tabs, {@link #MISC} exists in both.
 * Ignored on servers without recipe book categories.
 */
public enum RecipeBookCategory {

    BUILDING(true, false),
    REDSTONE(true, false),
    EQUIPMENT(true, false),
    FOOD(false, true),
    BLOCKS(false, true),
    MISC(true, true);

    private final boolean craftingCategory;
    private final boolean cookingCategory;

    RecipeBookCategory(boolean craftingCategory, boolean cookingCategory) {
        this.craftingCategory = craftingCategory;
        this.cookingCategory = cookingCategory;
    }

    /**
     * @return true when usable by crafting (grid) recipes
     */
    public boolean isCraftingCategory() {
        return this.craftingCategory;
    }

    /**
     * @return true when usable by furnace, blast furnace, smoker and campfire recipes
     */
    public boolean isCookingCategory() {
        return this.cookingCategory;
    }
}
