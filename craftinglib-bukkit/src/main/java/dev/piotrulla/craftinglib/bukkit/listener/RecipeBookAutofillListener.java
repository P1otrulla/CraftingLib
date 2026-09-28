package dev.piotrulla.craftinglib.bukkit.listener;

import com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent;
import dev.piotrulla.craftinglib.bukkit.version.VersionAdapter;
import dev.piotrulla.craftinglib.grid.CraftingGrid;
import dev.piotrulla.craftinglib.recipe.CraftingRecipe;
import dev.piotrulla.craftinglib.recipe.manager.CraftingRecipeManager;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 * Paper: replaces vanilla recipe book autofill for our crafting recipes (see {@link GridAutofill}).
 * Other recipes, including our cooking recipes, are left to vanilla.
 * Register only when {@code VersionDetector.isRecipeBookAutofillSupported()} returns true.
 */
public final class RecipeBookAutofillListener implements Listener {

    private final CraftingRecipeManager<ItemStack> recipeManager;
    private final VersionAdapter versionAdapter;
    private final GridAutofill gridAutofill = new GridAutofill();

    public RecipeBookAutofillListener(@NotNull CraftingRecipeManager<ItemStack> recipeManager, @NotNull VersionAdapter versionAdapter) {
        this.recipeManager = Objects.requireNonNull(recipeManager, "recipeManager");
        this.versionAdapter = Objects.requireNonNull(versionAdapter, "versionAdapter");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onRecipeBookClick(@NotNull PlayerRecipeBookClickEvent event) {
        Optional<CraftingRecipe<ItemStack>> recipe = this.findOwnRecipe(event.getRecipe());
        if (recipe.isEmpty()) {
            return;
        }

        Player player = event.getPlayer();
        Inventory topInventory = player.getOpenInventory().getTopInventory();
        if (!(topInventory instanceof CraftingInventory)) {
            return;
        }

        CraftingInventory craftingInventory = (CraftingInventory) topInventory;
        if (!CraftingGrid.isSupportedSlotCount(craftingInventory.getMatrix().length)) {
            return;
        }

        event.setCancelled(true);
        this.gridAutofill.fill(player, craftingInventory, recipe.get(), event.isMakeAll());
    }

    private Optional<CraftingRecipe<ItemStack>> findOwnRecipe(NamespacedKey key) {
        if (!this.versionAdapter.isOwnedNamespace(key.getNamespace())) {
            return Optional.empty();
        }

        return this.recipeManager.findRecipe(key.getKey());
    }
}
