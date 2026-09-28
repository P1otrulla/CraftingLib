package dev.piotrulla.craftinglib.recipe;

import dev.piotrulla.craftinglib.grid.CraftingGrid;
import dev.piotrulla.craftinglib.grid.GridArea;
import dev.piotrulla.craftinglib.pattern.CraftingPattern;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Recipe whose ingredients must form a {@link CraftingPattern}.
 *
 * @param <T> platform item type
 */
public final class ShapedCraftingRecipe<T> extends AbstractCraftingRecipe<T> {

    private final CraftingPattern pattern;
    private final Map<Character, T> ingredients;
    private final boolean mirrored;

    /**
     * @param mirrored whether a horizontally mirrored layout is also accepted (vanilla behaviour)
     */
    public ShapedCraftingRecipe(
            @NotNull RecipeSettings<T> settings,
            @NotNull CraftingPattern pattern,
            @NotNull Map<Character, T> ingredients,
            boolean mirrored
    ) {
        super(settings);
        this.pattern = pattern;
        this.ingredients = this.copyIngredients(ingredients);
        this.mirrored = mirrored;
        this.requireCraftingCategory();
    }

    private Map<Character, T> copyIngredients(Map<Character, T> source) {
        Set<Character> symbols = this.pattern.getSymbols();

        for (Character symbol : symbols) {
            if (!source.containsKey(symbol)) {
                throw new IllegalArgumentException("Recipe '" + this.getKey() + "': pattern symbol '"
                        + symbol + "' has no ingredient");
            }
        }

        Map<Character, T> copy = new LinkedHashMap<>();
        for (Map.Entry<Character, T> entry : source.entrySet()) {
            char symbol = entry.getKey();
            if (!symbols.contains(symbol)) {
                throw new IllegalArgumentException("Recipe '" + this.getKey() + "': ingredient '"
                        + symbol + "' is not used in pattern " + this.pattern.getRows());
            }
            if (this.itemAdapter.isEmpty(entry.getValue())) {
                throw new IllegalArgumentException("Recipe '" + this.getKey() + "': ingredient '"
                        + symbol + "' cannot be null or empty");
            }

            copy.put(symbol, this.itemAdapter.copy(entry.getValue()));
        }

        return Collections.unmodifiableMap(copy);
    }

    @NotNull
    @Override
    public RecipeType getType() {
        return RecipeType.SHAPED;
    }

    @NotNull
    public CraftingPattern getPattern() {
        return this.pattern;
    }

    /**
     * @return copies of the ingredients, keyed by pattern symbol
     */
    @NotNull
    public Map<Character, T> getIngredients() {
        Map<Character, T> copy = new LinkedHashMap<>();
        this.ingredients.forEach((symbol, ingredient) -> copy.put(symbol, this.itemAdapter.copy(ingredient)));
        return copy;
    }

    public boolean isMirrored() {
        return this.mirrored;
    }

    @Override
    public boolean fitsGrid(int gridSize) {
        return this.pattern.fitsGrid(gridSize);
    }

    @NotNull
    @Override
    public Map<Integer, T> getGridLayout(int gridSize) {
        this.requireFitsGrid(gridSize);

        Map<Integer, T> layout = new LinkedHashMap<>();
        for (int row = 0; row < this.pattern.getHeight(); row++) {
            for (int column = 0; column < this.pattern.getWidth(); column++) {
                char symbol = this.pattern.getSymbol(column, row);
                if (symbol != CraftingPattern.EMPTY_SYMBOL) {
                    layout.put(CraftingGrid.indexOf(gridSize, column, row), this.itemAdapter.copy(this.ingredients.get(symbol)));
                }
            }
        }

        return layout;
    }

    @NotNull
    @Override
    public Optional<RecipeMatch<T>> match(@NotNull CraftingGrid<T> grid) {
        Optional<GridArea> occupiedArea = grid.findOccupiedArea(this.itemAdapter);
        if (!occupiedArea.isPresent() || !this.hasPatternSize(occupiedArea.get())) {
            return Optional.empty();
        }

        Optional<RecipeMatch<T>> directMatch = this.matchAt(grid, occupiedArea.get(), false);
        if (directMatch.isPresent() || !this.mirrored) {
            return directMatch;
        }

        return this.matchAt(grid, occupiedArea.get(), true);
    }

    private boolean hasPatternSize(GridArea area) {
        return area.getWidth() == this.pattern.getWidth() && area.getHeight() == this.pattern.getHeight();
    }

    private Optional<RecipeMatch<T>> matchAt(CraftingGrid<T> grid, GridArea area, boolean mirror) {
        int[] consumption = new int[grid.getSlotCount()];

        for (int row = 0; row < this.pattern.getHeight(); row++) {
            for (int column = 0; column < this.pattern.getWidth(); column++) {
                int patternColumn = mirror ? this.pattern.getWidth() - 1 - column : column;
                char symbol = this.pattern.getSymbol(patternColumn, row);
                int slot = grid.indexOf(area.getColumn() + column, area.getRow() + row);

                if (!this.matchesSlot(symbol, grid.get(slot))) {
                    return Optional.empty();
                }
                if (symbol != CraftingPattern.EMPTY_SYMBOL) {
                    consumption[slot] = this.itemAdapter.getAmount(this.ingredients.get(symbol));
                }
            }
        }

        return Optional.of(RecipeMatch.of(this, grid, consumption, this.itemAdapter));
    }

    private boolean matchesSlot(char symbol, T actual) {
        if (symbol == CraftingPattern.EMPTY_SYMBOL) {
            return this.itemAdapter.isEmpty(actual);
        }

        return this.matchesIngredient(this.ingredients.get(symbol), actual);
    }
}
