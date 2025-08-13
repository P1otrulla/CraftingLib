package dev.piotrulla.craftinglib.event;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface CraftingEventHandler<E extends CraftingEvent> {

    /**
     * Handles a crafting event.
     *
     * @param event the event to handle
     */
    void handle(@NotNull E event);
}
