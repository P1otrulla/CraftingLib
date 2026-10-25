package dev.piotrulla.craftingshowcase.showcase;

import dev.piotrulla.craftinglib.bukkit.BukkitCraftingLib;
import dev.piotrulla.craftinglib.bukkit.BukkitRecipes;
import dev.piotrulla.craftinglib.recipe.StonecuttingRecipe;
import dev.piotrulla.craftinglib.recipe.manager.RecipeManager;
import dev.piotrulla.craftingshowcase.item.ShowcaseItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Stonecutter recipes, one of them taking a custom item.
 */
public final class StonecuttingShowcase extends AbstractRecipeShowcase {

    private static final int BRICKS = 2;
    private static final int REDSTONE_PER_RUBY = 9;

    private final ShowcaseItems items;

    public StonecuttingShowcase(@NotNull ShowcaseItems items) {
        super("stonecutting", "Stonecutter", "Cheaper cuts and custom items as input", Material.STONECUTTER);
        this.items = Objects.requireNonNull(items, "items");
    }

    @Override
    public void register(@NotNull BukkitCraftingLib craftingLib) {
        RecipeManager<StonecuttingRecipe<ItemStack>> manager = craftingLib.getStonecuttingManager();

        this.registerRecipe(manager, BukkitRecipes.stonecutting("cheap-bricks")
                .withInput(Material.STONE)
                .withResult(Material.STONE_BRICKS, BRICKS)
                .build());

        this.registerRecipe(manager, BukkitRecipes.stonecutting("crushed-ruby")
                .withInput(this.items.ruby())
                .withResult(Material.REDSTONE, REDSTONE_PER_RUBY)
                .build());
    }

    @NotNull
    @Override
    public List<ItemStack> getKit() {
        return List.of(
                new ItemStack(Material.STONE, KitAmounts.LARGE),
                this.items.ruby().asQuantity(KitAmounts.SMALL),
                new ItemStack(Material.STONECUTTER)
        );
    }
}
