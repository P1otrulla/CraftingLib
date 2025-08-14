package dev.piotrulla.craftinglib;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Immutable record for smithing recipe data
 */
public record SmithingRecipeData(
        @NotNull ItemStack template,
        @NotNull ItemStack base,
        @NotNull ItemStack addition,
        boolean copyNbt
) {

    // Compact constructor with validation and defensive copying
    public SmithingRecipeData {
        Objects.requireNonNull(template, "Template cannot be null");
        Objects.requireNonNull(base, "Base cannot be null");
        Objects.requireNonNull(addition, "Addition cannot be null");

        // Defensive copying
        template = template.clone();
        base = base.clone();
        addition = addition.clone();

        // Validation
        if (template.getType() == Material.AIR) {
            throw new IllegalArgumentException("Template cannot be AIR");
        }
        if (base.getType() == Material.AIR) {
            throw new IllegalArgumentException("Base cannot be AIR");
        }
        if (addition.getType() == Material.AIR) {
            throw new IllegalArgumentException("Addition cannot be AIR");
        }
    }

    /**
     * Creates smithing data with NBT copying enabled by default
     */
    public SmithingRecipeData(@NotNull ItemStack template, @NotNull ItemStack base, @NotNull ItemStack addition) {
        this(template, base, addition, true);
    }

    /**
     * Returns defensive copy of template
     */
    @Override
    @NotNull
    public ItemStack template() {
        return this.template.clone();
    }

    /**
     * Returns defensive copy of base
     */
    @Override
    @NotNull
    public ItemStack base() {
        return this.base.clone();
    }

    /**
     * Returns defensive copy of addition
     */
    @Override
    @NotNull
    public ItemStack addition() {
        return this.addition.clone();
    }
}