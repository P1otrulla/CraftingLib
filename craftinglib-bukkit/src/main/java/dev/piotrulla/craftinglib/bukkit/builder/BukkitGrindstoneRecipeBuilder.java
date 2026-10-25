package dev.piotrulla.craftinglib.bukkit.builder;

import dev.piotrulla.craftinglib.bukkit.item.BukkitItemAdapter;
import dev.piotrulla.craftinglib.recipe.builder.AbstractGrindstoneRecipeBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
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
 * Grindstone slots only accept damageable items (tools, weapons, armor) and enchanted items or books,
 * so other inputs fail fast with {@link IllegalArgumentException}.
 */
public final class BukkitGrindstoneRecipeBuilder
        extends AbstractGrindstoneRecipeBuilder<ItemStack, BukkitGrindstoneRecipeBuilder>
        implements BukkitResultSupport<BukkitGrindstoneRecipeBuilder> {

    private static final int SINGLE_ITEM = 1;
    private static final int NOT_DAMAGEABLE = 0;
    private static final String INPUT = "Input";
    private static final String SECOND_INPUT = "Second input";

    public BukkitGrindstoneRecipeBuilder(@NotNull String key) {
        super(BukkitItemAdapter.INSTANCE, key);
    }

    /**
     * @throws IllegalArgumentException when the item cannot be placed in a grindstone
     */
    @NotNull
    @Override
    public BukkitGrindstoneRecipeBuilder withInput(@NotNull ItemStack input) {
        this.requireGrindable(input, INPUT);
        return super.withInput(input);
    }

    @NotNull
    public BukkitGrindstoneRecipeBuilder withInput(@NotNull Material material) {
        return this.withInput(material, SINGLE_ITEM);
    }

    @NotNull
    public BukkitGrindstoneRecipeBuilder withInput(@NotNull Material material, int amount) {
        return this.withInput(this.itemOf(material, amount));
    }

    /**
     * @throws IllegalArgumentException when the item cannot be placed in a grindstone
     */
    @NotNull
    @Override
    public BukkitGrindstoneRecipeBuilder withSecondInput(@NotNull ItemStack secondInput) {
        this.requireGrindable(secondInput, SECOND_INPUT);
        return super.withSecondInput(secondInput);
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

    /**
     * Empty items are left to the core builder, which rejects them with its own message.
     */
    private void requireGrindable(ItemStack item, String description) {
        if (this.itemAdapter.isEmpty(item)) {
            return;
        }

        if (!this.isGrindable(item)) {
            throw new IllegalArgumentException(description + " of grindstone recipe '" + this.key
                    + "' must be damageable (tool, weapon, armor) or enchanted, got: " + item.getType());
        }
        if (item.getAmount() > item.getMaxStackSize()) {
            throw new IllegalArgumentException(description + " of grindstone recipe '" + this.key
                    + "' cannot exceed the stack size of " + item.getType() + " (" + item.getMaxStackSize()
                    + "), got: " + item.getAmount());
        }
    }

    private boolean isGrindable(ItemStack item) {
        if (item.getType().getMaxDurability() > NOT_DAMAGEABLE || !item.getEnchantments().isEmpty()) {
            return true;
        }

        ItemMeta meta = item.getItemMeta();
        return meta instanceof EnchantmentStorageMeta && ((EnchantmentStorageMeta) meta).hasStoredEnchants();
    }

    private ItemStack itemOf(Material material, int amount) {
        Objects.requireNonNull(material, "material");
        return new ItemStack(material, amount);
    }
}
