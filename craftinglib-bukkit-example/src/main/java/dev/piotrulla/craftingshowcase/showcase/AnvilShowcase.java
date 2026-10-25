package dev.piotrulla.craftingshowcase.showcase;

import dev.piotrulla.craftinglib.bukkit.BukkitCraftingLib;
import dev.piotrulla.craftinglib.bukkit.BukkitRecipes;
import dev.piotrulla.craftingshowcase.item.ShowcaseItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Anvil recipe with an amount on the right slot and a level cost.
 */
public final class AnvilShowcase extends AbstractRecipeShowcase {

    private static final int RUBIES_PER_SHARPENING = 3;
    private static final int LEVEL_COST = 5;

    private final ShowcaseItems items;

    public AnvilShowcase(@NotNull ShowcaseItems items) {
        super("anvil", "Anvil", "Custom results with amounts and a level cost", Material.ANVIL);
        this.items = Objects.requireNonNull(items, "items");
    }

    @Override
    public void register(@NotNull BukkitCraftingLib craftingLib) {
        this.registerRecipe(craftingLib.getAnvilManager(), BukkitRecipes.anvil("sharpen-sword")
                .withLeft(Material.IRON_SWORD)
                .withRight(this.items.ruby().asQuantity(RUBIES_PER_SHARPENING))
                .withResult(this.items.sharpenedSword())
                .withLevelCost(LEVEL_COST)
                .build());
    }

    @NotNull
    @Override
    public List<ItemStack> getKit() {
        return List.of(
                new ItemStack(Material.IRON_SWORD),
                this.items.ruby().asQuantity(RUBIES_PER_SHARPENING),
                new ItemStack(Material.EXPERIENCE_BOTTLE, KitAmounts.HUGE),
                new ItemStack(Material.ANVIL)
        );
    }
}
