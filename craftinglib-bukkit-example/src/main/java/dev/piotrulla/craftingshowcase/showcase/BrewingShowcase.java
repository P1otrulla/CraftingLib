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
 * Brewing stand recipe (Paper {@code PotionMix}).
 */
public final class BrewingShowcase extends AbstractRecipeShowcase {

    private final ShowcaseItems items;

    public BrewingShowcase(@NotNull ShowcaseItems items) {
        super("brewing", "Brewing", "Custom potions from custom ingredients (Paper)", Material.BREWING_STAND);
        this.items = Objects.requireNonNull(items, "items");
    }

    @Override
    public void register(@NotNull BukkitCraftingLib craftingLib) {
        this.registerRecipe(craftingLib.getBrewingManager(), BukkitRecipes.brewing("ruby-elixir")
                .withInput(this.items.awkwardPotion())
                .withIngredient(this.items.ruby())
                .withResult(this.items.rubyPotion())
                .build());
    }

    @NotNull
    @Override
    public List<ItemStack> getKit() {
        return List.of(
                this.items.awkwardPotion(),
                this.items.awkwardPotion(),
                this.items.awkwardPotion(),
                this.items.ruby().asQuantity(KitAmounts.PAIR),
                new ItemStack(Material.BLAZE_POWDER, KitAmounts.PAIR),
                new ItemStack(Material.BREWING_STAND)
        );
    }
}
