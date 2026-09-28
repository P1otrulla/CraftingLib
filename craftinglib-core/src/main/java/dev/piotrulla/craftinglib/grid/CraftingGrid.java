package dev.piotrulla.craftinglib.grid;

import dev.piotrulla.craftinglib.item.ItemAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Immutable snapshot of a square crafting grid: 2x2 (player inventory) or 3x3 (workbench).
 * Slots are indexed row by row, starting at the top-left corner. Empty slots may be {@code null}.
 *
 * @param <T> platform item type
 */
public final class CraftingGrid<T> {

    public static final int INVENTORY_SIZE = 2;
    public static final int WORKBENCH_SIZE = 3;

    private final List<T> slots;
    private final int size;

    private CraftingGrid(List<T> slots, int size) {
        this.slots = slots;
        this.size = size;
    }

    @NotNull
    public static <T> CraftingGrid<T> of(@NotNull List<T> slots) {
        Objects.requireNonNull(slots, "slots");

        int size = sideLengthOf(slots.size());
        return new CraftingGrid<>(Collections.unmodifiableList(new ArrayList<>(slots)), size);
    }

    @NotNull
    public static <T> CraftingGrid<T> fromArray(@NotNull T[] matrix) {
        Objects.requireNonNull(matrix, "matrix");
        return of(Arrays.asList(matrix));
    }

    public static boolean isSupportedSlotCount(int slotCount) {
        return slotCount == INVENTORY_SIZE * INVENTORY_SIZE || slotCount == WORKBENCH_SIZE * WORKBENCH_SIZE;
    }

    private static int sideLengthOf(int slotCount) {
        if (slotCount == INVENTORY_SIZE * INVENTORY_SIZE) {
            return INVENTORY_SIZE;
        }
        if (slotCount == WORKBENCH_SIZE * WORKBENCH_SIZE) {
            return WORKBENCH_SIZE;
        }

        throw new IllegalArgumentException("Crafting grid must have 4 or 9 slots, got: " + slotCount);
    }

    /**
     * @return side length of the grid (2 or 3)
     */
    public int getSize() {
        return this.size;
    }

    public int getSlotCount() {
        return this.slots.size();
    }

    @Nullable
    public T get(int slot) {
        return this.slots.get(slot);
    }

    @Nullable
    public T get(int column, int row) {
        return this.slots.get(this.indexOf(column, row));
    }

    public int indexOf(int column, int row) {
        return indexOf(this.size, column, row);
    }

    /**
     * @param size side length of the grid
     * @return slot index of the position, rows are stored one after another
     */
    public static int indexOf(int size, int column, int row) {
        if (column < 0 || column >= size || row < 0 || row >= size) {
            throw new IndexOutOfBoundsException("Position (" + column + ", " + row + ") is outside of "
                    + size + "x" + size + " grid");
        }

        return row * size + column;
    }

    /**
     * @return indexes of all non-empty slots, in grid order
     */
    @NotNull
    public List<Integer> findOccupiedSlots(@NotNull ItemAdapter<T> itemAdapter) {
        Objects.requireNonNull(itemAdapter, "itemAdapter");

        List<Integer> occupiedSlots = new ArrayList<>();
        for (int slot = 0; slot < this.slots.size(); slot++) {
            if (!itemAdapter.isEmpty(this.slots.get(slot))) {
                occupiedSlots.add(slot);
            }
        }

        return occupiedSlots;
    }

    /**
     * @return smallest rectangle containing every non-empty slot, or empty when the grid is empty
     */
    @NotNull
    public Optional<GridArea> findOccupiedArea(@NotNull ItemAdapter<T> itemAdapter) {
        List<Integer> occupiedSlots = this.findOccupiedSlots(itemAdapter);
        if (occupiedSlots.isEmpty()) {
            return Optional.empty();
        }

        int minColumn = this.size;
        int minRow = this.size;
        int maxColumn = -1;
        int maxRow = -1;

        for (int slot : occupiedSlots) {
            int column = slot % this.size;
            int row = slot / this.size;

            minColumn = Math.min(minColumn, column);
            minRow = Math.min(minRow, row);
            maxColumn = Math.max(maxColumn, column);
            maxRow = Math.max(maxRow, row);
        }

        return Optional.of(new GridArea(minColumn, minRow, maxColumn - minColumn + 1, maxRow - minRow + 1));
    }
}
