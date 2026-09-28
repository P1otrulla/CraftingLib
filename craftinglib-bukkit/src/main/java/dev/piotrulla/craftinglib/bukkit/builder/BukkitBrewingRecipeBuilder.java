package dev.piotrulla.craftinglib.bukkit.builder;

import dev.piotrulla.craftinglib.bukkit.item.BukkitItemAdapter;
import dev.piotrulla.craftinglib.recipe.builder.AbstractBrewingRecipeBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * <pre>
 * BukkitRecipes.brewing("luck-potion")
 *     .withInput(awkwardPotion)
 *     .withIngredient(Material.GOLDEN_CARROT)
 *     .withResult(luckPotion)
 *     .build();
 * </pre>
 * Requires Paper (PotionMix API). With {@code MatchMode.EXACT} the input potion must match including its potion data.
 */
public final class BukkitBrewingRecipeBuilder
        extends AbstractBrewingRecipeBuilder<ItemStack, BukkitBrewingRecipeBuilder>
        implements BukkitResultSupport<BukkitBrewingRecipeBuilder> {

    public BukkitBrewingRecipeBuilder(@NotNull String key) {
        super(BukkitItemAdapter.INSTANCE, key);
    }

    @NotNull
    public BukkitBrewingRecipeBuilder withIngredient(@NotNull Material material) {
        Objects.requireNonNull(material, "material");
        return this.withIngredient(new ItemStack(material));
    }

    @NotNull
    @Override
    protected BukkitBrewingRecipeBuilder self() {
        return this;
    }
}