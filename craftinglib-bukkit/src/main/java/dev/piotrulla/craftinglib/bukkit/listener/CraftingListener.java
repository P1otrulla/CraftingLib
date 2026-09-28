package dev.piotrulla.craftinglib.bukkit.listener;

import dev.piotrulla.craftinglib.recipe.RecipeMatch;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 * The crafting grid is the source of truth: the result shown and crafted always comes from our recipes,
 * never from the server recipe alone.
 */
public final class CraftingListener implements Listener {

    private final CraftingGuard craftingGuard;
    private final CraftingExecutor craftingExecutor = new CraftingExecutor();

    public CraftingListener(@NotNull CraftingGuard craftingGuard) {
        this.craftingGuard = Objects.requireNonNull(craftingGuard, "craftingGuard");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepareItemCraft(@NotNull PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();
        Optional<RecipeMatch<ItemStack>> match = this.craftingGuard.findMatch(inventory.getMatrix());

        if (match.isPresent()) {
            inventory.setResult(match.get().getRecipe().getResult());
            return;
        }

        if (this.craftingGuard.isBlocked(event.getRecipe())) {
            inventory.setResult(new ItemStack(Material.AIR));
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCraftItem(@NotNull CraftItemEvent event) {
        Optional<RecipeMatch<ItemStack>> match = this.craftingGuard.findMatch(event.getInventory().getMatrix());

        if (match.isPresent()) {
            event.setCancelled(true);
            this.craftingExecutor.craft(event, match.get());
            return;
        }

        if (this.craftingGuard.isBlocked(event.getRecipe())) {
            event.setCancelled(true);
        }
    }
}