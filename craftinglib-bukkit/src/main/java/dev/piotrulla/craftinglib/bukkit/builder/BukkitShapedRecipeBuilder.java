package dev.piotrulla.craftinglib.bukkit.builder;

import dev.piotrulla.craftinglib.bukkit.item.BukkitItemAdapter;
import dev.piotrulla.craftinglib.recipe.builder.AbstractShapedRecipeBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * <pre>
 * BukkitRecipes.shaped("super-pickaxe")
 *     .withPattern("DDD", " S ", " S ")
 *     .withIngredient('D', Material.DIAMOND_BLOCK)
 *     .withIngredient('S', Material.STICK)
 *     .withResult(superPickaxe)
 *     .build();
 * </pre>
 */
public final class BukkitShapedRecipeBuilder
        extends AbstractShapedRecipeBuilder<ItemStack, BukkitShapedRecipeBuilder>
        implements BukkitResultSupport<BukkitShapedRecipeBuilder> {

    public BukkitShapedRecipeBuilder(@NotNull String key) {
        super(BukkitItemAdapter.INSTANCE, key);
    }

    @NotNull
    public BukkitShapedRecipeBuilder withIngredient(char symbol, @NotNull Material material) {
        return this.withIngredient(symbol, material, 1);
    }

    /**
     * @param amount how many items must lie in each slot using this symbol
     */
    @NotNull
    public BukkitShapedRecipeBuilder withIngredient(char symbol, @NotNull Material material, int amount) {
        return this.withIngredient(symbol, new ItemStack(material, amount));
    }

    @NotNull
    @Override
    protected BukkitShapedRecipeBuilder self() {
        return this;
    }
}
