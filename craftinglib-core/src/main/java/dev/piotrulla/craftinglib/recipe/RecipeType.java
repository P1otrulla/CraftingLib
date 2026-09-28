package dev.piotrulla.craftinglib.recipe;

public enum RecipeType {

    /**
     * Ingredients must form a pattern (can be mirrored horizontally, if enabled).
     */
    SHAPED,

    /**
     * Only the set of ingredients matters, not their positions.
     */
    SHAPELESS
}
