package dev.piotrulla.craftinglib.pattern;

import dev.piotrulla.craftinglib.CraftingRecipeIngredient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class CraftingPattern<T> {

    protected final List<String> rows;
    protected final int width;
    protected final int height;

    protected CraftingPattern(List<String> rows, int width, int height) {
        this.rows = new ArrayList<>(rows);
        this.width = width;
        this.height = height;
    }

    public abstract List<CraftingRecipeIngredient<T>> getIngredients();

    public abstract boolean matches(List<CraftingRecipeIngredient<T>> input);

    public List<String> rows() {
        return Collections.unmodifiableList(this.rows);
    }

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }
}
