package dev.piotrulla.craftingshowcase.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

/**
 * Menus are read-only: every click and drag is cancelled, clicks inside the menu go to the {@link Menu}.
 */
public final class MenuListener implements Listener {

    @EventHandler
    public void onClick(@NotNull InventoryClickEvent event) {
        Inventory topInventory = event.getInventory();
        InventoryHolder holder = topInventory.getHolder();
        if (!(holder instanceof Menu)) {
            return;
        }

        event.setCancelled(true);

        int slot = event.getRawSlot();
        boolean insideMenu = slot >= 0 && slot < topInventory.getSize();
        if (insideMenu && event.getWhoClicked() instanceof Player) {
            ((Menu) holder).handleClick((Player) event.getWhoClicked(), slot, event.getClick());
        }
    }

    @EventHandler
    public void onDrag(@NotNull InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof Menu) {
            event.setCancelled(true);
        }
    }
}
