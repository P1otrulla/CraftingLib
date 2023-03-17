package dev.piotrulla.craftinglib;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Optional;

public class CraftingController implements Listener {

    private final CraftingManager manager;

    public CraftingController(CraftingManager craftingManager) {
        this.manager = craftingManager;
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        ItemStack result = event.getRecipe().getResult();
        Optional<Crafting> craftingOptional = this.manager.findCrafting(result);
        CraftingInventory inventory = event.getInventory();

        craftingOptional.filter(Crafting::isCustom).ifPresent(crafting -> {
            for (int i = 1; i <= 9; i++) {
                if (inventory.getItem(i) != null) {
                    ItemStack inventoryItem = inventory.getItem(i);
                    ItemStack itemStack = crafting.items()[i - 1];

                    if (inventoryItem.getType() != itemStack.getType()) {
                        event.setCancelled(true);
                    }

                    if (inventoryItem.getAmount() < itemStack.getAmount()) {
                        event.setCancelled(true);
                    }

                    if (!inventoryItem.getItemMeta().equals(itemStack.getItemMeta())){
                        event.setCancelled(true);
                    }
                }
            }

            inventory.setResult(new ItemStack(Material.AIR));

            if (!event.isCancelled()) {
                if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                    event.setCancelled(true);
                    inventory.setResult(crafting.result());

                    return;
                }

                for (int i = 1; i <= 9; i++) {
                    int amount = crafting.items()[i - 1].getAmount() - 1;

                    CraftingUtil.removeItem(inventory, i, amount);
                    inventory.setResult(crafting.result());
                }
            }
        });
    }

    @EventHandler
    public void onPrepare(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();

        if (recipe == null) {
            return;
        }

        ItemStack result = recipe.getResult();

        if (result == null) {
            return;
        }

        Optional<Crafting> craftingOptional = this.manager.findCrafting(result);
        CraftingInventory inventory = event.getInventory();

        craftingOptional.filter(Crafting::isCustom).ifPresent(crafting -> {
            for (int i = 1; i <= 9; i++) {
                if (inventory.getItem(i) != null) {
                    ItemStack inventoryItem = inventory.getItem(i);
                    ItemStack itemStack = crafting.items()[i - 1];

                    if (inventoryItem.getType() != itemStack.getType()) {
                        inventory.setResult(new ItemStack(Material.AIR));
                    }

                    if (inventoryItem.getAmount() < itemStack.getAmount()) {
                        inventory.setResult(new ItemStack(Material.AIR));
                    }

                    if (!inventoryItem.getItemMeta().equals(itemStack.getItemMeta())) {
                        inventory.setResult(new ItemStack(Material.AIR));
                    }
                }
            }
        });
    }
}
