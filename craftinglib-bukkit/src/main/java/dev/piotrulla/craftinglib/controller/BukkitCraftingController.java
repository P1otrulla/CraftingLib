package dev.piotrulla.craftinglib.controller;

import dev.piotrulla.craftinglib.BukkitCraftingRecipe;
import dev.piotrulla.craftinglib.BukkitCraftingRecipeIngredient;
import dev.piotrulla.craftinglib.BukkitCraftingRecipeManager;
import dev.piotrulla.craftinglib.CraftingRecipe;
import dev.piotrulla.craftinglib.CraftingRecipeIngredient;
import dev.piotrulla.craftinglib.action.BukkitCraftingAction;
import dev.piotrulla.craftinglib.action.BukkitCraftingTypedAction;
import dev.piotrulla.craftinglib.action.CraftingAction;
import dev.piotrulla.craftinglib.action.dispatcher.BukkitCraftingActionDispatcher;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Bukkit implementation of CraftingController.
 */
public class BukkitCraftingController extends AbstractCraftingController<ItemStack, BukkitCraftingAction> {

    private static final ItemStack AIR_ITEM = new ItemStack(Material.AIR);
    private static final int MATRIX_2X2 = 4;
    private static final int MATRIX_3X3 = 9;

    public BukkitCraftingController(@NotNull BukkitCraftingRecipeManager recipeManager,
                                    @NotNull BukkitCraftingActionDispatcher eventDispatcher,
                                    @NotNull Logger logger,
                                    boolean debugMode) {
        super(recipeManager, eventDispatcher, logger, debugMode);
    }

    public BukkitCraftingController(@NotNull BukkitCraftingRecipeManager recipeManager,
                                    @NotNull Logger logger) {
        this(recipeManager, new BukkitCraftingActionDispatcher(logger), logger, false);
    }

    /**
     * Handles the prepare craft action - shows preview.
     */
    public void handlePrepareCraft(@NotNull PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();
        ItemStack[] matrix = inventory.getMatrix();

        if (this.isEmptyMatrix(matrix)) {
            return;
        }

        BukkitCraftingRecipe customRecipe = this.findMatchingRecipe(matrix, inventory);

        if (customRecipe != null) {
            ItemStack result = customRecipe.result().item().clone();
            inventory.setResult(result);
            this.logDebug("Showing custom recipe preview: " + customRecipe.id());
        }
        else if (customRecipe.shouldReplaceVanilla()) {
            inventory.setResult(AIR_ITEM);
            this.logDebug("Blocked vanilla recipe preview");
        }
    }

    /**
     * Handles the actual craft action.
     */
    public void handleCraftItem(@NotNull CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        CraftingInventory inventory = event.getInventory();
        ItemStack[] matrix = inventory.getMatrix();

        CraftingEventResult<ItemStack> result = this.processCraft(matrix, player);

        if (result != null && result.isSuccess() && result.getRecipe() instanceof BukkitCraftingRecipe) {
            BukkitCraftingRecipe recipe = (BukkitCraftingRecipe) result.getRecipe();
            ItemStack craftedItem = event.getCurrentItem();

            if (craftedItem != null && craftedItem.getType() != Material.AIR) {
                BukkitCraftingTypedAction successEvent = new BukkitCraftingTypedAction(
                        player, recipe, CraftingAction.Type.CRAFT_SUCCESS, craftedItem, craftedItem.getAmount()
                );
                this.eventDispatcher.fireEvent(successEvent);
            }
        }

        event.setCancelled(true);
        this.logDebug("Blocked vanilla craft for: " + player.getName());
    }

    @Override
    @Nullable
    public CraftingEventController.CraftingEventResult<ItemStack> processCraft(@NotNull ItemStack[] matrix, @NotNull Object context) {
        if (this.isEmptyMatrix(matrix)) {
            return SimpleCraftEventResult.failure("Empty crafting matrix");
        }

        if (!(context instanceof Player)) {
            return SimpleCraftEventResult.failure("Invalid context");
        }

        Player player = (Player) context;

        // Convert to ingredients list
        List<CraftingRecipeIngredient<ItemStack>> ingredients = Arrays.stream(matrix)
                .map(item -> item == null || item.getType() == Material.AIR ? null :
                        new BukkitCraftingRecipeIngredient(item))
                .collect(Collectors.toList());

        // Find matching recipe
        CraftingRecipe<ItemStack> recipe = this.recipeManager.findMatchingRecipe(ingredients);

        if (recipe == null) {
            return SimpleCraftEventResult.failure("No matching recipe found");
        }

        // Validate the craft
        ValidationResult validation = this.validateCraft(recipe, player);
        if (!validation.isValid()) {
            return SimpleCraftEventResult.failure(validation.getInvalidReason());
        }

        return SimpleCraftEventResult.success(recipe.result().item(), recipe);
    }

    @Override
    @NotNull
    public ValidationResult validateCraft(@NotNull CraftingRecipe<ItemStack> recipe, @NotNull Object context) {
        if (!(context instanceof Player)) {
            return SimpleValidationResult.invalid("Invalid context", ValidationResult.ValidationStatus.CUSTOM_CONDITION_FAILED);
        }

        return SimpleValidationResult.valid();
    }

    @Nullable
    private BukkitCraftingRecipe findMatchingRecipe(@NotNull ItemStack[] matrix, @NotNull CraftingInventory inventory) {
        BukkitCraftingRecipe.InventoryType type = this.determineCraftingType(inventory);
        return ((BukkitCraftingRecipeManager) this.recipeManager).findMatchingRecipe(matrix, type);
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
    private BukkitCraftingRecipe.InventoryType determineCraftingType(@NotNull CraftingInventory inventory) {
        int matrixSize = inventory.getMatrix().length;

        switch (matrixSize) {
            case MATRIX_2X2:
                return BukkitCraftingRecipe.InventoryType.CRAFTING_TABLE_2X2;
            case MATRIX_3X3:
                return BukkitCraftingRecipe.InventoryType.CRAFTING_TABLE_3X3;
            default:
                this.logger.warning("Unexpected crafting matrix size: " + matrixSize);
                return BukkitCraftingRecipe.InventoryType.CRAFTING_TABLE_3X3;
        }
    }
}
