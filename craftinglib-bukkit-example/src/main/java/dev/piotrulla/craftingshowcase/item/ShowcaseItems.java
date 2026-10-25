package dev.piotrulla.craftingshowcase.item;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Custom items used by the showcase recipes. Every call returns a new stack.
 */
public final class ShowcaseItems {

    private static final String ITEM_ID_KEY = "item-id";
    private static final int TICKS_PER_SECOND = 20;
    private static final int RUBY_POTION_SECONDS = 180;
    private static final int STRENGTH_II = 1;
    private static final int SHARPNESS_LEVEL = 5;
    private static final int RUBY_ENCHANT_LEVEL = 3;
    private static final int FIRE_ASPECT_LEVEL = 2;

    private final NamespacedKey itemIdKey;

    public ShowcaseItems(@NotNull Plugin plugin) {
        Objects.requireNonNull(plugin, "plugin");
        this.itemIdKey = new NamespacedKey(plugin, ITEM_ID_KEY);
    }

    /**
     * Tagged with plugin data: recipes in {@code PERSISTENT_DATA} mode accept it even after renaming.
     */
    @NotNull
    public ItemStack ruby() {
        return ItemBuilder.of(Material.RED_DYE)
                .name("Ruby", NamedTextColor.RED)
                .lore("A precious gem", NamedTextColor.GRAY)
                .id(this.itemIdKey, "ruby")
                .build();
    }

    @NotNull
    public ItemStack rubyOre() {
        return ItemBuilder.of(Material.REDSTONE_ORE)
                .name("Ruby Ore", NamedTextColor.RED)
                .lore("Smelt it to get a ruby", NamedTextColor.GRAY)
                .id(this.itemIdKey, "ruby-ore")
                .build();
    }

    @NotNull
    public ItemStack rubySword() {
        return this.rubyTool(Material.IRON_SWORD, "Ruby Sword", "ruby-sword")
                .enchant(Enchantment.SHARPNESS, RUBY_ENCHANT_LEVEL)
                .build();
    }

    @NotNull
    public ItemStack rubyPickaxe() {
        return this.rubyTool(Material.IRON_PICKAXE, "Ruby Pickaxe", "ruby-pickaxe")
                .enchant(Enchantment.EFFICIENCY, RUBY_ENCHANT_LEVEL)
                .build();
    }

    @NotNull
    public ItemStack rubyAxe() {
        return this.rubyTool(Material.IRON_AXE, "Ruby Axe", "ruby-axe")
                .enchant(Enchantment.EFFICIENCY, RUBY_ENCHANT_LEVEL)
                .build();
    }

    @NotNull
    public ItemStack rubyBlade() {
        return this.rubyTool(Material.NETHERITE_SWORD, "Ruby Blade", "ruby-blade")
                .enchant(Enchantment.SHARPNESS, SHARPNESS_LEVEL)
                .enchant(Enchantment.FIRE_ASPECT, FIRE_ASPECT_LEVEL)
                .build();
    }

    @NotNull
    public ItemStack sharpenedSword() {
        return ItemBuilder.of(Material.IRON_SWORD)
                .name("Sharpened Sword", NamedTextColor.AQUA)
                .lore("Sharpened on an anvil with rubies", NamedTextColor.GRAY)
                .enchant(Enchantment.SHARPNESS, SHARPNESS_LEVEL)
                .build();
    }

    @NotNull
    public ItemStack jerky() {
        return ItemBuilder.of(Material.COOKED_BEEF)
                .name("Jerky", NamedTextColor.GOLD)
                .lore("Smoked rotten flesh. Surprisingly tasty", NamedTextColor.GRAY)
                .build();
    }

    @NotNull
    public ItemStack awkwardPotion() {
        return ItemBuilder.of(Material.POTION)
                .meta(PotionMeta.class, meta -> meta.setBasePotionType(PotionType.AWKWARD))
                .build();
    }

    @NotNull
    public ItemStack rubyPotion() {
        return ItemBuilder.of(Material.POTION)
                .name("Ruby Elixir", NamedTextColor.RED)
                .lore("Brewed from an awkward potion and a ruby", NamedTextColor.GRAY)
                .meta(PotionMeta.class, meta -> meta.addCustomEffect(
                        new PotionEffect(PotionEffectType.STRENGTH, RUBY_POTION_SECONDS * TICKS_PER_SECOND, STRENGTH_II),
                        true))
                .build();
    }

    private ItemBuilder rubyTool(Material material, String name, String id) {
        return ItemBuilder.of(material)
                .name(name, NamedTextColor.RED)
                .lore("Made of rubies", NamedTextColor.GRAY)
                .id(this.itemIdKey, id);
    }
}
