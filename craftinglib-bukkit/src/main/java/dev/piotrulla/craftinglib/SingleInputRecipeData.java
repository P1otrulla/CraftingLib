package dev.piotrulla.craftinglib;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Immutable record for single input recipe data
 */
public record SingleInputRecipeData(
        @NotNull ItemStack input,
        @Nullable String group
) {

    public SingleInputRecipeData {
        Objects.requireNonNull(input, "Input cannot be null");

        input = input.clone();

        if (input.getType() == Material.AIR) {
            throw new IllegalArgumentException("Input cannot be AIR");
        }
        if (input.getAmount() < 1) {
            throw new IllegalArgumentException("Input amount must be at least 1");
        }
    }

    /**
     * Creates single input data without group
     */
    public SingleInputRecipeData(@NotNull ItemStack input) {
        this(input, null);
    }

    /**
     * Returns defensive copy of input
     */
    @Override
    @NotNull
    public ItemStack input() {
        return this.input.clone();
    }
}
