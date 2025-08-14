package dev.piotrulla.craftinglib.controller;

import dev.piotrulla.craftinglib.Recipe;
import dev.piotrulla.craftinglib.action.RecipeAction;
import dev.piotrulla.craftinglib.action.dispatcher.RecipeActionDispatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Controller interface for handling crafting logic.
 */
public interface RecipeEventController<T, E extends RecipeAction> {

    /**
     * Processes a craft attempt.
     *
     * @param matrix the crafting matrix
     * @param context platform-specific context (e.g., Player for Bukkit)
     * @return the result of the craft attempt
     */
    @Nullable
    RecipeEventController.CraftingEventResult<T> processCraft(@NotNull T[] matrix, @NotNull Object context);

    /**
     * Validates if a recipe can be crafted.
     *
     * @param recipe the recipe to validate
     * @param context platform-specific context
     * @return validation result
     */
    @NotNull
    ValidationResult validateCraft(@NotNull Recipe<T> recipe, @NotNull Object context);

    /**
     * Gets the action dispatcher for this controller.
     */
    @NotNull
    RecipeActionDispatcher<E> getEventDispatcher();

    /**
     * Result of a craft attempt.
     */
    interface CraftingEventResult<T> {
        boolean isSuccess();

        @Nullable
        T getResult();

        @Nullable
        String getFailureReason();

        @Nullable
        Recipe<T> getRecipe();
    }

    /**
     * Result of craft validation.
     */
    interface ValidationResult {
        boolean isValid();

        @Nullable
        String getInvalidReason();

        @NotNull
        ValidationStatus getStatus();

        enum ValidationStatus {
            VALID,
            NO_PERMISSION,
            MISSING_REQUIREMENTS,
            DISABLED,
            CUSTOM_CONDITION_FAILED
        }
    }
}
