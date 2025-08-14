package dev.piotrulla.craftinglib.pattern;

import dev.piotrulla.craftinglib.RecipeIngredient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class RecipePattern<T> {

    protected final List<String> rows;
    protected final int width;
    protected final int height;

    protected RecipePattern(List<String> rows, int width, int height) {
        this.rows = new ArrayList<>(rows);
        this.width = width;
        this.height = height;
    }

    public abstract List<RecipeIngredient<T>> getIngredients();

    public abstract boolean matches(List<RecipeIngredient<T>> input);

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
