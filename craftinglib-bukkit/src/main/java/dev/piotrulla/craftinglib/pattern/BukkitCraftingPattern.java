package dev.piotrulla.craftinglib.pattern;

import dev.piotrulla.craftinglib.BukkitCraftingRecipeIngredient;
import dev.piotrulla.craftinglib.CraftingRecipeIngredient;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BukkitCraftingPattern extends CraftingPattern<ItemStack> {

    private final Map<Character, ItemStack> ingredientMap;
    private final List<CraftingRecipeIngredient<ItemStack>> ingredients;

    public BukkitCraftingPattern(@NotNull List<String> rows, int width, int height,
                                 @NotNull Map<Character, ItemStack> ingredientMap) {
        super(rows, width, height);
        this.ingredientMap = new HashMap<>(ingredientMap);
        this.ingredients = buildIngredientsList();
    }

    @Override
    public List<CraftingRecipeIngredient<ItemStack>> getIngredients() {
        return new ArrayList<>(this.ingredients);
    }

    @Override
    public boolean matches(List<CraftingRecipeIngredient<ItemStack>> input) {
        if (input.size() != this.ingredients.size()) {
            return false;
        }

        for (int i = 0; i < this.ingredients.size(); i++) {
            CraftingRecipeIngredient<ItemStack> required = this.ingredients.get(i);
            CraftingRecipeIngredient<ItemStack> actual = input.get(i);

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

    private List<CraftingRecipeIngredient<ItemStack>> buildIngredientsList() {
        List<CraftingRecipeIngredient<ItemStack>> result = new ArrayList<>();

        for (String row : this.rows()) {
            for (char symbol : row.toCharArray()) {
                if (symbol == ' ') {
                    result.add(null);
                }
                else {
                    ItemStack item = ingredientMap.get(symbol);
                    result.add(item != null ? new BukkitCraftingRecipeIngredient(item) : null);
                }
            }
        }

        return result;
    }
}
