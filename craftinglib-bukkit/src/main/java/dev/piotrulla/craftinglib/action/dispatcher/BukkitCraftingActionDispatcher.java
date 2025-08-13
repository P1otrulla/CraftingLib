package dev.piotrulla.craftinglib.action.dispatcher;

import dev.piotrulla.craftinglib.action.BukkitCraftingAction;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

/**
 * Bukkit implementation of action dispatcher.
 */
public class BukkitCraftingActionDispatcher extends AbstractCraftingActionDispatcher<BukkitCraftingAction> {

    public BukkitCraftingActionDispatcher(@NotNull Logger logger, boolean debugMode) {
        super(logger, debugMode);
    }

    public BukkitCraftingActionDispatcher(@NotNull Logger logger) {
        this(logger, false);
    }
}
