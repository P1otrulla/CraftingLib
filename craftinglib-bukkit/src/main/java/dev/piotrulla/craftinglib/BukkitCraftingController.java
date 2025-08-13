package dev.piotrulla.craftinglib;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

/**
 * Handles crafting events for custom recipes.
 */
public class BukkitCraftingController implements Listener {

    private static final String MANAGER_NULL = "Recipe manager cannot be null";
    private static final String LOGGER_NULL = "Logger cannot be null";
    private static final ItemStack AIR_ITEM = new ItemStack(Material.AIR);
    private static final int MATRIX_2X2 = 4;
    private static final int MATRIX_3X3 = 9;

    private final BukkitCraftingRecipeManager recipeManager;
    private final Logger logger;
    private final boolean debugMode;

    @Nullable
    private BiConsumer<Player, BukkitCraftingRecipe> onCraftSuccess;

    public BukkitCraftingController(
            @NotNull BukkitCraftingRecipeManager recipeManager,
            @NotNull Logger logger,
            boolean debugMode
    ) {
        this.recipeManager = Objects.requireNonNull(recipeManager, MANAGER_NULL);
        this.logger = Objects.requireNonNull(logger, LOGGER_NULL);
        this.debugMode = debugMode;
        this.onCraftSuccess = null;
    }

    public BukkitCraftingController(
            @NotNull BukkitCraftingRecipeManager recipeManager,
            @NotNull Logger logger
    ) {
        this(recipeManager, logger, false);
    }

    /**
     * Sets a callback to be executed when a player successfully crafts an item.
     * This allows external handling of craft events (messages, effects, etc.)
     */
    public void setOnCraftSuccess(@Nullable BiConsumer<Player, BukkitCraftingRecipe> callback) {
        this.onCraftSuccess = callback;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPrepareItemCraft(@NotNull PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();
        ItemStack[] matrix = inventory.getMatrix();

        if (this.isEmptyMatrix(matrix)) {
            return;
        }

        BukkitCraftingRecipe customRecipe = this.findMatchingRecipe(matrix, inventory);

        if (customRecipe != null) {
            ItemStack result = customRecipe.result().item().clone();
            inventory.setResult(result);
            this.logDebug("Showing custom recipe result: " + customRecipe.id());
        }
        else if (customRecipe.shouldReplaceVanilla()) {
            inventory.setResult(AIR_ITEM);
            this.logDebug("Blocked vanilla recipe preview");
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCraftItem(@NotNull CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        CraftingInventory inventory = event.getInventory();
        ItemStack[] matrix = inventory.getMatrix();

        if (this.isEmptyMatrix(matrix)) {
            return;
        }

        BukkitCraftingRecipe customRecipe = this.findMatchingRecipe(matrix, inventory);

        if (customRecipe != null) {
            this.handleCustomCraft(event, player, customRecipe);
        }
        else if (customRecipe.shouldReplaceVanilla()) {
            event.setCancelled(true);
            this.logDebug("Blocked vanilla craft for player: " + player.getName());
        }
    }

    private void handleCustomCraft(@NotNull CraftItemEvent event,
                                   @NotNull Player player,
                                   @NotNull BukkitCraftingRecipe recipe) {
        // Let the action proceed with the custom result
        this.logDebug("Player " + player.getName() + " crafted: " + recipe.id());

        // Execute callback if configured
        if (this.onCraftSuccess != null) {
            this.onCraftSuccess.accept(player, recipe);
        }
    }

    @Nullable
    private BukkitCraftingRecipe findMatchingRecipe(@NotNull ItemStack[] matrix,
                                                    @NotNull CraftingInventory inventory) {
        BukkitCraftingRecipe.CraftingType type = this.determineCraftingType(inventory);
        return this.recipeManager.findMatchingRecipe(matrix, type);
    }

    private boolean isEmptyMatrix(@NotNull ItemStack[] matrix) {
        for (ItemStack item : matrix) {
            if (item != null && item.getType() != Material.AIR) {
                return false;
            }
        }
        return true;
    }

    @NotNull
    private BukkitCraftingRecipe.CraftingType determineCraftingType(@NotNull CraftingInventory inventory) {
        int matrixSize = inventory.getMatrix().length;

        switch (matrixSize) {
            case MATRIX_2X2: {
                return BukkitCraftingRecipe.CraftingType.PLAYER_INVENTORY_2X2;
            }
            case MATRIX_3X3: {
                return BukkitCraftingRecipe.CraftingType.CRAFTING_TABLE_3X3;
            }
            default: {
                this.logger.warning("Unexpected crafting matrix size: " + matrixSize);
                return BukkitCraftingRecipe.CraftingType.CRAFTING_TABLE_3X3;
            }
        }
    }

    private void logDebug(@NotNull String message) {
        if (this.debugMode) {
            this.logger.info("[CraftingLib-Debug] " + message);
        }
    }

    public boolean isDebugMode() {
        return this.debugMode;
    }
}
