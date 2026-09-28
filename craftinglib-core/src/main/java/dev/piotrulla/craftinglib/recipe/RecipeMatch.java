package dev.piotrulla.craftinglib.recipe;

import dev.piotrulla.craftinglib.grid.CraftingGrid;
import dev.piotrulla.craftinglib.item.ItemAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Result of matching a recipe against a grid: which slots are consumed and how many times it can be crafted.
 *
 * @param <T> platform item type
 */
public final class RecipeMatch<T> {

    private final CraftingRecipe<T> recipe;
    private final int[] consumption;
    private final int maxCrafts;

    private RecipeMatch(CraftingRecipe<T> recipe, int[] consumption, int maxCrafts) {
        this.recipe = recipe;
        this.consumption = consumption;
        this.maxCrafts = maxCrafts;
    }

    /**
     * @param consumption amount consumed from each grid slot per single craft (0 = slot untouched)
     */
    @NotNull
    public static <T> RecipeMatch<T> of(
            @NotNull CraftingRecipe<T> recipe,
            @NotNull CraftingGrid<T> grid,
            int[] consumption,
            @NotNull ItemAdapter<T> itemAdapter
    ) {
        if (consumption.length != grid.getSlotCount()) {
            throw new IllegalArgumentException("Consumption must cover " + grid.getSlotCount()
                    + " slots, got: " + consumption.length);
        }

        return new RecipeMatch<>(recipe, consumption.clone(), calculateMaxCrafts(grid, consumption, itemAdapter));
    }

    private static <T> int calculateMaxCrafts(CraftingGrid<T> grid, int[] consumption, ItemAdapter<T> itemAdapter) {
        int maxCrafts = Integer.MAX_VALUE;

        for (int slot = 0; slot < consumption.length; slot++) {
            if (consumption[slot] <= 0) {
                continue;
            }

            T item = grid.get(slot);
            int available = itemAdapter.isEmpty(item) ? 0 : itemAdapter.getAmount(item);
            maxCrafts = Math.min(maxCrafts, available / consumption[slot]);
        }

        return maxCrafts == Integer.MAX_VALUE ? 0 : maxCrafts;
    }

    @NotNull
    public CraftingRecipe<T> getRecipe() {
        return this.recipe;
    }

    /**
     * @return amount taken from the slot per single craft
     */
    public int getConsumption(int slot) {
        return this.consumption[slot];
    }

    public int getSlotCount() {
        return this.consumption.length;
    }

    /**
     * @return how many times the recipe can be crafted with the items currently in the grid
     */
    public int getMaxCrafts() {
        return this.maxCrafts;
    }

    @Override
    public String toString() {
        return "RecipeMatch{recipe=" + this.recipe.getKey()
                + ", consumption=" + Arrays.toString(this.consumption)
                + ", maxCrafts=" + this.maxCrafts + '}';
    }
}
