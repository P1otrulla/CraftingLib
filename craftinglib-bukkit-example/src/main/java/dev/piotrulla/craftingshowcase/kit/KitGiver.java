package dev.piotrulla.craftingshowcase.kit;

import dev.piotrulla.craftingshowcase.showcase.RecipeShowcase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Hands out showcase kits; items that do not fit are dropped at the player's feet.
 */
public final class KitGiver {

    private static final String KIT_SEPARATOR = ", ";

    public void give(@NotNull Player player, @NotNull List<RecipeShowcase> showcases) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(showcases, "showcases");

        showcases.forEach(showcase -> this.addItems(player, showcase.getKit()));
        player.sendMessage(Component.text("Received kit: " + showcases.stream()
                .map(RecipeShowcase::getName)
                .collect(Collectors.joining(KIT_SEPARATOR)), NamedTextColor.GREEN));
    }

    private void addItems(Player player, List<ItemStack> items) {
        player.getInventory().addItem(items.toArray(new ItemStack[0])).values()
                .forEach(leftover -> player.getWorld().dropItemNaturally(player.getLocation(), leftover));
    }
}
