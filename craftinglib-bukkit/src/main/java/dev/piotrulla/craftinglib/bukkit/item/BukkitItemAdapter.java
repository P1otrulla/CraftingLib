package dev.piotrulla.craftinglib.bukkit.item;

import dev.piotrulla.craftinglib.item.ItemAdapter;
import dev.piotrulla.craftinglib.item.MatchMode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * {@link MatchMode#EXACT} uses {@link ItemStack#isSimilar(ItemStack)} (type, durability, meta).
 * {@link MatchMode#TYPE} compares {@link Material} only, so on 1.8-1.12 it also ignores data values (e.g. wool colour).
 * {@link MatchMode#PERSISTENT_DATA} compares {@link Material} and the {@code PersistentDataContainer} (1.14+).
 */
public final class BukkitItemAdapter implements ItemAdapter<ItemStack> {

    public static final BukkitItemAdapter INSTANCE = new BukkitItemAdapter();

    private final PersistentDataMatcher persistentDataMatcher = new PersistentDataMatcher();

    private BukkitItemAdapter() {
    }

    @Override
    public boolean isEmpty(@Nullable ItemStack item) {
        return item == null || item.getType() == Material.AIR || item.getAmount() < 1;
    }

    @Override
    public int getAmount(@NotNull ItemStack item) {
        return item.getAmount();
    }

    @Override
    public boolean isSimilar(@NotNull ItemStack required, @NotNull ItemStack actual, @NotNull MatchMode matchMode) {
        switch (matchMode) {
            case EXACT:
                return required.isSimilar(actual);
            case TYPE:
                return required.getType() == actual.getType();
            case PERSISTENT_DATA:
                return this.persistentDataMatcher.matches(required, actual);
            default:
                throw new IllegalArgumentException("Unsupported match mode: " + matchMode);
        }
    }

    @NotNull
    @Override
    public ItemStack copy(@NotNull ItemStack item) {
        return item.clone();
    }
}
