package dev.piotrulla.craftinglib.item;

/**
 * How strictly a grid item is compared with a recipe ingredient.
 * Amounts are always checked separately (grid amount must be at least the ingredient amount).
 */
public enum MatchMode {

    /**
     * Type and all item data (meta, NBT, durability) must match.
     */
    EXACT,

    /**
     * Only the item type must match.
     */
    TYPE,

    /**
     * Type and custom plugin data must match, everything else (name, lore, damage, enchantments) is ignored.
     * On Bukkit this is the {@code PersistentDataContainer} (1.14+). Use it for custom items tagged by plugins.
     */
    PERSISTENT_DATA
}
