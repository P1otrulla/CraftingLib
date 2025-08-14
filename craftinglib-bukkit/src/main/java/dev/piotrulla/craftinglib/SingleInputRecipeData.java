package dev.piotrulla.craftinglib;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SingleInputRecipeData {

    private final ItemStack input;
    private final String group;

    public SingleInputRecipeData(@NotNull ItemStack input, @Nullable String group) {
        this.input = Objects.requireNonNull(input, "Input cannot be null").clone();
        this.group = group;
    }

    public SingleInputRecipeData(@NotNull ItemStack input) {
        this(input, null);
    }

    @NotNull
    public ItemStack input() {
        return this.input.clone();
    }

    @Nullable
    public String group() {
        return this.group;
    }
}
