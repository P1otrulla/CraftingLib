package dev.piotrulla.craftingshowcase.showcase;

import dev.piotrulla.craftinglib.bukkit.BukkitCraftingLib;
import dev.piotrulla.craftinglib.bukkit.BukkitRecipes;
import dev.piotrulla.craftinglib.item.MatchMode;
import dev.piotrulla.craftinglib.recipe.GrindstoneRecipe;
import dev.piotrulla.craftinglib.recipe.manager.RecipeManager;
import dev.piotrulla.craftingshowcase.item.ShowcaseItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Grindstone recipes: one input only, and two inputs in either slot. Both give experience.
 * Grindstone slots only accept damageable or enchanted items, so both inputs are tools.
 */
public final class GrindstoneShowcase extends AbstractRecipeShowcase {

    private static final int SCRAPPED_RUBIES = 2;
    private static final int SCRAPPED_DIAMONDS = 2;
    private static final int RUBY_EXPERIENCE = 10;
    private static final int DIAMOND_EXPERIENCE = 5;

    private final ShowcaseItems items;

    public GrindstoneShowcase(@NotNull ShowcaseItems items) {
        super("grindstone", "Grindstone", "Scrap gear back into materials and experience", Material.GRINDSTONE);
        this.items = Objects.requireNonNull(items, "items");
    }

    @Override
    public void register(@NotNull BukkitCraftingLib craftingLib) {
        RecipeManager<GrindstoneRecipe<ItemStack>> manager = craftingLib.getGrindstoneManager();

        // PERSISTENT_DATA: works for a renamed or re-enchanted ruby sword too
        this.registerRecipe(manager, BukkitRecipes.grindstone("scrap-ruby-sword")
                .withInput(this.items.rubySword())
                .withResult(this.items.ruby().asQuantity(SCRAPPED_RUBIES))
                .withExperience(RUBY_EXPERIENCE)
                .withMatchMode(MatchMode.PERSISTENT_DATA)
                .build());

        this.registerRecipe(manager, BukkitRecipes.grindstone("scrap-diamond-sword")
                .withInput(Material.DIAMOND_SWORD)
                .withSecondInput(Material.IRON_SWORD)
                .withResult(Material.DIAMOND, SCRAPPED_DIAMONDS)
                .withExperience(DIAMOND_EXPERIENCE)
                .build());
    }

    @NotNull
    @Override
    public List<ItemStack> getKit() {
        return List.of(
                this.items.rubySword(),
                new ItemStack(Material.DIAMOND_SWORD),
                new ItemStack(Material.IRON_SWORD),
                new ItemStack(Material.GRINDSTONE)
        );
    }
}
