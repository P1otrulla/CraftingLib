package dev.piotrulla.craftinglib.bukkit.builder;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * {@link Material} shortcuts for setting a recipe result, shared by all Bukkit builders.
 *
 * @param <B> concrete builder type
 */
public interface BukkitResultSupport<B> {

    @NotNull
    B withResult(@NotNull ItemStack result);

    @NotNull
    default B withResult(@NotNull Material material) {
        return this.withResult(material, 1);
    }

    @NotNull
    default B withResult(@NotNull Material material, int amount) {
        Objects.requireNonNull(material, "material");
        return this.withResult(new ItemStack(material, amount));
    }
}
