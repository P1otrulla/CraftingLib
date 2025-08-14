package dev.piotrulla.craftinglib.builder;

import dev.piotrulla.craftinglib.Recipe;
import dev.piotrulla.craftinglib.exception.RecipeException;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class RecipeBuilder<T, R extends Recipe<T>> {

    protected final String name;
    protected final Map<Character, T> ingredientMap = new HashMap<>();
    protected List<String> pattern;
    protected T result;
    protected boolean exactMatch = true;
    protected boolean replaceVanilla = false;

    protected RecipeBuilder(@NotNull String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new RecipeException("Recipe name cannot be null or empty");
        }
        this.name = name.trim();
    }

    @NotNull
    public abstract RecipeBuilder<T, R> withIngredient(char symbol, @NotNull T ingredient);

    @NotNull
    public abstract RecipeBuilder<T, R> withResult(@NotNull T result);

    @NotNull
    public abstract R build();

    @NotNull
    public RecipeBuilder<T, R> requireExactMatch(boolean exactMatch) {
        this.exactMatch = exactMatch;
        return this;
    }

    @NotNull
    public RecipeBuilder<T, R> replaceVanillaRecipes(boolean replaceVanilla) {
        this.replaceVanilla = replaceVanilla;
        return this;
    }

    protected void validateBuildState() {
        if (this.pattern == null) {
            throw new RecipeException("Pattern must be set");
        }
        if (this.result == null) {
            throw new RecipeException("Result must be set");
        }

        Set<Character> usedSymbols = new HashSet<>();
        for (String row : this.pattern) {
            for (char c : row.toCharArray()) {
                if (c != ' ') {
                    usedSymbols.add(c);
                }
            }
        }

        for (char symbol : usedSymbols) {
            if (!this.ingredientMap.containsKey(symbol)) {
                throw new RecipeException("Pattern symbol '" + symbol + "' has no ingredient mapping");
            }
        }
    }

    protected String generateId() {
        return "craftinglib:" + this.name.toLowerCase()
                .replaceAll("[^a-z0-9_-]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");
    }

    protected void validatePatternRow(String row, String rowName, int expectedLength) {
        if (row == null || row.length() != expectedLength) {
            throw new RecipeException(rowName + " row must be exactly " + expectedLength + " characters");
        }
    }
}