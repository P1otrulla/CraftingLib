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
 * Smithing table recipe with a template (1.20+).
 */
public final class SmithingShowcase extends AbstractRecipeShowcase {

    private final ShowcaseItems items;

    public SmithingShowcase(@NotNull ShowcaseItems items) {
        super("smithing", "Smithing", "Upgrade gear with a template and a custom item", Material.SMITHING_TABLE);
        this.items = Objects.requireNonNull(items, "items");
    }

    @Override
    public void register(@NotNull BukkitCraftingLib craftingLib) {
        this.registerRecipe(craftingLib.getSmithingManager(), BukkitRecipes.smithing("ruby-blade")
                .withTemplate(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
                .withBase(Material.DIAMOND_SWORD)
                .withAddition(this.items.ruby())
                .withResult(this.items.rubyBlade())
                .build());
    }

    @NotNull
    @Override
    public List<ItemStack> getKit() {
        return List.of(
                new ItemStack(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                new ItemStack(Material.DIAMOND_SWORD),
                this.items.ruby(),
                new ItemStack(Material.SMITHING_TABLE)
        );
    }
}
