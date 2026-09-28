package dev.piotrulla.craftinglib.bukkit.builder;

import dev.piotrulla.craftinglib.bukkit.item.BukkitItemAdapter;
import dev.piotrulla.craftinglib.recipe.builder.AbstractSmeltingRecipeBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * <pre>
 * BukkitRecipes.smelting("double-iron")
 *     .withCookingType(CookingType.BLASTING)
 *     .withInput(Material.RAW_IRON)
 *     .withResult(Material.IRON_INGOT, 2)
 *     .withExperience(0.7F)
 *     .build();
 * </pre>
 * On 1.8-1.13 only {@code FURNACE} works and the input is matched by material only.
 */
public final class BukkitSmeltingRecipeBuilder
        extends AbstractSmeltingRecipeBuilder<ItemStack, BukkitSmeltingRecipeBuilder>
        implements BukkitResultSupport<BukkitSmeltingRecipeBuilder> {

    public BukkitSmeltingRecipeBuilder(@NotNull String key) {
        super(BukkitItemAdapter.INSTANCE, key);
    }

    @NotNull
    public BukkitSmeltingRecipeBuilder withInput(@NotNull Material material) {
        Objects.requireNonNull(material, "material");
        return this.withInput(new ItemStack(material));
    }

    @NotNull
    @Override
    protected BukkitSmeltingRecipeBuilder self() {
        return this;
    }
}