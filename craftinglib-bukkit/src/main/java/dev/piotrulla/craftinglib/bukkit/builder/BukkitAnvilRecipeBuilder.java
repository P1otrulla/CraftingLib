package dev.piotrulla.craftinglib.bukkit.builder;

import dev.piotrulla.craftinglib.bukkit.item.BukkitItemAdapter;
import dev.piotrulla.craftinglib.recipe.builder.AbstractAnvilRecipeBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * <pre>
 * BukkitRecipes.anvil("sharpen-sword")
 *     .withLeft(Material.IRON_SWORD)
 *     .withRight(Material.FLINT, 4)
 *     .withResult(sharpSword)
 *     .withLevelCost(5)
 *     .build();
 * </pre>
 * Requires Minecraft 1.11+.
 */
public final class BukkitAnvilRecipeBuilder
        extends AbstractAnvilRecipeBuilder<ItemStack, BukkitAnvilRecipeBuilder>
        implements BukkitResultSupport<BukkitAnvilRecipeBuilder> {

    private static final int SINGLE_ITEM = 1;

    public BukkitAnvilRecipeBuilder(@NotNull String key) {
        super(BukkitItemAdapter.INSTANCE, key);
    }

    @NotNull
    public BukkitAnvilRecipeBuilder withLeft(@NotNull Material material) {
        return this.withLeft(material, SINGLE_ITEM);
    }

    @NotNull
    public BukkitAnvilRecipeBuilder withLeft(@NotNull Material material, int amount) {
        return this.withLeft(this.itemOf(material, amount));
    }

    @NotNull
    public BukkitAnvilRecipeBuilder withRight(@NotNull Material material) {
        return this.withRight(material, SINGLE_ITEM);
    }

    @NotNull
    public BukkitAnvilRecipeBuilder withRight(@NotNull Material material, int amount) {
        return this.withRight(this.itemOf(material, amount));
    }

    @NotNull
    @Override
    protected BukkitAnvilRecipeBuilder self() {
        return this;
    }

    private ItemStack itemOf(Material material, int amount) {
        Objects.requireNonNull(material, "material");
        return new ItemStack(material, amount);
    }
}