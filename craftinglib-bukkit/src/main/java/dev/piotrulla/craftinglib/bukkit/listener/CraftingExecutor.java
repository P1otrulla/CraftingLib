package dev.piotrulla.craftinglib.bukkit.listener;

import dev.piotrulla.craftinglib.recipe.RecipeMatch;
import org.bukkit.Material;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Crafts a matched custom recipe by hand (the event is already cancelled), so ingredient amounts
 * and exact matching work the same on every server version.
 */
final class CraftingExecutor {

    private static final String BUCKET_SUFFIX = "_BUCKET";

    private final ResultDelivery resultDelivery = new ResultDelivery();

    void craft(@NotNull CraftItemEvent event, @NotNull RecipeMatch<ItemStack> match) {
        int crafts = this.resultDelivery.deliver(event, match.getRecipe().getResult(), match.getMaxCrafts());
        if (crafts <= 0) {
            return;
        }

        this.consumeIngredients(event.getInventory(), match, crafts);
        this.resultDelivery.updateInventory(event.getWhoClicked());
    }

    private void consumeIngredients(CraftingInventory inventory, RecipeMatch<ItemStack> match, int crafts) {
        ItemStack[] matrix = inventory.getMatrix();

        for (int slot = 0; slot < matrix.length; slot++) {
            int consumed = match.getConsumption(slot) * crafts;
            if (consumed > 0) {
                matrix[slot] = this.reduce(matrix[slot], consumed);
            }
        }

        inventory.setMatrix(matrix);
    }

    @Nullable
    private ItemStack reduce(ItemStack item, int consumed) {
        int remaining = item.getAmount() - consumed;
        if (remaining <= 0) {
            return this.craftingRemainder(item);
        }

        ItemStack reduced = item.clone();
        reduced.setAmount(remaining);
        return reduced;
    }

    /**
     * Vanilla leaves an empty bucket after using milk/water/lava buckets.
     */
    @Nullable
    private ItemStack craftingRemainder(ItemStack item) {
        return item.getType().name().endsWith(BUCKET_SUFFIX) ? new ItemStack(Material.BUCKET) : null;
    }
}