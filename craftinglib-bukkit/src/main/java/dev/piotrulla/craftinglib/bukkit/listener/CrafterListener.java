package dev.piotrulla.craftinglib.bukkit.listener;

import dev.piotrulla.craftinglib.recipe.RecipeMatch;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 * Applies exact matching to the crafter block (1.21), which otherwise crafts from the material-level server recipe.
 * A crafter always consumes one item per slot, so recipes with ingredient amounts above 1 are blocked there.
 * Register only when {@code VersionDetector.isCrafterSupported()} returns true.
 */
public final class CrafterListener implements Listener {

    private static final int SINGLE_ITEM = 1;

    private final CraftingGuard craftingGuard;

    public CrafterListener(@NotNull CraftingGuard craftingGuard) {
        this.craftingGuard = Objects.requireNonNull(craftingGuard, "craftingGuard");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCrafterCraft(@NotNull CrafterCraftEvent event) {
        BlockState state = event.getBlock().getState();
        if (!(state instanceof Container)) {
            return;
        }

        Optional<RecipeMatch<ItemStack>> match = this.craftingGuard.findMatch(((Container) state).getInventory().getContents());

        if (match.isPresent()) {
            if (this.consumesSingleItems(match.get())) {
                event.setResult(match.get().getRecipe().getResult());
            }
            else {
                event.setCancelled(true);
            }
            return;
        }

        if (this.craftingGuard.isBlocked(event.getRecipe())) {
            event.setCancelled(true);
        }
    }

    private boolean consumesSingleItems(RecipeMatch<ItemStack> match) {
        for (int slot = 0; slot < match.getSlotCount(); slot++) {
            if (match.getConsumption(slot) > SINGLE_ITEM) {
                return false;
            }
        }

        return true;
    }
}