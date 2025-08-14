package dev.piotrulla.craftinglib.builder;

import dev.piotrulla.craftinglib.BukkitRecipe;
import dev.piotrulla.craftinglib.BukkitRecipeIngredient;
import dev.piotrulla.craftinglib.CookingRecipeData;
import dev.piotrulla.craftinglib.InventoryType;
import dev.piotrulla.craftinglib.RecipeType;
import dev.piotrulla.craftinglib.SingleInputRecipeData;
import dev.piotrulla.craftinglib.SmithingRecipeData;
import dev.piotrulla.craftinglib.exception.RecipeException;
import dev.piotrulla.craftinglib.pattern.BukkitRecipePattern;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public class BukkitRecipeBuilder extends RecipeBuilder<ItemStack, BukkitRecipe> {

    private RecipeType recipeType = RecipeType.SHAPED;
    private InventoryType inventoryType = InventoryType.CRAFTING_TABLE_3X3;

    private CookingRecipeData cookingData;
    private SmithingRecipeData smithingData;
    private SingleInputRecipeData singleInputData;

    private BukkitRecipeBuilder(@NotNull String name) {
        super(name);
    }

    @NotNull
    public static BukkitRecipeBuilder create(@NotNull String name) {
        return new BukkitRecipeBuilder(name);
    }

    @NotNull
    public BukkitRecipeBuilder withShapedPattern(@NotNull String row1, @NotNull String row2, @NotNull String row3) {
        this.validatePatternRow(row1, "First", 3);
        this.validatePatternRow(row2, "Second", 3);
        this.validatePatternRow(row3, "Third", 3);

        this.pattern = Arrays.asList(row1, row2, row3);
        this.recipeType = RecipeType.SHAPED;
        this.inventoryType = InventoryType.CRAFTING_TABLE_3X3;
        return this;
    }

    @NotNull
    public BukkitRecipeBuilder withShapedPattern(@NotNull String row1, @NotNull String row2) {
        this.validatePatternRow(row1, "First", 2);
        this.validatePatternRow(row2, "Second", 2);

        this.pattern = Arrays.asList(row1, row2);
        this.recipeType = RecipeType.SHAPED;
        this.inventoryType = InventoryType.PLAYER_INVENTORY_2X2;
        return this;
    }

    @NotNull
    public BukkitRecipeBuilder withShapelessIngredients(@NotNull ItemStack... ingredients) {
        Objects.requireNonNull(ingredients, "Ingredients cannot be null");

        if (ingredients.length == 0 || ingredients.length > 9) {
            throw new RecipeException("Shapeless recipes must have 1-9 ingredients");
        }

        StringBuilder patternBuilder = new StringBuilder();
        for (int i = 0; i < ingredients.length; i++) {
            char symbol = (char) ('A' + i);
            patternBuilder.append(symbol);
            this.ingredientMap.put(symbol, ingredients[i].clone());
        }

        if (ingredients.length <= 4) {
            this.pattern = Arrays.asList(
                    patternBuilder.substring(0, Math.min(2, ingredients.length)),
                    ingredients.length > 2 ? patternBuilder.substring(2, ingredients.length) : ""
            );
            this.inventoryType = InventoryType.SHAPELESS_2X2;
        }
        else {
            this.pattern = Arrays.asList(
                    patternBuilder.substring(0, 3),
                    patternBuilder.substring(3, Math.min(6, ingredients.length)),
                    ingredients.length > 6 ? patternBuilder.substring(6, ingredients.length) : ""
            );
            this.inventoryType = InventoryType.SHAPELESS_3X3;
        }

        this.recipeType = RecipeType.SHAPELESS;
        return this;
    }

    @NotNull
    public BukkitRecipeBuilder withFurnaceRecipe(@NotNull ItemStack input, int cookingTime, float experience) {
        this.singleInputData = new SingleInputRecipeData(input);
        this.cookingData = new CookingRecipeData(cookingTime, experience);
        this.recipeType = RecipeType.FURNACE;
        this.inventoryType = InventoryType.FURNACE_SMELTING;
        return this;
    }

    @NotNull
    public BukkitRecipeBuilder withFurnaceRecipe(@NotNull ItemStack input) {
        return this.withFurnaceRecipe(input, 200, 0.1f);
    }

    @NotNull
    public BukkitRecipeBuilder withBlastingRecipe(@NotNull ItemStack input, int cookingTime, float experience) {
        this.singleInputData = new SingleInputRecipeData(input);
        this.cookingData = new CookingRecipeData(cookingTime, experience);
        this.recipeType = RecipeType.BLASTING;
        this.inventoryType = InventoryType.BLAST_FURNACE;
        return this;
    }

    @NotNull
    public BukkitRecipeBuilder withBlastingRecipe(@NotNull ItemStack input) {
        return this.withBlastingRecipe(input, 100, 0.1f);
    }

    @NotNull
    public BukkitRecipeBuilder withSmokingRecipe(@NotNull ItemStack input, int cookingTime, float experience) {
        this.singleInputData = new SingleInputRecipeData(input);
        this.cookingData = new CookingRecipeData(cookingTime, experience);
        this.recipeType = RecipeType.SMOKING;
        this.inventoryType = InventoryType.SMOKER;
        return this;
    }

    @NotNull
    public BukkitRecipeBuilder withSmokingRecipe(@NotNull ItemStack input) {
        return this.withSmokingRecipe(input, 100, 0.1f);
    }

    @NotNull
    public BukkitRecipeBuilder withCampfireRecipe(@NotNull ItemStack input, int cookingTime, float experience) {
        this.singleInputData = new SingleInputRecipeData(input);
        this.cookingData = new CookingRecipeData(cookingTime, experience);
        this.recipeType = RecipeType.CAMPFIRE;
        this.inventoryType = InventoryType.CAMPFIRE_COOKING;
        return this;
    }

    @NotNull
    public BukkitRecipeBuilder withCampfireRecipe(@NotNull ItemStack input) {
        return this.withCampfireRecipe(input, 600, 0.35f);
    }

    @NotNull
    public BukkitRecipeBuilder withSmithingTransform(
            @NotNull ItemStack template,
            @NotNull ItemStack base,
            @NotNull ItemStack addition
    ) {
        this.smithingData = new SmithingRecipeData(template, base, addition);
        this.recipeType = RecipeType.SMITHING_TRANSFORM;
        this.inventoryType = InventoryType.SMITHING_TABLE_TRANSFORM;
        return this;
    }

    @NotNull
    public BukkitRecipeBuilder withSmithingTrim(
            @NotNull ItemStack template,
            @NotNull ItemStack base,
            @NotNull ItemStack addition
    ) {
        this.smithingData = new SmithingRecipeData(template, base, addition);
        this.recipeType = RecipeType.SMITHING_TRIM;
        this.inventoryType = InventoryType.SMITHING_TABLE_TRIM;
        return this;
    }

    @NotNull
    public BukkitRecipeBuilder withStonecuttingRecipe(@NotNull ItemStack input) {
        this.singleInputData = new SingleInputRecipeData(input);
        this.recipeType = RecipeType.STONECUTTING;
        this.inventoryType = InventoryType.STONECUTTER;
        return this;
    }

    @NotNull
    public BukkitRecipeBuilder withIngredient(char symbol, @NotNull Material material) {
        return this.withIngredient(symbol, new ItemStack(material));
    }

    @NotNull
    public BukkitRecipeBuilder withIngredient(char symbol, @NotNull Material material, int amount) {
        return this.withIngredient(symbol, new ItemStack(material, amount));
    }

    @Override
    @NotNull
    public BukkitRecipeBuilder withIngredient(char symbol, @NotNull ItemStack item) {
        if (symbol == ' ') {
            throw new RecipeException("Cannot use space ' ' as ingredient symbol");
        }

        Objects.requireNonNull(item, "Item cannot be null");
        if (item.getType() == Material.AIR) {
            throw new RecipeException("Item cannot be AIR");
        }

        this.ingredientMap.put(symbol, item.clone());
        return this;
    }

    @NotNull
    public BukkitRecipeBuilder withResult(@NotNull Material material) {
        return this.withResult(new ItemStack(material));
    }

    @NotNull
    public BukkitRecipeBuilder withResult(@NotNull Material material, int amount) {
        return this.withResult(new ItemStack(material, amount));
    }

    @Override
    @NotNull
    public BukkitRecipeBuilder withResult(@NotNull ItemStack result) {
        Objects.requireNonNull(result, "Result cannot be null");
        if (result.getType() == Material.AIR) {
            throw new RecipeException("Result cannot be AIR");
        }

        this.result = result.clone();
        return this;
    }

    @Override
    @NotNull
    public BukkitRecipeBuilder requireExactMatch(boolean exactMatch) {
        this.exactMatch = exactMatch;
        return this;
    }

    @Override
    @NotNull
    public BukkitRecipeBuilder replaceVanillaRecipes(boolean replaceVanilla) {
        this.replaceVanilla = replaceVanilla;
        return this;
    }

    @Override
    @NotNull
    public BukkitRecipe build() {
        this.validateBuildState();

        String id = this.generateId();
        BukkitRecipeIngredient resultIngredient = new BukkitRecipeIngredient(this.result);

        switch (this.recipeType) {
            case SHAPED, SHAPELESS -> {
                return this.buildCraftingRecipe(id, resultIngredient);
            }
            case FURNACE, BLASTING, SMOKING, CAMPFIRE -> {
                return this.buildCookingRecipe(id, resultIngredient);
            }
            case SMITHING_TRANSFORM, SMITHING_TRIM -> {
                return this.buildSmithingRecipe(id, resultIngredient);
            }
            case STONECUTTING -> {
                return this.buildStonecuttingRecipe(id, resultIngredient);
            }
            default -> {
                throw new RecipeException("Unsupported recipe type: " + this.recipeType);
            }
        }
    }

    private BukkitRecipe buildCraftingRecipe(
            @NotNull String id,
            @NotNull BukkitRecipeIngredient result
    ) {
        int width = this.inventoryType == InventoryType.PLAYER_INVENTORY_2X2 ||
                this.inventoryType == InventoryType.SHAPELESS_2X2 ? 2 : 3;

        BukkitRecipePattern craftingPattern = new BukkitRecipePattern(
                this.pattern, width, width, this.ingredientMap
        );

        return new BukkitRecipe(
                this.name, id, craftingPattern, result,
                this.exactMatch, this.replaceVanilla,
                this.inventoryType, this.recipeType
        );
    }

    private BukkitRecipe buildCookingRecipe(
            @NotNull String id,
            @NotNull BukkitRecipeIngredient result
    ) {
        if (this.singleInputData == null || this.cookingData == null) {
            throw new RecipeException("Cooking recipe requires input and cooking data");
        }

        return new BukkitRecipe(
                this.name, id, this.singleInputData, result,
                this.inventoryType, this.recipeType, this.cookingData
        );
    }

    private BukkitRecipe buildSmithingRecipe(
            @NotNull String id,
            @NotNull BukkitRecipeIngredient result
    ) {
        if (this.smithingData == null) {
            throw new RecipeException("Smithing recipe requires smithing data");
        }

        return new BukkitRecipe(
                this.name, id, this.smithingData, result,
                this.inventoryType, this.recipeType
        );
    }

    private BukkitRecipe buildStonecuttingRecipe(
            @NotNull String id,
            @NotNull BukkitRecipeIngredient result
    ) {
        if (this.singleInputData == null) {
            throw new RecipeException("Stonecutting recipe requires input data");
        }

        return new BukkitRecipe(
                this.name, id, this.singleInputData, result,
                this.inventoryType, this.recipeType
        );
    }

    @Override
    protected void validateBuildState() {
        if (this.result == null) {
            throw new RecipeException("Result must be set");
        }

        switch (this.recipeType) {
            case SHAPED, SHAPELESS -> {
                super.validateBuildState();
            }
            case FURNACE, BLASTING, SMOKING, CAMPFIRE -> {
                if (this.singleInputData == null || this.cookingData == null) {
                    throw new RecipeException("Cooking recipes require input and cooking data");
                }
            }
            case SMITHING_TRANSFORM, SMITHING_TRIM -> {
                if (this.smithingData == null) {
                    throw new RecipeException("Smithing recipes require smithing data");
                }
            }
            case STONECUTTING -> {
                if (this.singleInputData == null) {
                    throw new RecipeException("Stonecutting recipes require input data");
                }
            }
        }
    }
}