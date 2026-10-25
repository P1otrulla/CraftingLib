package dev.piotrulla.craftingshowcase.showcase;

import dev.piotrulla.craftinglib.bukkit.BukkitCraftingLib;
import dev.piotrulla.craftinglib.bukkit.BukkitRecipes;
import dev.piotrulla.craftinglib.recipe.book.RecipeBookCategory;
import dev.piotrulla.craftinglib.recipe.manager.RecipeManager;
import dev.piotrulla.craftinglib.recipe.smelting.CookingType;
import dev.piotrulla.craftinglib.recipe.smelting.SmeltingRecipe;
import dev.piotrulla.craftingshowcase.item.ShowcaseItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Furnace, blast furnace, smoker and campfire recipes.
 */
public final class SmeltingShowcase extends AbstractRecipeShowcase {

    private static final String RUBY_GROUP = "ruby";
    private static final float RUBY_EXPERIENCE = 1.0F;
    private static final float FOOD_EXPERIENCE = 0.35F;
    private static final int FAST_BLASTING_TICKS = 40;
    private static final int SLOW_CAMPFIRE_TICKS = 300;

    private final ShowcaseItems items;

    public SmeltingShowcase(@NotNull ShowcaseItems items) {
        super("smelting", "Smelting", "Furnace, blast furnace, smoker and campfire", Material.BLAST_FURNACE);
        this.items = Objects.requireNonNull(items, "items");
    }

    @Override
    public void register(@NotNull BukkitCraftingLib craftingLib) {
        RecipeManager<SmeltingRecipe<ItemStack>> manager = craftingLib.getSmeltingManager();

        this.registerRecipe(manager, BukkitRecipes.smelting("ruby-ore")
                .withInput(this.items.rubyOre())
                .withResult(this.items.ruby())
                .withExperience(RUBY_EXPERIENCE)
                .withRecipeBookGroup(RUBY_GROUP)
                .withRecipeBookCategory(RecipeBookCategory.BLOCKS)
                .build());

        this.registerRecipe(manager, BukkitRecipes.smelting("ruby-ore-blasting")
                .withCookingType(CookingType.BLASTING)
                .withInput(this.items.rubyOre())
                .withResult(this.items.ruby())
                .withExperience(RUBY_EXPERIENCE)
                .withCookingTime(FAST_BLASTING_TICKS)
                .withRecipeBookGroup(RUBY_GROUP)
                .withRecipeBookCategory(RecipeBookCategory.BLOCKS)
                .build());

        this.registerRecipe(manager, BukkitRecipes.smelting("jerky")
                .withCookingType(CookingType.SMOKING)
                .withInput(Material.ROTTEN_FLESH)
                .withResult(this.items.jerky())
                .withExperience(FOOD_EXPERIENCE)
                .withRecipeBookCategory(RecipeBookCategory.FOOD)
                .build());

        this.registerRecipe(manager, BukkitRecipes.smelting("campfire-leather")
                .withCookingType(CookingType.CAMPFIRE)
                .withInput(Material.ROTTEN_FLESH)
                .withResult(Material.LEATHER)
                .withCookingTime(SLOW_CAMPFIRE_TICKS)
                .build());
    }

    @NotNull
    @Override
    public List<ItemStack> getKit() {
        return List.of(
                this.items.rubyOre().asQuantity(KitAmounts.MEDIUM),
                new ItemStack(Material.ROTTEN_FLESH, KitAmounts.LARGE),
                new ItemStack(Material.COAL, KitAmounts.LARGE),
                new ItemStack(Material.FURNACE),
                new ItemStack(Material.BLAST_FURNACE),
                new ItemStack(Material.SMOKER),
                new ItemStack(Material.CAMPFIRE)
        );
    }
}
