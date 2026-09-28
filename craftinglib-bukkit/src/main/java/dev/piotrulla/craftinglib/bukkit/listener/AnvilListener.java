package dev.piotrulla.craftinglib.bukkit.listener;

import dev.piotrulla.craftinglib.bukkit.version.VersionDetector;
import dev.piotrulla.craftinglib.recipe.AnvilRecipe;
import dev.piotrulla.craftinglib.recipe.manager.RecipeManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 * Shows custom anvil results and performs the take by hand: checks levels, consumes both inputs' amounts
 * and takes the level cost. Anvil damage is not simulated.
 * Register only when {@code VersionDetector.isAnvilSupported()} returns true.
 */
public final class AnvilListener implements Listener {

    private static final int LEFT_SLOT = 0;
    private static final int RIGHT_SLOT = 1;
    private static final int RESULT_SLOT = 2;
    private static final int SINGLE_CRAFT = 1;

    private final RecipeManager<AnvilRecipe<ItemStack>> anvilManager;
    private final ResultDelivery resultDelivery = new ResultDelivery();
    private final boolean anvilViewSupported = VersionDetector.isAnvilViewSupported();

    public AnvilListener(@NotNull RecipeManager<AnvilRecipe<ItemStack>> anvilManager) {
        this.anvilManager = Objects.requireNonNull(anvilManager, "anvilManager");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepareAnvil(@NotNull PrepareAnvilEvent event) {
        this.findMatch(event.getInventory()).ifPresent(recipe -> {
            event.setResult(recipe.getResult());
            this.setRepairCost(event, recipe.getLevelCost());
        });
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onAnvilClick(@NotNull InventoryClickEvent event) {
        if (event.getRawSlot() != RESULT_SLOT || !(event.getInventory() instanceof AnvilInventory)) {
            return;
        }

        AnvilInventory inventory = (AnvilInventory) event.getInventory();
        Optional<AnvilRecipe<ItemStack>> match = this.findMatch(inventory);
        if (match.isEmpty()) {
            return;
        }

        event.setCancelled(true);
        this.takeResult(event, inventory, match.get());
    }

    /**
     * 1.21+ keeps the repair cost in {@code AnvilView}; older servers only have {@link AnvilInventory#setRepairCost(int)}.
     */
    private void setRepairCost(PrepareAnvilEvent event, int levelCost) {
        if (this.anvilViewSupported) {
            event.getView().setRepairCost(levelCost);
            return;
        }

        this.setLegacyRepairCost(event.getInventory(), levelCost);
    }

    @SuppressWarnings({"deprecation", "removal"})
    private void setLegacyRepairCost(AnvilInventory inventory, int levelCost) {
        inventory.setRepairCost(levelCost);
    }

    private void takeResult(InventoryClickEvent event, AnvilInventory inventory, AnvilRecipe<ItemStack> recipe) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        boolean creative = player.getGameMode() == GameMode.CREATIVE;
        if (!creative && player.getLevel() < recipe.getLevelCost()) {
            return;
        }
        if (this.resultDelivery.deliver(event, recipe.getResult(), SINGLE_CRAFT) <= 0) {
            return;
        }

        InventorySlots.consume(inventory, LEFT_SLOT, recipe.getLeft().getAmount());
        InventorySlots.consume(inventory, RIGHT_SLOT, recipe.getRight().getAmount());
        if (!creative) {
            player.setLevel(player.getLevel() - recipe.getLevelCost());
        }

        this.resultDelivery.updateInventory(player);
    }

    private Optional<AnvilRecipe<ItemStack>> findMatch(AnvilInventory inventory) {
        ItemStack left = inventory.getItem(LEFT_SLOT);
        ItemStack right = inventory.getItem(RIGHT_SLOT);

        for (AnvilRecipe<ItemStack> recipe : this.anvilManager.getRecipes()) {
            if (recipe.matches(left, right)) {
                return Optional.of(recipe);
            }
        }

        return Optional.empty();
    }
}
