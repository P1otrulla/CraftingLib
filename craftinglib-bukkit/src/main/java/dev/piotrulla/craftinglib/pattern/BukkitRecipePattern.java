package dev.piotrulla.craftinglib.pattern;

import dev.piotrulla.craftinglib.BukkitRecipeIngredient;
import dev.piotrulla.craftinglib.RecipeIngredient;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BukkitRecipePattern extends RecipePattern<ItemStack> {

    private final Map<Character, ItemStack> ingredientMap;
    private final List<RecipeIngredient<ItemStack>> ingredients;

    public BukkitRecipePattern(@NotNull List<String> rows, int width, int height,
                               @NotNull Map<Character, ItemStack> ingredientMap) {
        super(rows, width, height);
        this.ingredientMap = new HashMap<>(ingredientMap);
        this.ingredients = buildIngredientsList();
    }

    @Override
    public List<RecipeIngredient<ItemStack>> getIngredients() {
        return new ArrayList<>(this.ingredients);
    }

    @Override
    public boolean matches(List<RecipeIngredient<ItemStack>> input) {
        if (input.size() != this.ingredients.size()) {
            return false;
        }

        for (int i = 0; i < this.ingredients.size(); i++) {
            RecipeIngredient<ItemStack> required = this.ingredients.get(i);
            RecipeIngredient<ItemStack> actual = input.get(i);

            if (required == null && actual == null) {
                continue;
            }

            if (required == null || actual == null) {
                return false;
            }

            if (!required.matches(actual, true)) { // Pattern matching always exact
                return false;
            }
        }

        return true;
    }

    /**
     * Gets the ingredient map used by this pattern.
     */
    @NotNull
    public Map<Character, ItemStack> getIngredientMap() {
        return new HashMap<>(this.ingredientMap);
    }

    /**
     * Gets the ingredient at the specified position.
     */
    @NotNull
    public ItemStack getIngredientAt(int row, int col) {
        if (row < 0 || row >= this.height() || col < 0 || col >= this.width()) {
            throw new IndexOutOfBoundsException("Position (" + row + ", " + col + ") out of bounds");
        }

        String rowString = this.rows().get(row);
        char symbol = rowString.charAt(col);

        if (symbol == ' ') {
            return null;
        }

        ItemStack item = ingredientMap.get(symbol);
        return item != null ? item.clone() : null;
    }

    private List<RecipeIngredient<ItemStack>> buildIngredientsList() {
        List<RecipeIngredient<ItemStack>> result = new ArrayList<>();

        for (String row : this.rows()) {
            for (char symbol : row.toCharArray()) {
                if (symbol == ' ') {
                    result.add(null);
                }
                else {
                    ItemStack item = ingredientMap.get(symbol);
                    result.add(item != null ? new BukkitRecipeIngredient(item) : null);
                }
            }
        }

        return result;
    }
}
