package dev.piotrulla.craftinglib.bukkit.listener;

import dev.piotrulla.craftinglib.recipe.GrindstoneRecipe;
import dev.piotrulla.craftinglib.recipe.manager.RecipeManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 * Shows custom grindstone results and performs the take by hand: consumes the inputs' amounts
 * and gives the recipe experience. Inputs may be in either slot.
 * Register only when {@code VersionDetector.isGrindstoneSupported()} returns true.
 */
public final class GrindstoneListener implements Listener {

    private static final int UPPER_SLOT = 0;
    private static final int LOWER_SLOT = 1;
    private static final int RESULT_SLOT = 2;
    private static final int SINGLE_CRAFT = 1;

    private final RecipeManager<GrindstoneRecipe<ItemStack>> grindstoneManager;
    private final ResultDelivery resultDelivery = new ResultDelivery();

    public GrindstoneListener(@NotNull RecipeManager<GrindstoneRecipe<ItemStack>> grindstoneManager) {
        this.grindstoneManager = Objects.requireNonNull(grindstoneManager, "grindstoneManager");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepareGrindstone(@NotNull PrepareGrindstoneEvent event) {
        this.findMatch(event.getInventory()).ifPresent(match -> event.setResult(match.getRecipe().getResult()));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onGrindstoneClick(@NotNull InventoryClickEvent event) {
        if (event.getRawSlot() != RESULT_SLOT || !(event.getInventory() instanceof GrindstoneInventory)) {
            return;
        }

        GrindstoneInventory inventory = (GrindstoneInventory) event.getInventory();
        Optional<GrindstoneMatch> match = this.findMatch(inventory);
        if (!match.isPresent()) {
            return;
        }

        event.setCancelled(true);
        this.takeResult(event, inventory, match.get());
    }

    private void takeResult(InventoryClickEvent event, GrindstoneInventory inventory, GrindstoneMatch match) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        GrindstoneRecipe<ItemStack> recipe = match.getRecipe();
        if (this.resultDelivery.deliver(event, recipe.getResult(), SINGLE_CRAFT) <= 0) {
            return;
        }

        InventorySlots.consume(inventory, match.getInputSlot(), recipe.getInput().getAmount());
        recipe.getSecondInput().ifPresent(secondInput ->
                InventorySlots.consume(inventory, match.getSecondInputSlot(), secondInput.getAmount()));

        Player player = (Player) event.getWhoClicked();
        if (recipe.getExperience() > GrindstoneRecipe.NO_EXPERIENCE) {
            player.giveExp(recipe.getExperience());
        }

        this.resultDelivery.updateInventory(player);
    }

    private Optional<GrindstoneMatch> findMatch(GrindstoneInventory inventory) {
        ItemStack upper = inventory.getItem(UPPER_SLOT);
        ItemStack lower = inventory.getItem(LOWER_SLOT);

        for (GrindstoneRecipe<ItemStack> recipe : this.grindstoneManager.getRecipes()) {
            if (recipe.matches(upper, lower)) {
                return Optional.of(new GrindstoneMatch(recipe, UPPER_SLOT, LOWER_SLOT));
            }
            if (recipe.matches(lower, upper)) {
                return Optional.of(new GrindstoneMatch(recipe, LOWER_SLOT, UPPER_SLOT));
            }
        }

        return Optional.empty();
    }

    /**
     * Recipe plus the slots its inputs were found in.
     */
    private static final class GrindstoneMatch {

        private final GrindstoneRecipe<ItemStack> recipe;
        private final int inputSlot;
        private final int secondInputSlot;

        private GrindstoneMatch(GrindstoneRecipe<ItemStack> recipe, int inputSlot, int secondInputSlot) {
            this.recipe = recipe;
            this.inputSlot = inputSlot;
            this.secondInputSlot = secondInputSlot;
        }

        private GrindstoneRecipe<ItemStack> getRecipe() {
            return this.recipe;
        }

        private int getInputSlot() {
            return this.inputSlot;
        }

        private int getSecondInputSlot() {
            return this.secondInputSlot;
        }
    }
}
