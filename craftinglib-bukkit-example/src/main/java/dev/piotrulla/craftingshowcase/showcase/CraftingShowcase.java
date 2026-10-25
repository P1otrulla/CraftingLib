package dev.piotrulla.craftingshowcase.showcase;

import dev.piotrulla.craftinglib.bukkit.BukkitCraftingLib;
import dev.piotrulla.craftinglib.bukkit.BukkitRecipes;
import dev.piotrulla.craftinglib.item.MatchMode;
import dev.piotrulla.craftinglib.recipe.CraftingRecipe;
import dev.piotrulla.craftinglib.recipe.book.RecipeBookCategory;
import dev.piotrulla.craftinglib.recipe.manager.RecipeManager;
import dev.piotrulla.craftingshowcase.item.ShowcaseItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Shaped and shapeless crafting: amounts per slot, mirroring, 2x2 recipes, match modes,
 * recipe book groups and replacing vanilla recipes.
 */
public final class CraftingShowcase extends AbstractRecipeShowcase {

    private static final String RUBY_TOOLS_GROUP = "ruby-tools";
    private static final int RUBIES_PER_SWORD_SLOT = 2;
    private static final int REDSTONE_PER_RUBY = 4;
    private static final int TORCHES = 8;
    private static final int BREADS = 2;

    private final ShowcaseItems items;

    public CraftingShowcase(@NotNull ShowcaseItems items) {
        super("crafting", "Crafting", "Amounts, mirroring, 2x2, match modes, vanilla replacing", Material.CRAFTING_TABLE);
        this.items = Objects.requireNonNull(items, "items");
    }

    @Override
    public void register(@NotNull BukkitCraftingLib craftingLib) {
        RecipeManager<CraftingRecipe<ItemStack>> manager = craftingLib.getRecipeManager();

        // 2 rubies in each R slot; PERSISTENT_DATA accepts renamed rubies too
        this.registerRecipe(manager, BukkitRecipes.shaped("ruby-sword")
                .withPattern("R", "R", "S")
                .withIngredient('R', this.items.ruby().asQuantity(RUBIES_PER_SWORD_SLOT))
                .withIngredient('S', Material.STICK)
                .withResult(this.items.rubySword())
                .withMatchMode(MatchMode.PERSISTENT_DATA)
                .withRecipeBookGroup(RUBY_TOOLS_GROUP)
                .withRecipeBookCategory(RecipeBookCategory.EQUIPMENT)
                .build());

        this.registerRecipe(manager, BukkitRecipes.shaped("ruby-pickaxe")
                .withPattern("RRR", " S ", " S ")
                .withIngredient('R', this.items.ruby())
                .withIngredient('S', Material.STICK)
                .withResult(this.items.rubyPickaxe())
                .withMatchMode(MatchMode.PERSISTENT_DATA)
                .withRecipeBookGroup(RUBY_TOOLS_GROUP)
                .withRecipeBookCategory(RecipeBookCategory.EQUIPMENT)
                .build());

        // mirroring is on by default: the axe can face either way
        this.registerRecipe(manager, BukkitRecipes.shaped("ruby-axe")
                .withPattern("RR", "RS", " S")
                .withIngredient('R', this.items.ruby())
                .withIngredient('S', Material.STICK)
                .withResult(this.items.rubyAxe())
                .withMatchMode(MatchMode.PERSISTENT_DATA)
                .withRecipeBookGroup(RUBY_TOOLS_GROUP)
                .withRecipeBookCategory(RecipeBookCategory.EQUIPMENT)
                .build());

        // fits 2x2: craftable in the player inventory, 4 redstone in one slot
        this.registerRecipe(manager, BukkitRecipes.shapeless("ruby")
                .withIngredient(Material.REDSTONE, REDSTONE_PER_RUBY)
                .withIngredient(Material.DIAMOND)
                .withResult(this.items.ruby())
                .withRecipeBookCategory(RecipeBookCategory.MISC)
                .build());

        // replaces the vanilla torch recipe (4 torches) everywhere
        this.registerRecipe(manager, BukkitRecipes.shaped("better-torches")
                .withPattern("C", "S")
                .withIngredient('C', Material.COAL)
                .withIngredient('S', Material.STICK)
                .withResult(Material.TORCH, TORCHES)
                .replaceVanillaRecipes(true)
                .build());

        this.registerRecipe(manager, BukkitRecipes.shapeless("quick-bread")
                .withIngredient(Material.WHEAT)
                .withIngredient(Material.WHEAT)
                .withResult(Material.BREAD, BREADS)
                .withMatchMode(MatchMode.TYPE)
                .build());
    }

    @NotNull
    @Override
    public List<ItemStack> getKit() {
        return List.of(
                this.items.ruby().asQuantity(KitAmounts.LARGE),
                new ItemStack(Material.STICK, KitAmounts.MEDIUM),
                new ItemStack(Material.REDSTONE, KitAmounts.LARGE),
                new ItemStack(Material.DIAMOND, KitAmounts.SMALL),
                new ItemStack(Material.COAL, KitAmounts.MEDIUM),
                new ItemStack(Material.WHEAT, KitAmounts.MEDIUM),
                new ItemStack(Material.CRAFTING_TABLE)
        );
    }
}
