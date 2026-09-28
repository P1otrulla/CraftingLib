package dev.piotrulla.craftinglib.bukkit.listener;

import dev.piotrulla.craftinglib.bukkit.item.BukkitItemAdapter;
import dev.piotrulla.craftinglib.grid.CraftingGrid;
import dev.piotrulla.craftinglib.item.MatchMode;
import dev.piotrulla.craftinglib.recipe.CraftingRecipe;
import dev.piotrulla.craftinglib.recipe.RecipeMatch;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

/**
 * Recipe book autofill done by the library, so ingredients are picked with the recipe {@link MatchMode}
 * and ingredient amounts, which the vanilla (material based) autofill cannot do.
 * <p>
 * Items already in the grid go back to the player first. Every craft is simulated on a copy of the player's
 * storage and committed only when all ingredients of that craft were found.
 */
final class GridAutofill {

    private static final int PLAYER_STORAGE_SLOTS = 36;
    private static final int NO_CRAFTS = 0;
    private static final int ONE_MORE_CRAFT = 1;

    private final BukkitItemAdapter itemAdapter = BukkitItemAdapter.INSTANCE;

    /**
     * @param makeAll true to fill as many crafts as possible (shift click), false to add a single craft
     * @return number of crafts placed in the grid
     */
    int fill(@NotNull Player player, @NotNull CraftingInventory inventory, @NotNull CraftingRecipe<ItemStack> recipe, boolean makeAll) {
        CraftingGrid<ItemStack> grid = CraftingGrid.fromArray(inventory.getMatrix());
        if (!recipe.fitsGrid(grid.getSize())) {
            return NO_CRAFTS;
        }

        int targetCrafts = makeAll ? Integer.MAX_VALUE : this.craftsInGrid(recipe, grid) + ONE_MORE_CRAFT;
        this.returnGridItems(player, inventory);

        ItemStack[] storage = this.copyStorage(player.getInventory());
        ItemStack[] matrix = new ItemStack[grid.getSlotCount()];
        Map<Integer, ItemStack> layout = recipe.getGridLayout(grid.getSize());

        int crafts = NO_CRAFTS;
        while (crafts < targetCrafts) {
            ItemStack[] nextStorage = this.deepCopy(storage);
            ItemStack[] nextMatrix = this.deepCopy(matrix);
            if (!this.placeCraft(layout, recipe.getMatchMode(), nextStorage, nextMatrix)) {
                break;
            }

            storage = nextStorage;
            matrix = nextMatrix;
            crafts++;
        }

        if (crafts > NO_CRAFTS) {
            this.writeStorage(player.getInventory(), storage);
            inventory.setMatrix(matrix);
        }

        player.updateInventory();
        return crafts;
    }

    private int craftsInGrid(CraftingRecipe<ItemStack> recipe, CraftingGrid<ItemStack> grid) {
        Optional<RecipeMatch<ItemStack>> match = recipe.match(grid);
        return match.map(RecipeMatch::getMaxCrafts).orElse(NO_CRAFTS);
    }

    /**
     * Items that do not fit into the player's inventory are dropped at the player.
     */
    private void returnGridItems(Player player, CraftingInventory inventory) {
        ItemStack[] matrix = inventory.getMatrix();

        for (int slot = 0; slot < matrix.length; slot++) {
            if (this.itemAdapter.isEmpty(matrix[slot])) {
                continue;
            }

            for (ItemStack leftover : player.getInventory().addItem(matrix[slot]).values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), leftover);
            }
            matrix[slot] = null;
        }

        inventory.setMatrix(matrix);
    }

    private boolean placeCraft(Map<Integer, ItemStack> layout, MatchMode matchMode, ItemStack[] storage, ItemStack[] matrix) {
        for (Map.Entry<Integer, ItemStack> entry : layout.entrySet()) {
            if (!this.placeIngredient(entry.getValue(), matchMode, storage, matrix, entry.getKey())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Takes the ingredient amount from storage. A grid slot holds one stack, so once a slot has items,
     * only items stacking with them are taken.
     */
    private boolean placeIngredient(ItemStack required, MatchMode matchMode, ItemStack[] storage, ItemStack[] matrix, int gridSlot) {
        ItemStack placed = matrix[gridSlot];
        int missing = required.getAmount();

        if (placed != null && placed.getAmount() + missing > placed.getMaxStackSize()) {
            return false;
        }

        for (int storageSlot = 0; storageSlot < storage.length && missing > 0; storageSlot++) {
            ItemStack candidate = storage[storageSlot];
            if (this.itemAdapter.isEmpty(candidate) || !this.accepts(placed, required, candidate, matchMode)) {
                continue;
            }

            int taken = Math.min(missing, candidate.getAmount());
            placed = this.addToPlaced(placed, candidate, taken);
            storage[storageSlot] = this.reduce(candidate, taken);
            missing -= taken;
        }

        matrix[gridSlot] = placed;
        return missing == 0;
    }

    private boolean accepts(ItemStack placed, ItemStack required, ItemStack candidate, MatchMode matchMode) {
        return placed == null
                ? this.itemAdapter.isSimilar(required, candidate, matchMode)
                : placed.isSimilar(candidate);
    }

    private ItemStack addToPlaced(ItemStack placed, ItemStack candidate, int amount) {
        ItemStack result = placed == null ? candidate.clone() : placed.clone();
        result.setAmount(placed == null ? amount : placed.getAmount() + amount);
        return result;
    }

    private ItemStack reduce(ItemStack item, int amount) {
        int remaining = item.getAmount() - amount;
        if (remaining <= 0) {
            return null;
        }

        ItemStack reduced = item.clone();
        reduced.setAmount(remaining);
        return reduced;
    }

    private ItemStack[] copyStorage(PlayerInventory inventory) {
        ItemStack[] storage = new ItemStack[PLAYER_STORAGE_SLOTS];
        for (int slot = 0; slot < PLAYER_STORAGE_SLOTS; slot++) {
            ItemStack item = inventory.getItem(slot);
            storage[slot] = item == null ? null : item.clone();
        }

        return storage;
    }

    private void writeStorage(PlayerInventory inventory, ItemStack[] storage) {
        for (int slot = 0; slot < PLAYER_STORAGE_SLOTS; slot++) {
            inventory.setItem(slot, storage[slot]);
        }
    }

    private ItemStack[] deepCopy(ItemStack[] items) {
        ItemStack[] copy = new ItemStack[items.length];
        for (int slot = 0; slot < items.length; slot++) {
            copy[slot] = items[slot] == null ? null : items[slot].clone();
        }

        return copy;
    }
}
