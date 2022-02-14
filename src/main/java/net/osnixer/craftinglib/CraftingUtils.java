package net.osnixer.craftinglib;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import panda.std.Option;

import java.util.Iterator;

public final class CraftingUtils {

    public static void removeRecipe(ItemStack itemStack){
        final Iterator<Recipe> recipes = Bukkit.recipeIterator();

        while (recipes.hasNext()){
            final Recipe recipe = recipes.next();
            if (recipe != null && recipe.getResult().isSimilar(itemStack)){
                recipes.remove();
            }
        }
    }

    public static void removeItem(Inventory inventory, int slot, int amount) {
        if (inventory.getItem(slot) == null) {
            return;
        }
        final ItemStack item = inventory.getItem(slot).clone();

        if (item == null || item.getType().equals(Material.AIR)) {
            return;
        }
        if (item.getAmount() <= amount) {
            inventory.setItem(slot, new ItemStack(Material.AIR));
            return;
        }
        item.setAmount(item.getAmount() - amount);
        inventory.setItem(slot, item);
    }

    // Metoda zapożyczona z FunnyGuilds!
    public static ItemStack parseItem(String string) {
        String[] split = string.split(" ");
        String[] typeSplit = split[1].split(":");
        String subtype = typeSplit.length > 1 ? typeSplit[1] : "0";

        Option<Material> material = Option.of(Material.matchMaterial(typeSplit[0]));

        int stack;
        int data;

        try {
            stack = Integer.parseInt(split[0]);
            data = Integer.parseInt(subtype);
        }
        catch (NumberFormatException e) {
            stack = 1;
            data = 0;
        }

        if (!material.isPresent()){
            material = Option.of(Material.DIRT);
        }

        return new ItemStack(material.get(), stack, (short)data);
    }
}
