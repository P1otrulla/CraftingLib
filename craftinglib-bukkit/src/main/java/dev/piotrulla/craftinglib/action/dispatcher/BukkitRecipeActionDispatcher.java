package dev.piotrulla.craftinglib.action.dispatcher;

import dev.piotrulla.craftinglib.action.BukkitRecipeAction;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

/**
 * Bukkit implementation of action dispatcher.
 */
public class BukkitRecipeActionDispatcher extends AbstractRecipeActionDispatcher<BukkitRecipeAction> {

    public BukkitRecipeActionDispatcher(@NotNull Logger logger, boolean debugMode) {
        super(logger, debugMode);
    }

    public BukkitRecipeActionDispatcher(@NotNull Logger logger) {
        this(logger, false);
    }
}
