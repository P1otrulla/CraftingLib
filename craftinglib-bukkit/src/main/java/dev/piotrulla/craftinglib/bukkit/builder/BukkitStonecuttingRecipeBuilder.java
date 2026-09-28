package dev.piotrulla.craftinglib.bukkit.builder;

import dev.piotrulla.craftinglib.bukkit.item.BukkitItemAdapter;
import dev.piotrulla.craftinglib.recipe.builder.AbstractStonecuttingRecipeBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * <pre>
 * BukkitRecipes.stonecutting("cheap-bricks")
 *     .withInput(Material.STONE)
 *     .withResult(Material.STONE_BRICKS, 2)
 *     .build();
 * </pre>
 * Requires Minecraft 1.14+.
 */
public final class BukkitStonecuttingRecipeBuilder
        extends AbstractStonecuttingRecipeBuilder<ItemStack, BukkitStonecuttingRecipeBuilder>
        implements BukkitResultSupport<BukkitStonecuttingRecipeBuilder> {

    public BukkitStonecuttingRecipeBuilder(@NotNull String key) {
        super(BukkitItemAdapter.INSTANCE, key);
    }

    @NotNull
    public BukkitStonecuttingRecipeBuilder withInput(@NotNull Material material) {
        Objects.requireNonNull(material, "material");
        return this.withInput(new ItemStack(material));
    }

    @NotNull
    @Override
    protected BukkitStonecuttingRecipeBuilder self() {
        return this;
    }
}