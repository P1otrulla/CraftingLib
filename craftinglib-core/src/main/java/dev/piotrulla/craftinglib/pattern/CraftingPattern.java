package dev.piotrulla.craftinglib.pattern;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Shape of a shaped recipe, e.g. {@code "DDD", " S ", " S "}.
 * A space means "this slot must be empty".
 * <p>
 * The pattern is normalized on creation: fully empty border rows and columns are removed,
 * so {@code "L ", "  "} becomes {@code "L"}. The recipe can be placed anywhere in the grid it fits in.
 */
public final class CraftingPattern {

    public static final char EMPTY_SYMBOL = ' ';
    public static final int MAX_SIZE = 3;

    private final List<String> rows;
    private final int width;
    private final int height;

    private CraftingPattern(List<String> rows) {
        this.rows = Collections.unmodifiableList(rows);
        this.width = rows.get(0).length();
        this.height = rows.size();
    }

    @NotNull
    public static CraftingPattern of(@NotNull String... rows) {
        validateRows(rows);
        return new CraftingPattern(normalize(rows));
    }

    private static void validateRows(String[] rows) {
        if (rows.length < 1 || rows.length > MAX_SIZE) {
            throw new IllegalArgumentException("Pattern must have 1-" + MAX_SIZE + " rows, got: " + rows.length);
        }

        for (String row : rows) {
            if (row == null) {
                throw new IllegalArgumentException("Pattern row cannot be null");
            }
            if (row.isEmpty() || row.length() > MAX_SIZE) {
                throw new IllegalArgumentException("Pattern row must have 1-" + MAX_SIZE + " characters, got: '" + row + "'");
            }
            if (row.length() != rows[0].length()) {
                throw new IllegalArgumentException("All pattern rows must have the same length, got: '"
                        + rows[0] + "' and '" + row + "'");
            }
        }
    }

    private static List<String> normalize(String[] rows) {
        int minColumn = Integer.MAX_VALUE;
        int maxColumn = -1;
        int minRow = Integer.MAX_VALUE;
        int maxRow = -1;

        for (int row = 0; row < rows.length; row++) {
            for (int column = 0; column < rows[row].length(); column++) {
                if (rows[row].charAt(column) == EMPTY_SYMBOL) {
                    continue;
                }

                minColumn = Math.min(minColumn, column);
                maxColumn = Math.max(maxColumn, column);
                minRow = Math.min(minRow, row);
                maxRow = Math.max(maxRow, row);
            }
        }

        if (maxRow < 0) {
            throw new IllegalArgumentException("Pattern must contain at least one ingredient symbol");
        }

        List<String> normalizedRows = new ArrayList<>();
        for (int row = minRow; row <= maxRow; row++) {
            normalizedRows.add(rows[row].substring(minColumn, maxColumn + 1));
        }

        return normalizedRows;
    }

    @NotNull
    public List<String> getRows() {
        return this.rows;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public char getSymbol(int column, int row) {
        return this.rows.get(row).charAt(column);
    }

    /**
     * @return all ingredient symbols (spaces excluded), in reading order
     */
    @NotNull
    public Set<Character> getSymbols() {
        Set<Character> symbols = new LinkedHashSet<>();
        for (String row : this.rows) {
            for (char symbol : row.toCharArray()) {
                if (symbol != EMPTY_SYMBOL) {
                    symbols.add(symbol);
                }
            }
        }

        return symbols;
    }

    public boolean fitsGrid(int gridSize) {
        return this.width <= gridSize && this.height <= gridSize;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CraftingPattern)) {
            return false;
        }

        return this.rows.equals(((CraftingPattern) other).rows);
    }

    @Override
    public int hashCode() {
        return this.rows.hashCode();
    }

    @Override
    public String toString() {
        return "CraftingPattern" + this.rows;
    }
}
