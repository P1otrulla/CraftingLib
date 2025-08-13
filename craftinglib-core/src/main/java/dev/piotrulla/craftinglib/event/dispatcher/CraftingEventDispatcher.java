package dev.piotrulla.craftinglib.event.dispatcher;

import dev.piotrulla.craftinglib.event.CraftingEvent;
import dev.piotrulla.craftinglib.event.CraftingEventHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Dispatcher for crafting events.
 * Manages event handlers and event firing.
 */
public interface CraftingEventDispatcher<E extends CraftingEvent> {

    /**
     * Registers an event handler.
     */
    void addHandler(@NotNull CraftingEventHandler<E> handler);

    /**
     * Removes an event handler.
     */
    boolean removeHandler(@NotNull CraftingEventHandler<E> handler);

    /**
     * Fires an event to all registered handlers.
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