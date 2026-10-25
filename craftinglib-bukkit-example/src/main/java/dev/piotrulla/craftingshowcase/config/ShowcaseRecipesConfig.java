package dev.piotrulla.craftingshowcase.config;

import dev.piotrulla.craftinglib.bukkit.BukkitRecipes;
import dev.piotrulla.craftinglib.recipe.CraftingRecipe;
import dev.piotrulla.craftinglib.recipe.smelting.SmeltingRecipe;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * recipes.yml: recipes stored in a config with the CraftingLib okaeri serializers.
 */
public final class ShowcaseRecipesConfig extends OkaeriConfig {

    private static final float FLINT_EXPERIENCE = 0.1F;

    @Comment("Crafting recipes (shaped and shapeless). Edit and restart the server to see changes.")
    private List<CraftingRecipe<ItemStack>> crafting = new ArrayList<>(List.of(
            BukkitRecipes.shaped("config-saddle")
                    .withPattern("LLL", "L L")
                    .withIngredient('L', Material.LEATHER)
                    .withResult(Material.SADDLE)
                    .build()
    ));

    @Comment("Furnace, blast furnace, smoker and campfire recipes.")
    private List<SmeltingRecipe<ItemStack>> smelting = new ArrayList<>(List.of(
            BukkitRecipes.smelting("config-flint")
                    .withInput(Material.GRAVEL)
                    .withResult(Material.FLINT)
                    .withExperience(FLINT_EXPERIENCE)
                    .build()
    ));

    @NotNull
    public List<CraftingRecipe<ItemStack>> getCrafting() {
        return new ArrayList<>(this.crafting);
    }

    @NotNull
    public List<SmeltingRecipe<ItemStack>> getSmelting() {
        return new ArrayList<>(this.smelting);
    }
}
