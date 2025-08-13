package dev.piotrulla.craftinglib.event.dispatcher;

import dev.piotrulla.craftinglib.event.BukkitCraftingEvent;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

/**
 * Bukkit implementation of event dispatcher.
 */
public class BukkitCraftingEventDispatcher extends AbstractCraftingEventDispatcher<BukkitCraftingEvent> {

    public BukkitCraftingEventDispatcher(@NotNull Logger logger, boolean debugMode) {
        super(logger, debugMode);
    }

    public BukkitCraftingEventDispatcher(@NotNull Logger logger) {
        this(logger, false);
    }
}
