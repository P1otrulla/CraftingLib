package dev.piotrulla.craftinglib.action;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public interface CraftingAction {

    /**
     * Gets the type of this action.
     */
    @NotNull
    CraftingAction.Type type();

    /**
     * Gets when this action occurred.
     */
    Instant timestamp();

    /**
     * Standard action types that all platforms should support.
     */
    enum Type {
        CRAFT_SUCCESS,
        CRAFT_BLOCKED,
        CRAFT_FAILED,
        VANILLA_BLOCKED,
        RECIPE_NOT_FOUND,
        CUSTOM
    }
}
