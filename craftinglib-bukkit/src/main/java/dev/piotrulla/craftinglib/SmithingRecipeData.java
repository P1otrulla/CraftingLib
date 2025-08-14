package dev.piotrulla.craftinglib;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SmithingRecipeData {
    
    private final ItemStack template;  
    private final ItemStack base;       
    private final ItemStack addition;  
    private final boolean copyNbt;      

    public SmithingRecipeData(
            @NotNull ItemStack template, 
            @NotNull ItemStack base,
            @NotNull ItemStack addition, 
            boolean copyNbt
    ) {
        this.template = Objects.requireNonNull(template, "Template cannot be null").clone();
        this.base = Objects.requireNonNull(base, "Base cannot be null").clone();
        this.addition = Objects.requireNonNull(addition, "Addition cannot be null").clone();
        this.copyNbt = copyNbt;
    }

    public SmithingRecipeData(@NotNull ItemStack template, @NotNull ItemStack base, @NotNull ItemStack addition) {
        this(template, base, addition, true);
    }

    @NotNull
    public ItemStack template() {
        return this.template;
    }

    @NotNull
    public ItemStack base() {
        return this.base;
    }

    @NotNull
    public ItemStack addition() {
        return this.addition;
    }

    public boolean copyNbt() {
        return this.copyNbt;
    }
}