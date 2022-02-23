package net.osnixer.craftinglib;

import lombok.Getter;
import net.osnixer.craftinglib.recipe.RecipeNMS;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class Crafting {

    @Getter private final ItemStack[]   itemArray;
    @Getter private final ItemStack     result;
    @Getter private boolean             custom;

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
}