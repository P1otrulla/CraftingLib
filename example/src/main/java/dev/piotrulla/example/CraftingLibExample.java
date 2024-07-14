package dev.piotrulla.example;

import dev.piotrulla.craftinglib.Crafting;
import dev.piotrulla.craftinglib.CraftingLib;
import dev.piotrulla.craftinglib.CraftingManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;

public class CraftingLibExample extends JavaPlugin {

    @Override
    public void onEnable() {
        CraftingLib craftingLib = new CraftingLib(this);

        CraftingManager craftingManager = craftingLib.getCraftingManager();

        String pickaxeName = "super-pickaxe";
        Crafting pickaxeCrafting = Crafting.builder()
                .withMaterial(1, Material.DIAMOND_BLOCK, 3)
                .withMaterial(2, Material.DIAMOND_BLOCK, 3)
                .withMaterial(3, Material.DIAMOND_BLOCK, 3)
                .withMaterial(5, Material.STICK)
                .withMaterial(8, Material.STICK)

                .withName(pickaxeName)

                .withResultItem(this.createPickaxe())
                .build();

        craftingManager.createCrafting(pickaxeName, pickaxeCrafting);

        String oldNotchAppleName = "old-notch-apple";
        Crafting oldNotchApple = Crafting.builder()
                .withMaterial(1, Material.GOLD_BLOCK)
                .withMaterial(2, Material.GOLD_BLOCK)
                .withMaterial(3, Material.GOLD_BLOCK)
                .withMaterial(4, Material.GOLD_BLOCK)
                .withMaterial(5, Material.APPLE)
                .withMaterial(6, Material.GOLD_BLOCK)
                .withMaterial(7, Material.GOLD_BLOCK)
                .withMaterial(8, Material.GOLD_BLOCK)
                .withMaterial(9, Material.GOLD_BLOCK)

                .withName(oldNotchAppleName)

                .withResultMaterial(Material.ENCHANTED_GOLDEN_APPLE)
                .build();

        craftingManager.createCrafting(oldNotchAppleName, oldNotchApple);
    }

    private ItemStack createPickaxe() {
        ItemStack pickaxe = new ItemStack(Material.DIAMOND_PICKAXE);

        ItemMeta itemMeta = pickaxe.getItemMeta();
        itemMeta.addEnchant(Enchantment.DIG_SPEED, 6, true);
        itemMeta.addEnchant(Enchantment.DURABILITY, 4, true);
        itemMeta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 4, true);

        itemMeta.setDisplayName(this.color("&6&lSuper Pickaxe"));
        itemMeta.setLore(Collections.singletonList(this.color("&cThis pickaxe is super fast!")));

        pickaxe.setItemMeta(itemMeta);

        return pickaxe;
    }

    private String color(String toColor) {
        return ChatColor.translateAlternateColorCodes('&', toColor);
    }
}
