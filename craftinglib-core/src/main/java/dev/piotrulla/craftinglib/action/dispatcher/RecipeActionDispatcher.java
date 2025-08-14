package dev.piotrulla.craftinglib.action.dispatcher;

import dev.piotrulla.craftinglib.action.RecipeAction;
import dev.piotrulla.craftinglib.action.RecipeActionHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Dispatcher for crafting events.
 * Manages action handlers and action firing.
 */
public interface RecipeActionDispatcher<E extends RecipeAction> {

    /**
     * Registers an action handler.
     */
    void addHandler(@NotNull RecipeActionHandler<E> handler);

    /**
     * Removes an action handler.
     */
    boolean removeHandler(@NotNull RecipeActionHandler<E> handler);

    /**
     * Fires an action to all registered handlers.
     */
    void fireEvent(@NotNull E event);

    /**
     * Clears all registered handlers.
     */
    void clearHandlers();

    /**
     * Gets the number of registered handlers.
     */
    int getHandlerCount();
}