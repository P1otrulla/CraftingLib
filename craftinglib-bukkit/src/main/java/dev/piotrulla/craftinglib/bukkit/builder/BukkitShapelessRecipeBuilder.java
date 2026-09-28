package dev.piotrulla.craftinglib.bukkit.builder;

import dev.piotrulla.craftinglib.bukkit.item.BukkitItemAdapter;
import dev.piotrulla.craftinglib.recipe.builder.AbstractShapelessRecipeBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * <pre>
 * BukkitRecipes.shapeless("dough")
 *     .withIngredient(Material.WHEAT)
 *     .withIngredient(Material.SUGAR)
 *     .withIngredient(Material.EGG)
 *     .withResult(dough)
 *     .build();
 * </pre>
 */
public final class BukkitShapelessRecipeBuilder
        extends AbstractShapelessRecipeBuilder<ItemStack, BukkitShapelessRecipeBuilder>
        implements BukkitResultSupport<BukkitShapelessRecipeBuilder> {

    public BukkitShapelessRecipeBuilder(@NotNull String key) {
        super(BukkitItemAdapter.INSTANCE, key);
    }

    @NotNull
    public BukkitShapelessRecipeBuilder withIngredient(@NotNull Material material) {
        return this.withIngredient(material, 1);
    }

    /**
     * Adds ONE slot that must hold at least {@code amount} items.
     * To require several slots, call this method several times.
     */
    @NotNull
    public BukkitShapelessRecipeBuilder withIngredient(@NotNull Material material, int amount) {
        return this.withIngredient(new ItemStack(material, amount));
    }

    @NotNull
    @Override
    protected BukkitShapelessRecipeBuilder self() {
        return this;
    }
}
