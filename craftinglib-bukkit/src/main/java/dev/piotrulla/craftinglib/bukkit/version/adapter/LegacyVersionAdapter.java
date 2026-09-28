package dev.piotrulla.craftinglib.bukkit.version.adapter;

import dev.piotrulla.craftinglib.CraftingException;
import dev.piotrulla.craftinglib.bukkit.version.VersionAdapter;
import dev.piotrulla.craftinglib.recipe.book.RecipeBookSettings;
import dev.piotrulla.craftinglib.recipe.smelting.CookingType;
import dev.piotrulla.craftinglib.recipe.smelting.SmeltingRecipe;
import org.bukkit.Server;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 1.8-1.11: no recipe keys, ingredients registered with wildcard data value, furnace only.
 * Must not reference any class added after 1.8.
 */
public class LegacyVersionAdapter implements VersionAdapter {

    /**
     * Bukkit maps -1 to the vanilla data wildcard for crafting ingredients.
     */
    private static final int ANY_DATA = -1;
    /**
     * Vanilla data wildcard, used directly for furnace inputs.
     */
    private static final int WILDCARD_DATA = Short.MAX_VALUE;
    private static final int SINGLE_ITEM = 1;

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public ShapedRecipe createShapedRecipe(@NotNull String key, @NotNull ItemStack result, @NotNull RecipeBookSettings recipeBook) {
        return new ShapedRecipe(result);
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public ShapelessRecipe createShapelessRecipe(@NotNull String key, @NotNull ItemStack result, @NotNull RecipeBookSettings recipeBook) {
        return new ShapelessRecipe(result);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void setIngredient(@NotNull ShapedRecipe recipe, char symbol, @NotNull ItemStack ingredient) {
        recipe.setIngredient(symbol, ingredient.getType(), ANY_DATA);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void addIngredient(@NotNull ShapelessRecipe recipe, @NotNull ItemStack ingredient) {
        recipe.addIngredient(SINGLE_ITEM, ingredient.getType(), ANY_DATA);
    }

    /**
     * Material level only: match mode, experience and cooking time are not supported before 1.13.
     */
    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public Recipe createCookingRecipe(@NotNull SmeltingRecipe<ItemStack> recipe) {
        this.requireFurnace(recipe);
        return new FurnaceRecipe(recipe.getResult(), recipe.getInput().getType(), WILDCARD_DATA);
    }

    @Override
    public boolean isCookingRecipe(@NotNull Recipe serverRecipe) {
        return serverRecipe instanceof FurnaceRecipe;
    }

    @Override
    public boolean isCookingRecipeFor(@NotNull Recipe serverRecipe, @NotNull SmeltingRecipe<ItemStack> recipe) {
        return recipe.getCookingType() == CookingType.FURNACE
                && serverRecipe instanceof FurnaceRecipe
                && ((FurnaceRecipe) serverRecipe).getInput().getType() == recipe.getInput().getType();
    }

    @Override
    public boolean addRecipe(@NotNull Server server, @NotNull Recipe serverRecipe) {
        return server.addRecipe(serverRecipe);
    }

    @Override
    public boolean removeRecipe(@NotNull Server server, @NotNull String key) {
        return false;
    }

    @Override
    public boolean isKeyed(@NotNull Recipe serverRecipe) {
        return false;
    }

    @Nullable
    @Override
    public String getOwnedKey(@NotNull Recipe serverRecipe) {
        return null;
    }

    @Override
    public boolean isOwnedNamespace(@NotNull String namespace) {
        return false;
    }

    @NotNull
    @Override
    public String getName() {
        return "legacy (1.8-1.11)";
    }

    protected void requireFurnace(@NotNull SmeltingRecipe<ItemStack> recipe) {
        if (recipe.getCookingType() != CookingType.FURNACE) {
            throw new CraftingException("Recipe '" + recipe.getKey() + "': " + recipe.getCookingType()
                    + " recipes require Minecraft 1.14+");
        }
    }
}