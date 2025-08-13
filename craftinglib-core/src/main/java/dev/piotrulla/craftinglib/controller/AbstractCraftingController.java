package dev.piotrulla.craftinglib.controller;

import dev.piotrulla.craftinglib.CraftingRecipe;
import dev.piotrulla.craftinglib.CraftingRecipeManager;
import dev.piotrulla.craftinglib.action.CraftingAction;
import dev.piotrulla.craftinglib.action.dispatcher.CraftingActionDispatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.logging.Logger;

/**
 * Abstract base implementation of CraftingController.
 */
public abstract class AbstractCraftingController<T, E extends CraftingAction>
        implements CraftingEventController<T, E> {

    protected final CraftingRecipeManager<T> recipeManager;
    protected final CraftingActionDispatcher<E> eventDispatcher;
    protected final Logger logger;
    protected final boolean debugMode;

    protected AbstractCraftingController(
            @NotNull CraftingActionDispatcher<E> eventDispatcher,
            @NotNull CraftingRecipeManager<T> recipeManager,
            @NotNull Logger logger,
            boolean debugMode
    ) {
        this.eventDispatcher = Objects.requireNonNull(eventDispatcher, "Event dispatcher cannot be null");
        this.recipeManager = Objects.requireNonNull(recipeManager, "Recipe manager cannot be null");
        this.logger = Objects.requireNonNull(logger, "Logger cannot be null");
        this.debugMode = debugMode;
    }

    @Override
    @NotNull
    public CraftingActionDispatcher<E> getEventDispatcher() {
        return this.eventDispatcher;
    }

    protected void logDebug(@NotNull String message) {
        if (this.debugMode) {
            this.logger.info("[CraftingLib-Controller-Debug] " + message);
        }
    }

    /**
     * Simple implementation of CraftResult.
     */
    protected static class SimpleCraftEventResult<T> implements CraftingEventResult<T> {
        private final boolean success;
        private final T result;
        private final String failureReason;
        private final CraftingRecipe<T> recipe;

        private SimpleCraftEventResult(boolean success, @Nullable T result,
                                       @Nullable String failureReason,
                                       @Nullable CraftingRecipe<T> recipe) {
            this.success = success;
            this.result = result;
            this.failureReason = failureReason;
            this.recipe = recipe;
        }

        public static <T> SimpleCraftEventResult<T> success(@NotNull T result, @NotNull CraftingRecipe<T> recipe) {
            return new SimpleCraftEventResult<>(true, result, null, recipe);
        }

        public static <T> SimpleCraftEventResult<T> failure(@NotNull String reason) {
            return new SimpleCraftEventResult<>(false, null, reason, null);
        }

        @Override
        public boolean isSuccess() {
            return this.success;
        }

        @Override
        @Nullable
        public T getResult() {
            return this.result;
        }

        @Override
        @Nullable
        public String getFailureReason() {
            return this.failureReason;
        }

        @Override
        @Nullable
        public CraftingRecipe<T> getRecipe() {
            return this.recipe;
        }
    }

    /**
     * Simple implementation of ValidationResult.
     */
    protected static class SimpleValidationResult implements ValidationResult {
        private final boolean valid;
        private final String reason;
        private final ValidationStatus status;

        private SimpleValidationResult(boolean valid, @Nullable String reason, @NotNull ValidationStatus status) {
            this.valid = valid;
            this.reason = reason;
            this.status = status;
        }

        public static SimpleValidationResult valid() {
            return new SimpleValidationResult(true, null, ValidationStatus.VALID);
        }

        public static SimpleValidationResult invalid(@NotNull String reason, @NotNull ValidationStatus status) {
            return new SimpleValidationResult(false, reason, status);
        }

        @Override
        public boolean isValid() {
            return this.valid;
        }

        @Override
        @Nullable
        public String getInvalidReason() {
            return this.reason;
        }

        @Override
        @NotNull
        public ValidationStatus getStatus() {
            return this.status;
        }
    }
}