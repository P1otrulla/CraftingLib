package dev.piotrulla.craftinglib.tests;

import be.seeseemelk.mockbukkit.MockBukkit;
import dev.piotrulla.craftinglib.bukkit.item.BukkitItemAdapter;
import dev.piotrulla.craftinglib.item.MatchMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BukkitItemAdapterTest {

    private static final NamespacedKey ITEM_ID_KEY = new NamespacedKey("craftinglib", "item-id");

    private final BukkitItemAdapter itemAdapter = BukkitItemAdapter.INSTANCE;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void shouldTreatNullAirAndZeroAmountAsEmpty() {
        ItemStack zeroAmount = new ItemStack(Material.STICK);
        zeroAmount.setAmount(0);

        assertTrue(this.itemAdapter.isEmpty(null));
        assertTrue(this.itemAdapter.isEmpty(new ItemStack(Material.AIR)));
        assertTrue(this.itemAdapter.isEmpty(zeroAmount));
        assertFalse(this.itemAdapter.isEmpty(new ItemStack(Material.STICK)));
    }

    @Test
    void shouldCompareMetaOnlyInExactMode() {
        ItemStack plain = new ItemStack(Material.DIAMOND);
        ItemStack named = this.named(Material.DIAMOND, "Magic Diamond");

        assertFalse(this.itemAdapter.isSimilar(named, plain, MatchMode.EXACT));
        assertTrue(this.itemAdapter.isSimilar(named, named.clone(), MatchMode.EXACT));
        assertTrue(this.itemAdapter.isSimilar(named, plain, MatchMode.TYPE));
        assertFalse(this.itemAdapter.isSimilar(named, new ItemStack(Material.EMERALD), MatchMode.TYPE));
    }

    @Test
    void shouldIgnoreAmountWhenComparing() {
        assertTrue(this.itemAdapter.isSimilar(new ItemStack(Material.DIAMOND, 3), new ItemStack(Material.DIAMOND, 1), MatchMode.EXACT));
    }

    @Test
    void shouldComparePersistentDataIgnoringDisplayName() {
        ItemStack ruby = this.tagged(new ItemStack(Material.DIAMOND), "ruby");
        ItemStack renamedRuby = this.tagged(this.named(Material.DIAMOND, "Shiny Ruby"), "ruby");

        assertTrue(this.itemAdapter.isSimilar(ruby, renamedRuby, MatchMode.PERSISTENT_DATA));
        assertFalse(this.itemAdapter.isSimilar(ruby, new ItemStack(Material.DIAMOND), MatchMode.PERSISTENT_DATA));
        assertFalse(this.itemAdapter.isSimilar(ruby, this.tagged(new ItemStack(Material.DIAMOND), "sapphire"), MatchMode.PERSISTENT_DATA));
        assertFalse(this.itemAdapter.isSimilar(ruby, this.tagged(new ItemStack(Material.EMERALD), "ruby"), MatchMode.PERSISTENT_DATA));
    }

    private ItemStack tagged(ItemStack item, String itemId) {
        ItemStack tagged = item.clone();
        ItemMeta meta = tagged.getItemMeta();
        meta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, itemId);
        tagged.setItemMeta(meta);
        return tagged;
    }

    private ItemStack named(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
}
