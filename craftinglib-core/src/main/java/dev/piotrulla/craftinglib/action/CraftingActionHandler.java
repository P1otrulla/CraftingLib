package dev.piotrulla.craftinglib.action;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface CraftingActionHandler<E extends CraftingAction> {

    /**
     * Handles a crafting action.
     *
     * @param event the action to handle
     */
    void handle(@NotNull E event);
}
