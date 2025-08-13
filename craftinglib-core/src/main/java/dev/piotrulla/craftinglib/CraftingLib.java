package dev.piotrulla.craftinglib;

import dev.piotrulla.craftinglib.registry.CraftingRecipeRegistry;
import org.jetbrains.annotations.NotNull;

public interface CraftingLib<T> {

    /**
     * Gets the recipe manager for this platform.
     */
    @NotNull
    CraftingRecipeManager<T> getRecipeManager();

    /**
     * Gets the recipe registry for this platform.
     */
    @NotNull
    CraftingRecipeRegistry<T> getRecipeRegistry();

    /**
     * Gets the platform identifier.
     */
    @NotNull
    String getPlatform();

    /**
     * Gets the version of this CraftingLib implementation.
     */
    @NotNull
    String getVersion();

    /**
     * Initializes the CraftingLib instance.
     */
    void initialize();

    /**
     * Shuts down the CraftingLib instance and cleans up resources.
     */
    void shutdown();

    /**
     * Checks if this CraftingLib instance is initialized.
     */
    boolean isInitialized();
}