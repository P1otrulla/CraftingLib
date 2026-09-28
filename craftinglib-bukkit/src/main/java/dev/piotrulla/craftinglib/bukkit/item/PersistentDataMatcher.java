package dev.piotrulla.craftinglib.bukkit.item;

import dev.piotrulla.craftinglib.bukkit.version.VersionDetector;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

/**
 * {@code MatchMode.PERSISTENT_DATA}: same type and equal {@link PersistentDataContainer}, other meta is ignored.
 * The container API is touched only after the capability check, so this class is safe to load on 1.8.
 */
final class PersistentDataMatcher {

    private static final String UNSUPPORTED = "PERSISTENT_DATA match mode requires Minecraft 1.14+";

    private final boolean supported = VersionDetector.isPersistentDataSupported();

    boolean matches(@NotNull ItemStack required, @NotNull ItemStack actual) {
        if (!this.supported) {
            throw new UnsupportedOperationException(UNSUPPORTED);
        }

        return required.getType() == actual.getType() && this.containerOf(required).equals(this.containerOf(actual));
    }

    private PersistentDataContainer containerOf(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            throw new IllegalArgumentException("Item " + item.getType() + " has no item meta");
        }

        return meta.getPersistentDataContainer();
    }
}
