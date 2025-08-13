package dev.piotrulla.craftinglib;

import dev.piotrulla.craftinglib.pattern.BukkitCraftingPattern;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BukkitCraftingRecipe implements CraftingRecipe<ItemStack> {

    public enum CraftingType {
        CRAFTING_TABLE_3X3,
        PLAYER_INVENTORY_2X2
    }

    private final String name;
    private final String id;
    private final BukkitCraftingPattern pattern;
    private final BukkitCraftingRecipeIngredient result;
    private final boolean exactMatch;
    private final boolean replaceVanilla;
    private final CraftingType craftingType;

    public BukkitCraftingRecipe(
            @NotNull String name, @NotNull String id,
            @NotNull BukkitCraftingPattern pattern,
            @NotNull BukkitCraftingRecipeIngredient result,
            boolean exactMatch, boolean replaceVanilla,
            @NotNull CraftingType craftingType
    ) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.pattern = Objects.requireNonNull(pattern, "Pattern cannot be null");
        this.result = Objects.requireNonNull(result, "Result cannot be null");
        this.exactMatch = exactMatch;
        this.replaceVanilla = replaceVanilla;
        this.craftingType = Objects.requireNonNull(craftingType, "Crafting type cannot be null");
    }

    @Override
    @NotNull
    public String name() {
        return this.name;
    }

    @Override
    @NotNull
    public String id() {
        return this.id;
    }

    @Override
    @NotNull
    public List<CraftingRecipeIngredient<ItemStack>> ingredients() {
        return this.pattern.getIngredients();
    }

    @Override
    @NotNull
    public CraftingRecipeIngredient<ItemStack> result() {
        return this.result;
    }

    @Override
    public boolean matches(@NotNull List<CraftingRecipeIngredient<ItemStack>> input) {
        return pattern.matches(input);
    }

    @Override
    public boolean requiresExactMatch() {
        return exactMatch;
    }

    /**
     * Whether this recipe should replace vanilla recipes with the same result.
     */
    public boolean shouldReplaceVanilla() {
        return replaceVanilla;
    }

    /**
     * Gets the crafting type (2x2 or 3x3).
     */
    @NotNull
    public CraftingType getCraftingType() {
        return this.craftingType;
    }

    /**
     * Gets the pattern for this recipe.
     */
    @NotNull
    public BukkitCraftingPattern getPattern() {
        return this.pattern;
    }

    /**
     * Checks if this recipe matches the given crafting matrix.
     */
    public boolean matchesCraftingMatrix(@NotNull ItemStack[] craftingMatrix) {
        List<CraftingRecipeIngredient<ItemStack>> matrixIngredients = convertToIngredients(craftingMatrix);

        return matches(matrixIngredients);
    }

    private List<CraftingRecipeIngredient<ItemStack>> convertToIngredients(ItemStack[] matrix) {
        return java.util.Arrays.stream(matrix)
                .map(item -> item != null ? new BukkitCraftingRecipeIngredient(item) : null)
                .filter(Objects::nonNull)
                .map(ingredient -> (CraftingRecipeIngredient<ItemStack>) ingredient)
                .collect(Collectors.toList());
    }
}