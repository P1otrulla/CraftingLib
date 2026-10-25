package dev.piotrulla.craftingshowcase.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Builds showcase items: name, lore, enchantments, plugin data tag and type-specific meta.
 */
public final class ItemBuilder {

    private static final int SINGLE_ITEM = 1;
    private static final boolean IGNORE_LEVEL_RESTRICTION = true;

    private final Material material;
    private final List<Component> lore = new ArrayList<>();
    private final Map<Enchantment, Integer> enchantments = new LinkedHashMap<>();
    private final List<Consumer<ItemMeta>> metaCustomizers = new ArrayList<>();

    private int amount = SINGLE_ITEM;
    private Component name;
    private NamespacedKey idKey;
    private String id;

    private ItemBuilder(Material material) {
        this.material = material;
    }

    @NotNull
    public static ItemBuilder of(@NotNull Material material) {
        return new ItemBuilder(Objects.requireNonNull(material, "material"));
    }

    @NotNull
    public ItemBuilder amount(int amount) {
        if (amount < SINGLE_ITEM) {
            throw new IllegalArgumentException("Amount must be at least " + SINGLE_ITEM + ", got: " + amount);
        }

        this.amount = amount;
        return this;
    }

    @NotNull
    public ItemBuilder name(@NotNull String name, @NotNull TextColor color) {
        this.name = this.plain(name, color);
        return this;
    }

    @NotNull
    public ItemBuilder lore(@NotNull String line, @NotNull TextColor color) {
        this.lore.add(this.plain(line, color));
        return this;
    }

    @NotNull
    public ItemBuilder enchant(@NotNull Enchantment enchantment, int level) {
        this.enchantments.put(Objects.requireNonNull(enchantment, "enchantment"), level);
        return this;
    }

    /**
     * Tags the item in its {@code PersistentDataContainer}, which {@code MatchMode.PERSISTENT_DATA} compares.
     */
    @NotNull
    public ItemBuilder id(@NotNull NamespacedKey key, @NotNull String id) {
        this.idKey = Objects.requireNonNull(key, "key");
        this.id = Objects.requireNonNull(id, "id");
        return this;
    }

    /**
     * Type-specific meta, e.g. {@code PotionMeta}.
     */
    @NotNull
    public <M extends ItemMeta> ItemBuilder meta(@NotNull Class<M> metaType, @NotNull Consumer<M> customizer) {
        Objects.requireNonNull(metaType, "metaType");
        Objects.requireNonNull(customizer, "customizer");

        this.metaCustomizers.add(meta -> {
            if (!metaType.isInstance(meta)) {
                throw new IllegalStateException(this.material + " has no " + metaType.getSimpleName());
            }
            customizer.accept(metaType.cast(meta));
        });
        return this;
    }

    @NotNull
    public ItemStack build() {
        ItemStack item = new ItemStack(this.material, this.amount);
        ItemMeta meta = item.getItemMeta();

        if (this.name != null) {
            meta.displayName(this.name);
        }
        if (!this.lore.isEmpty()) {
            meta.lore(new ArrayList<>(this.lore));
        }
        this.enchantments.forEach((enchantment, level) -> meta.addEnchant(enchantment, level, IGNORE_LEVEL_RESTRICTION));
        if (this.idKey != null) {
            meta.getPersistentDataContainer().set(this.idKey, PersistentDataType.STRING, this.id);
        }
        this.metaCustomizers.forEach(customizer -> customizer.accept(meta));

        item.setItemMeta(meta);
        return item;
    }

    private Component plain(String text, TextColor color) {
        return Component.text(text, color).decoration(TextDecoration.ITALIC, false);
    }
}
