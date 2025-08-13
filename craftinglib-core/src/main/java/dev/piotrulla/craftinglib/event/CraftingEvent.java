package dev.piotrulla.craftinglib.event;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public interface CraftingEvent {

    /**
     * Gets the type of this event.
     */
    @NotNull
    CraftingEvent.Type getType();

    /**
     * Gets when this event occurred.
     */
    Instant getTimestamp();

    /**
     * Standard event types that all platforms should support.
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
