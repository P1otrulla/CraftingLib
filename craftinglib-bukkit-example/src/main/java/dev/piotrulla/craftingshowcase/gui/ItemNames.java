package dev.piotrulla.craftingshowcase.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Readable item names for menu lore, e.g. {@code 2x Ruby} or {@code 1x Stone bricks}.
 */
final class ItemNames {

    private static final char ENUM_SEPARATOR = '_';
    private static final char WORD_SEPARATOR = ' ';

    private ItemNames() {
    }

    @NotNull
    static String withAmount(@NotNull ItemStack item) {
        return item.getAmount() + "x " + name(item);
    }

    @NotNull
    static String name(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            Component displayName = meta.displayName();
            return PlainTextComponentSerializer.plainText().serialize(displayName);
        }

        return readable(item.getType().name());
    }

    /**
     * @param constantName enum-style name, e.g. {@code STONE_BRICKS}
     * @return {@code Stone bricks}
     */
    @NotNull
    static String readable(@NotNull String constantName) {
        String words = constantName.toLowerCase(Locale.ROOT).replace(ENUM_SEPARATOR, WORD_SEPARATOR);
        return Character.toUpperCase(words.charAt(0)) + words.substring(1);
    }
}
