package dev.piotrulla.craftinglib.action.dispatcher;

import dev.piotrulla.craftinglib.action.CraftingAction;
import dev.piotrulla.craftinglib.action.CraftingActionHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract implementation of action dispatcher with common functionality.
 */
public abstract class AbstractCraftingActionDispatcher<E extends CraftingAction>
        implements CraftingActionDispatcher<E> {

    private static final String HANDLER_NULL = "Handler cannot be null";
    private static final String EVENT_NULL = "Event cannot be null";

    protected final List<CraftingActionHandler<E>> handlers;
    protected final Logger logger;
    protected final boolean debugMode;

    protected AbstractCraftingActionDispatcher(@NotNull Logger logger, boolean debugMode) {
        this.handlers = new CopyOnWriteArrayList<>();
        this.logger = Objects.requireNonNull(logger, "Logger cannot be null");
        this.debugMode = debugMode;
    }

    @Override
    public void addHandler(@NotNull CraftingActionHandler<E> handler) {
        this.handlers.add(Objects.requireNonNull(handler, HANDLER_NULL));
        this.logDebug("Added action handler: " + handler.getClass().getSimpleName());
    }

    @Override
    public boolean removeHandler(@NotNull CraftingActionHandler<E> handler) {
        boolean removed = this.handlers.remove(Objects.requireNonNull(handler, HANDLER_NULL));
        if (removed) {
            this.logDebug("Removed action handler: " + handler.getClass().getSimpleName());
        }
        return removed;
    }

    @Override
    public void fireEvent(@NotNull E event) {
        Objects.requireNonNull(event, EVENT_NULL);
        this.logDebug("Firing action: " + event.type());

        for (CraftingActionHandler<E> handler : this.handlers) {
            try {
                handler.handle(event);
            }
            catch (Exception exception) {
                this.logger.log(Level.SEVERE,
                        "Error handling action " + event.type() + " in handler " +
                                handler.getClass().getSimpleName(), exception);
            }
        }
    }

    @Override
    public void clearHandlers() {
        int count = this.handlers.size();
        this.handlers.clear();
        this.logDebug("Cleared " + count + " action handlers");
    }

    @Override
    public int getHandlerCount() {
        return this.handlers.size();
    }

    protected void logDebug(@NotNull String message) {
        if (this.debugMode) {
            this.logger.info("[CraftingLib-EventDispatcher-Debug] " + message);
        }
    }
}