package dev.piotrulla.craftinglib.bukkit.builder;

import dev.piotrulla.craftinglib.bukkit.item.BukkitItemAdapter;
import dev.piotrulla.craftinglib.recipe.builder.AbstractGrindstoneRecipeBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * <pre>
 * BukkitRecipes.grindstone("scrap-sword")
 *     .withInput(Material.IRON_SWORD)
 *     .withResult(Material.IRON_NUGGET, 6)
 *     .withExperience(3)
 *     .build();
 * </pre>
 * Requires a server with {@code PrepareGrindstoneEvent} (Spigot/Paper 1.19.3+).
 */
public final class BukkitGrindstoneRecipeBuilder
        extends AbstractGrindstoneRecipeBuilder<ItemStack, BukkitGrindstoneRecipeBuilder>
        implements BukkitResultSupport<BukkitGrindstoneRecipeBuilder> {

    private static final int SINGLE_ITEM = 1;

    public BukkitGrindstoneRecipeBuilder(@NotNull String key) {
        super(BukkitItemAdapter.INSTANCE, key);
    }

    @NotNull
    public BukkitGrindstoneRecipeBuilder withInput(@NotNull Material material) {
        return this.withInput(material, SINGLE_ITEM);
    }

    @NotNull
    public BukkitGrindstoneRecipeBuilder withInput(@NotNull Material material, int amount) {
        return this.withInput(this.itemOf(material, amount));
    }

    @NotNull
    public BukkitGrindstoneRecipeBuilder withSecondInput(@NotNull Material material) {
        return this.withSecondInput(material, SINGLE_ITEM);
    }

    @NotNull
    public BukkitGrindstoneRecipeBuilder withSecondInput(@NotNull Material material, int amount) {
        return this.withSecondInput(this.itemOf(material, amount));
    }

    @NotNull
    @Override
    protected BukkitGrindstoneRecipeBuilder self() {
        return this;
    }

    private ItemStack itemOf(Material material, int amount) {
        Objects.requireNonNull(material, "material");
        return new ItemStack(material, amount);
    }
}
