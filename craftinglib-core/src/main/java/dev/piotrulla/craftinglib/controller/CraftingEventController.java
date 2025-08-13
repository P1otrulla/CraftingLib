package dev.piotrulla.craftinglib.controller;

import dev.piotrulla.craftinglib.CraftingRecipe;
import dev.piotrulla.craftinglib.action.CraftingAction;
import dev.piotrulla.craftinglib.action.dispatcher.CraftingActionDispatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Controller interface for handling crafting logic.
 */
public interface CraftingEventController<T, E extends CraftingAction> {

    /**
     * Processes a craft attempt.
     *
     * @param matrix the crafting matrix
     * @param context platform-specific context (e.g., Player for Bukkit)
     * @return the result of the craft attempt
     */
    @Nullable
    CraftingEventController.CraftingEventResult<T> processCraft(@NotNull T[] matrix, @NotNull Object context);

    /**
     * Validates if a recipe can be crafted.
     *
     * @param recipe the recipe to validate
     * @param context platform-specific context
     * @return validation result
     */
    @NotNull
    ValidationResult validateCraft(@NotNull CraftingRecipe<T> recipe, @NotNull Object context);

    /**
     * Gets the action dispatcher for this controller.
     */
    @NotNull
    CraftingActionDispatcher<E> getEventDispatcher();

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
        CraftingRecipe<T> getRecipe();
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
