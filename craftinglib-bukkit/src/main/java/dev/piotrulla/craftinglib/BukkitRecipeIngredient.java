package dev.piotrulla.craftinglib;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BukkitRecipeIngredient implements RecipeIngredient<ItemStack> {

    private final ItemStack item;

    public BukkitRecipeIngredient(@NotNull ItemStack item) {
        this.item = Objects.requireNonNull(item, "Item cannot be null").clone();
    }

    @Override
    @NotNull
    public ItemStack item() {
        return this.item.clone();
    }

    @Override
    public int amount() {
        return this.item.getAmount();
    }

    @Override
    @Nullable
    public Object metaData() {
        return this.item.getItemMeta();
    }

    @Override
    public boolean matches(@NotNull RecipeIngredient<ItemStack> other, boolean exactMatch) {
        if (!(other instanceof BukkitRecipeIngredient)) {
            return false;
        }

        BukkitRecipeIngredient bukkitIngredient = (BukkitRecipeIngredient) other;

        ItemStack otherItem = bukkitIngredient.item();

        if (this.item.getType() != otherItem.getType()) {
            return false;
        }

        if (otherItem.getAmount() < this.item.getAmount()) {
            return false;
        }

        if (exactMatch) {
            return matchesExactly(otherItem);
        }

        return true;
    }

    private boolean matchesExactly(ItemStack otherItem) {
        ItemMeta thisMeta = this.item.getItemMeta();
        ItemMeta otherMeta = otherItem.getItemMeta();

        if (thisMeta == null && otherMeta == null) {
            return true;
        }

        if (thisMeta == null || otherMeta == null) {
            return false;
        }

        return thisMeta.equals(otherMeta);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        BukkitRecipeIngredient that = (BukkitRecipeIngredient) obj;

        return this.item.equals(that.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.item);
    }

    @Override
    public String toString() {
        return "BukkitRecipeIngredient{" +
                "type=" + this.item.getType() +
                ", amount=" + this.item.getAmount() +
                ", hasMeta=" + this.item.hasItemMeta() +
                '}';
    }
}
