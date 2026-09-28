package dev.piotrulla.craftinglib.bukkit.listener;

import dev.piotrulla.craftinglib.bukkit.item.BukkitItemAdapter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

/**
 * Hands a result to the player after a result-slot click that was cancelled to be handled by hand.
 * <p>
 * Supported clicks: left/right (to cursor, once) and shift (to inventory, as many times as allowed and as fit).
 * Other clicks (number keys, drop) deliver nothing.
 */
final class ResultDelivery {

    private static final int PLAYER_STORAGE_SLOTS = 36;

    private final BukkitItemAdapter itemAdapter = BukkitItemAdapter.INSTANCE;

    /**
     * @param maxCrafts upper limit for shift-clicks
     * @return how many results were delivered (0 = nothing happened, consume nothing)
     */
    int deliver(@NotNull InventoryClickEvent event, @NotNull ItemStack result, int maxCrafts) {
        ClickType click = event.getClick();

        if (click == ClickType.SHIFT_LEFT || click == ClickType.SHIFT_RIGHT) {
            return this.deliverToInventory(event.getWhoClicked().getInventory(), result, maxCrafts);
        }
        if (click == ClickType.LEFT || click == ClickType.RIGHT) {
            return this.deliverToCursor(event.getWhoClicked(), result);
        }

        return 0;
    }

    void updateInventory(@NotNull HumanEntity human) {
        if (human instanceof Player) {
            ((Player) human).updateInventory();
        }
    }

    /**
     * Uses {@link HumanEntity} cursor methods instead of InventoryView, which became an interface in 1.21.
     */
    private int deliverToCursor(HumanEntity human, ItemStack result) {
        ItemStack cursor = human.getItemOnCursor();

        if (this.itemAdapter.isEmpty(cursor)) {
            human.setItemOnCursor(result);
            return 1;
        }

        int mergedAmount = cursor.getAmount() + result.getAmount();
        if (!cursor.isSimilar(result) || mergedAmount > this.maxStackSize(cursor)) {
            return 0;
        }

        ItemStack merged = cursor.clone();
        merged.setAmount(mergedAmount);
        human.setItemOnCursor(merged);
        return 1;
    }

    private int deliverToInventory(PlayerInventory inventory, ItemStack result, int maxCrafts) {
        int crafts = Math.min(maxCrafts, this.freeSpaceFor(inventory, result) / result.getAmount());
        if (crafts <= 0) {
            return 0;
        }

        this.addInStacks(inventory, result, crafts * result.getAmount());
        return crafts;
    }

    private int freeSpaceFor(PlayerInventory inventory, ItemStack result) {
        int maxStackSize = this.maxStackSize(result);
        int freeSpace = 0;

        for (int slot = 0; slot < PLAYER_STORAGE_SLOTS; slot++) {
            ItemStack item = inventory.getItem(slot);

            if (this.itemAdapter.isEmpty(item)) {
                freeSpace += maxStackSize;
            }
            else if (item.isSimilar(result)) {
                freeSpace += Math.max(0, maxStackSize - item.getAmount());
            }
        }

        return freeSpace;
    }

    private void addInStacks(PlayerInventory inventory, ItemStack result, int totalAmount) {
        int maxStackSize = this.maxStackSize(result);
        int remaining = totalAmount;

        while (remaining > 0) {
            ItemStack stack = result.clone();
            stack.setAmount(Math.min(remaining, maxStackSize));
            inventory.addItem(stack);
            remaining -= stack.getAmount();
        }
    }

    private int maxStackSize(ItemStack item) {
        return Math.max(1, item.getMaxStackSize());
    }
}