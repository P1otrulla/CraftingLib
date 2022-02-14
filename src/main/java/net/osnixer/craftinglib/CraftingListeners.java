package net.osnixer.craftinglib;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import panda.std.Option;

public final class CraftingListeners implements Listener {

    private final CraftingManager manager;

    public CraftingListeners(CraftingManager craftingManager){
        this.manager = craftingManager;
    }

    @EventHandler
    public void onCraft(CraftItemEvent event){
        ItemStack result = event.getRecipe().getResult();
        Option<Crafting> craftingOption = this.manager.findCrafting(result);
        CraftingInventory inventory = event.getInventory();

        craftingOption.filter(Crafting::isCustom).peek(crafting -> {
            for (int i = 1; i <= 9; i++) {
                if (inventory.getItem(i) != null) {
                    ItemStack inventoryItem = inventory.getItem(i);
                    ItemStack itemStack = crafting.getItems()[i - 1];

                    if (inventoryItem.getType() != itemStack.getType()) {
                        event.setCancelled(true);
                    }
                    if (inventoryItem.getAmount() < itemStack.getAmount()) {
                        event.setCancelled(true);
                    }
                }
            }
            inventory.setResult(new ItemStack(Material.AIR));
            if (!event.isCancelled()){
                for (int i = 1; i <= 9; i++) {
                    CraftingUtils.removeItem(inventory, i, crafting.getItems()[i - 1].getAmount() - 1);
                    inventory.setResult(crafting.getResult());
                }
            }
        });
    }

    @EventHandler
    public void onPrepare(PrepareItemCraftEvent event){
        Option<ItemStack> resultOption = Option.of(event.getRecipe().getResult());

        resultOption.peek(result -> {
            Option<Crafting> craftingOption = this.manager.findCrafting(result);
            CraftingInventory inventory = event.getInventory();

            craftingOption.filter(Crafting::isCustom).peek(crafting -> {
                for (int i = 1; i <= 9; i++) {
                    if (inventory.getItem(i) != null) {
                        ItemStack inventoryItem = inventory.getItem(i);
                        ItemStack itemStack = crafting.getItems()[i - 1];

                        if (inventoryItem.getType() != itemStack.getType()) {
                            inventory.setResult(new ItemStack(Material.AIR));
                        }
                        if (inventoryItem.getAmount() < itemStack.getAmount()) {
                            inventory.setResult(new ItemStack(Material.AIR));
                        }
                        if (!inventoryItem.getItemMeta().equals(itemStack.getItemMeta())){
                            inventory.setResult(new ItemStack(Material.AIR));
                        }
                    }
                }
            });
        });
    }
}