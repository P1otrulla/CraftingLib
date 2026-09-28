package dev.piotrulla.craftinglib.bukkit.builder;

import dev.piotrulla.craftinglib.bukkit.item.BukkitItemAdapter;
import dev.piotrulla.craftinglib.recipe.builder.AbstractSmithingRecipeBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * <pre>
 * BukkitRecipes.smithing("mithril-sword")
 *     .withTemplate(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
 *     .withBase(Material.DIAMOND_SWORD)
 *     .withAddition(mithrilIngot)
 *     .withResult(mithrilSword)
 *     .build();
 * </pre>
 * Template is required on 1.20+ and must be skipped on 1.16-1.19.
 */
public final class BukkitSmithingRecipeBuilder
        extends AbstractSmithingRecipeBuilder<ItemStack, BukkitSmithingRecipeBuilder>
        implements BukkitResultSupport<BukkitSmithingRecipeBuilder> {

    public BukkitSmithingRecipeBuilder(@NotNull String key) {
        super(BukkitItemAdapter.INSTANCE, key);
    }

    @NotNull
    public BukkitSmithingRecipeBuilder withTemplate(@NotNull Material material) {
        return this.withTemplate(this.itemOf(material));
    }

    @NotNull
    public BukkitSmithingRecipeBuilder withBase(@NotNull Material material) {
        return this.withBase(this.itemOf(material));
    }

    @NotNull
    public BukkitSmithingRecipeBuilder withAddition(@NotNull Material material) {
        return this.withAddition(this.itemOf(material));
    }

    @NotNull
    @Override
    protected BukkitSmithingRecipeBuilder self() {
        return this;
    }

    private ItemStack itemOf(Material material) {
        Objects.requireNonNull(material, "material");
        return new ItemStack(material);
    }
}