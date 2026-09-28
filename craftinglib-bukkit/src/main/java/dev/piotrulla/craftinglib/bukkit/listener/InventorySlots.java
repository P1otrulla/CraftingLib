package dev.piotrulla.craftinglib.bukkit.listener;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Slot operations shared by station listeners.
 */
final class InventorySlots {

    private InventorySlots() {
    }

    /**
     * Removes {@code amount} items from the slot, clearing it when nothing is left.
     */
    static void consume(@NotNull Inventory inventory, int slot, int amount) {
        ItemStack item = inventory.getItem(slot);
        if (item == null) {
            throw new IllegalStateException("Slot " + slot + " is empty, cannot consume " + amount + " items");
        }

        int remaining = item.getAmount() - amount;
        if (remaining <= 0) {
            inventory.setItem(slot, null);
            return;
        }

        ItemStack reduced = item.clone();
        reduced.setAmount(remaining);
        inventory.setItem(slot, reduced);
    }
}
