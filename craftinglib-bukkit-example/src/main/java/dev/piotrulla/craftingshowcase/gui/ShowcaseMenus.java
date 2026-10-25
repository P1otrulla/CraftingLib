package dev.piotrulla.craftingshowcase.gui;

import dev.piotrulla.craftinglib.recipe.CustomRecipe;
import dev.piotrulla.craftingshowcase.kit.KitGiver;
import dev.piotrulla.craftingshowcase.showcase.RecipeShowcase;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Navigation between menus. Opening and closing run on the next tick, which is safe from inside click events.
 */
public final class ShowcaseMenus {

    private final Plugin plugin;
    private final List<RecipeShowcase> showcases;
    private final KitGiver kitGiver;
    private final RecipeDescriber describer = new RecipeDescriber();

    public ShowcaseMenus(@NotNull Plugin plugin, @NotNull List<RecipeShowcase> showcases, @NotNull KitGiver kitGiver) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.showcases = List.copyOf(showcases);
        this.kitGiver = Objects.requireNonNull(kitGiver, "kitGiver");
    }

    public void openMain(@NotNull Player player) {
        this.openNextTick(player, new MainMenu(this, this.showcases));
    }

    public void openStation(@NotNull Player player, @NotNull RecipeShowcase showcase) {
        this.openNextTick(player, new StationMenu(this, showcase, this.describer));
    }

    public void openRecipe(@NotNull Player player, @NotNull RecipeShowcase showcase, @NotNull CustomRecipe<ItemStack> recipe) {
        this.openNextTick(player, new RecipePreviewMenu(this, showcase, recipe, this.describer));
    }

    public void giveKits(@NotNull Player player, @NotNull List<RecipeShowcase> selected) {
        this.kitGiver.give(player, selected);
    }

    public void close(@NotNull Player player) {
        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> player.closeInventory());
    }

    private void openNextTick(Player player, Menu menu) {
        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> menu.open(player));
    }
}
