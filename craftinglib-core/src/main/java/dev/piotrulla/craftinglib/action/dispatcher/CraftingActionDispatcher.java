package dev.piotrulla.craftinglib.action.dispatcher;

import dev.piotrulla.craftinglib.action.CraftingAction;
import dev.piotrulla.craftinglib.action.CraftingActionHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Dispatcher for crafting events.
 * Manages action handlers and action firing.
 */
public interface CraftingActionDispatcher<E extends CraftingAction> {

    /**
     * Registers an action handler.
     */
    void addHandler(@NotNull CraftingActionHandler<E> handler);

    /**
     * Removes an action handler.
     */
    boolean removeHandler(@NotNull CraftingActionHandler<E> handler);

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