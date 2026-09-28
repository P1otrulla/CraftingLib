package dev.piotrulla.craftinglib.recipe;

import dev.piotrulla.craftinglib.grid.CraftingGrid;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Recipe where only the ingredients matter, not their positions.
 * Every ingredient occupies exactly one grid slot.
 *
 * @param <T> platform item type
 */
public final class ShapelessCraftingRecipe<T> extends AbstractCraftingRecipe<T> {

    public static final int MAX_INGREDIENTS = CraftingGrid.WORKBENCH_SIZE * CraftingGrid.WORKBENCH_SIZE;

    private final List<T> ingredients;

    public ShapelessCraftingRecipe(@NotNull RecipeSettings<T> settings, @NotNull List<T> ingredients) {
        super(settings);
        this.ingredients = this.copyIngredients(ingredients);
        this.requireCraftingCategory();
    }

    private List<T> copyIngredients(List<T> source) {
        if (source.isEmpty() || source.size() > MAX_INGREDIENTS) {
            throw new IllegalArgumentException("Recipe '" + this.getKey() + "' must have 1-" + MAX_INGREDIENTS
                    + " ingredients, got: " + source.size());
        }

        List<T> copy = new ArrayList<>();
        for (T ingredient : source) {
            if (this.itemAdapter.isEmpty(ingredient)) {
                throw new IllegalArgumentException("Recipe '" + this.getKey() + "': ingredient cannot be null or empty");
            }

            copy.add(this.itemAdapter.copy(ingredient));
        }

        return Collections.unmodifiableList(copy);
    }

    @NotNull
    @Override
    public RecipeType getType() {
        return RecipeType.SHAPELESS;
    }

    /**
     * @return copies of the ingredients
     */
    @NotNull
    public List<T> getIngredients() {
        List<T> copy = new ArrayList<>();
        for (T ingredient : this.ingredients) {
            copy.add(this.itemAdapter.copy(ingredient));
        }

        return copy;
    }

    @Override
    public boolean fitsGrid(int gridSize) {
        return this.ingredients.size() <= gridSize * gridSize;
    }

    @NotNull
    @Override
    public Map<Integer, T> getGridLayout(int gridSize) {
        this.requireFitsGrid(gridSize);

        Map<Integer, T> layout = new LinkedHashMap<>();
        for (int slot = 0; slot < this.ingredients.size(); slot++) {
            layout.put(slot, this.itemAdapter.copy(this.ingredients.get(slot)));
        }

        return layout;
    }

    @NotNull
    @Override
    public Optional<RecipeMatch<T>> match(@NotNull CraftingGrid<T> grid) {
        List<Integer> occupiedSlots = grid.findOccupiedSlots(this.itemAdapter);
        if (occupiedSlots.size() != this.ingredients.size()) {
            return Optional.empty();
        }

        int[] consumption = new int[grid.getSlotCount()];
        if (!this.assignIngredients(0, grid, occupiedSlots, consumption)) {
            return Optional.empty();
        }

        return Optional.of(RecipeMatch.of(this, grid, consumption, this.itemAdapter));
    }

    /**
     * Backtracking assignment of ingredients to slots. Greedy is not enough when ingredients overlap,
     * e.g. "any diamond" + "named diamond" with the named one placed first.
     * A slot with consumption greater than 0 is already taken (ingredient amounts are always at least 1).
     */
    private boolean assignIngredients(int ingredientIndex, CraftingGrid<T> grid, List<Integer> slots, int[] consumption) {
        if (ingredientIndex == this.ingredients.size()) {
            return true;
        }

        T required = this.ingredients.get(ingredientIndex);
        for (int slot : slots) {
            if (consumption[slot] > 0 || !this.matchesIngredient(required, grid.get(slot))) {
                continue;
            }

            consumption[slot] = this.itemAdapter.getAmount(required);
            if (this.assignIngredients(ingredientIndex + 1, grid, slots, consumption)) {
                return true;
            }
            consumption[slot] = 0;
        }

        return false;
    }
}
