package dev.piotrulla.craftinglib.builder;

import dev.piotrulla.craftinglib.exception.CraftingException;
import dev.piotrulla.craftinglib.CraftingRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class CraftingRecipeBuilder<T, R extends CraftingRecipe<T>> {

    protected final String name;
    protected final Map<Character, T> ingredientMap = new HashMap<>();
    protected List<String> pattern;
    protected T result;
    protected boolean exactMatch = true;
    protected boolean replaceVanilla = false;

    protected CraftingRecipeBuilder(@NotNull String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new CraftingException("Recipe name cannot be null or empty");
        }
        this.name = name.trim();
    }

    @NotNull
    public abstract CraftingRecipeBuilder<T, R> withIngredient(char symbol, @NotNull T ingredient);

    @NotNull
    public abstract CraftingRecipeBuilder<T, R> withResult(@NotNull T result);

    @NotNull
    public abstract R build();

    @NotNull
    public CraftingRecipeBuilder<T, R> requireExactMatch(boolean exactMatch) {
        this.exactMatch = exactMatch;
        return this;
    }

    @NotNull
    public CraftingRecipeBuilder<T, R> replaceVanillaRecipes(boolean replaceVanilla) {
        this.replaceVanilla = replaceVanilla;
        return this;
    }

    protected void validateBuildState() {
        if (this.pattern == null) {
            throw new CraftingException("Pattern must be set");
        }
        if (this.result == null) {
            throw new CraftingException("Result must be set");
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
                throw new CraftingException("Pattern symbol '" + symbol + "' has no ingredient mapping");
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
            throw new CraftingException(rowName + " row must be exactly " + expectedLength + " characters");
        }
    }
}