package dev.piotrulla.craftingshowcase.showcase;

import dev.piotrulla.craftinglib.bukkit.BukkitCraftingLib;
import dev.piotrulla.craftinglib.recipe.CustomRecipe;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * One station of the showcase: its recipes, how it is shown in the menu and the ingredients needed to demo it.
 */
public interface RecipeShowcase {

    /**
     * @return kit name used by {@code /showcase <name>}
     */
    @NotNull
    String getName();

    @NotNull
    String getTitle();

    @NotNull
    String getDescription();

    @NotNull
    Material getIcon();

    void register(@NotNull BukkitCraftingLib craftingLib);

    /**
     * @return recipes registered by {@link #register(BukkitCraftingLib)}, empty before it
     */
    @NotNull
    List<CustomRecipe<ItemStack>> getRecipes();

    /**
     * @return new stacks with everything needed to craft this showcase's recipes
     */
    @NotNull
    List<ItemStack> getKit();
}
