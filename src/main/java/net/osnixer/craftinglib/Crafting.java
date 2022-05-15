package net.osnixer.craftinglib;

import net.osnixer.craftinglib.recipe.RecipeNMS;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class Crafting {

    private final ItemStack[]   itemArray;
    private final ItemStack     result;
    private boolean             custom;

    public Crafting(ItemStack[] itemArray, ItemStack result) {
        this.itemArray = itemArray;
        this.result = result;

        char[] chars = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I' };

        RecipeNMS recipeNMS = CraftingLib.getInstance().getRecipeNMS();
        ShapedRecipe recipe = recipeNMS.getRecipe(this.result);

        recipe.shape("ABC", "DEF", "GHI");

        for (int i = 0; i <= 8; i++){
            recipeNMS.setIngredient(recipe, chars[i], itemArray[i]);
        }

        for (ItemStack itemStack : itemArray){
            if (itemStack != null && itemStack.hasItemMeta()){
                this.custom = true;
                break;
            }
            if (itemStack.getAmount() > 1) {
                this.custom = true;
                break;
            }
        }

        Bukkit.addRecipe(recipe);
    }

    public ItemStack[] getItemArray() {
        return this.itemArray;
    }

    public ItemStack getResult() {
        return this.result;
    }

    public boolean isCustom() {
        return this.custom;
    }

}