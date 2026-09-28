package dev.piotrulla.craftinglib.recipe.smelting;

/**
 * Cooking station. Everything except {@link #FURNACE} needs Minecraft 1.14+.
 */
public enum CookingType {

    FURNACE(200),
    BLASTING(100),
    SMOKING(100),
    CAMPFIRE(600);

    private final int defaultCookingTime;

    CookingType(int defaultCookingTime) {
        this.defaultCookingTime = defaultCookingTime;
    }

    /**
     * @return vanilla cooking time in ticks
     */
    public int getDefaultCookingTime() {
        return this.defaultCookingTime;
    }
}