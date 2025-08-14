package dev.piotrulla.craftinglib.builder;

import dev.piotrulla.craftinglib.InventoryType;
import dev.piotrulla.craftinglib.pattern.BukkitCraftingPattern;
import dev.piotrulla.craftinglib.BukkitCraftingRecipe;
import dev.piotrulla.craftinglib.BukkitCraftingRecipeIngredient;
import dev.piotrulla.craftinglib.exception.CraftingException;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

/**
 * Bukkit implementation of CraftingRecipeBuilder.
 */
public class BukkitCraftingBuilder extends CraftingRecipeBuilder<ItemStack, BukkitCraftingRecipe> {

    private InventoryType inventoryType = InventoryType.CRAFTING_TABLE_3X3;

    private BukkitCraftingBuilder(@NotNull String name) {
        super(name);
    }

    @NotNull
    public static BukkitCraftingBuilder create(@NotNull String name) {
        return new BukkitCraftingBuilder(name);
    }

    /**
     * Sets a 3x3 crafting table pattern.
     */
    @NotNull
    public BukkitCraftingBuilder withPattern(@NotNull String row1, @NotNull String row2, @NotNull String row3) {
        this.validatePatternRow(row1, "First", 3);
        this.validatePatternRow(row2, "Second", 3);
        this.validatePatternRow(row3, "Third", 3);

        this.pattern = Arrays.asList(row1, row2, row3);
        this.inventoryType = InventoryType.CRAFTING_TABLE_3X3;
        return this;
    }

    /**
     * Sets a 2x2 player inventory pattern.
     */
    @NotNull
    public BukkitCraftingBuilder withPattern(@NotNull String row1, @NotNull String row2) {
        this.validatePatternRow(row1, "First", 2);
        this.validatePatternRow(row2, "Second", 2);

        this.pattern = Arrays.asList(row1, row2);
        this.inventoryType = InventoryType.PLAYER_INVENTORY_2X2;
        return this;
    }

    /**
     * Maps a pattern symbol to a material.
     */
    @NotNull
    public BukkitCraftingBuilder withIngredient(char symbol, @NotNull Material material) {
        return this.withIngredient(symbol, new ItemStack(material));
    }

    /**
     * Maps a pattern symbol to a material with amount.
     */
    @NotNull
    public BukkitCraftingBuilder withIngredient(char symbol, @NotNull Material material, int amount) {
        return this.withIngredient(symbol, new ItemStack(material, amount));
    }

    @Override
    @NotNull
    public BukkitCraftingBuilder withIngredient(char symbol, @NotNull ItemStack item) {
        if (symbol == ' ') {
            throw new CraftingException("Cannot use space ' ' as ingredient symbol");
        }

        Objects.requireNonNull(item, "Item cannot be null");
        if (item.getType() == Material.AIR) {
            throw new CraftingException("Item cannot be AIR");
        }

        this.ingredientMap.put(symbol, item.clone());
        return this;
    }

    /**
     * Sets the result material.
     */
    @NotNull
    public BukkitCraftingBuilder withResult(@NotNull Material material) {
        return this.withResult(new ItemStack(material));
    }

    /**
     * Sets the result material with amount.
     */
    @NotNull
    public BukkitCraftingBuilder withResult(@NotNull Material material, int amount) {
        return this.withResult(new ItemStack(material, amount));
    }

    @Override
    @NotNull
    public BukkitCraftingBuilder withResult(@NotNull ItemStack result) {
        Objects.requireNonNull(result, "Result cannot be null");
        if (result.getType() == Material.AIR) {
            throw new CraftingException("Result cannot be AIR");
        }

        this.result = result.clone();
        return this;
    }

    @Override
    @NotNull
    public BukkitCraftingBuilder requireExactMatch(boolean exactMatch) {
        this.exactMatch = exactMatch;
        return this;
    }

    @Override
    @NotNull
    public BukkitCraftingBuilder replaceVanillaRecipes(boolean replaceVanilla) {
        this.replaceVanilla = replaceVanilla;
        return this;
    }

    @Override
    @NotNull
    public BukkitCraftingRecipe build() {
        this.validateBuildState();

        String id = this.generateId();
        int width = this.inventoryType == InventoryType.PLAYER_INVENTORY_2X2 ? 2 : 3;
        int height = this.inventoryType == InventoryType.PLAYER_INVENTORY_2X2 ? 2 : 3;

        BukkitCraftingPattern craftingPattern = new BukkitCraftingPattern(this.pattern, width, height, this.ingredientMap);
        BukkitCraftingRecipeIngredient resultIngredient = new BukkitCraftingRecipeIngredient(this.result);

        return new BukkitCraftingRecipe(this.name, id, craftingPattern, resultIngredient, this.exactMatch, this.replaceVanilla, this.inventoryType);
    }

    @Deprecated
    @NotNull
    public BukkitCraftingBuilder withItem(int slot, @NotNull ItemStack item) {
        return this;
    }

    @Deprecated
    @NotNull
    public BukkitCraftingBuilder withMaterial(int slot, @NotNull Material material) {
        return this.withItem(slot, new ItemStack(material));
    }
}