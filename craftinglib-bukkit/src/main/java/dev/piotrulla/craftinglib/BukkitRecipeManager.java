package dev.piotrulla.craftinglib;

import dev.piotrulla.craftinglib.exception.RecipeException;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bukkit implementation of RecipeManager - manages recipes
 */
public class BukkitRecipeManager implements RecipeManager<ItemStack> {

    private static final String RECIPE_TYPE_MISMATCH = "Recipe must be a BukkitCraftingRecipe";

    private final Map<String, BukkitRecipe> recipesById;
    private final Map<String, Set<BukkitRecipe>> recipesByResult;
    private final BukkitRecipeRegistry registry;

    public BukkitRecipeManager(@NotNull BukkitRecipeRegistry registry) {
        this.registry = Objects.requireNonNull(registry, "Registry cannot be null");
        this.recipesById = new ConcurrentHashMap<>();
        this.recipesByResult = new ConcurrentHashMap<>();
    }

    @Override
    public void addRecipe(@NotNull Recipe<ItemStack> recipe) {
        if (!(recipe instanceof BukkitRecipe bukkitRecipe)) {
            throw new IllegalArgumentException(RECIPE_TYPE_MISMATCH);
        }

        BukkitRecipeValidator.validateRecipe(bukkitRecipe);

        String recipeId = bukkitRecipe.id();

        if (this.recipesById.containsKey(recipeId)) {
            this.removeRecipe(recipeId);
        }

        if (bukkitRecipe.shouldReplaceVanilla()) {
            this.registry.removeRecipesByResult(bukkitRecipe.result().item());
        }

        this.registerRecipe(bukkitRecipe);
    }

    @Override
    public boolean removeRecipe(@NotNull String recipeId) {
        BukkitRecipe recipeToRemove = this.recipesById.remove(recipeId);
        if (recipeToRemove == null) {
            return false;
        }

        try {
            this.registry.removeRecipe(recipeToRemove);
            this.removeFromResultIndex(recipeToRemove);
            return true;
        }
        catch (Exception exception) {
            // Rollback on failure
            this.recipesById.put(recipeId, recipeToRemove);
            throw new RecipeException("Failed to unregister recipe: " + recipeToRemove.name(), exception);
        }
    }

    @Override
    public boolean removeRecipe(@NotNull Recipe<ItemStack> recipe) {
        if (!(recipe instanceof BukkitRecipe)) {
            return false;
        }
        return this.removeRecipe(recipe.id());
    }

    @Override
    @Nullable
    public Recipe<ItemStack> findRecipe(@NotNull String recipeId) {
        return this.recipesById.get(recipeId);
    }

    @Override
    @Nullable
    public Recipe<ItemStack> findMatchingRecipe(@NotNull List<RecipeIngredient<ItemStack>> input) {
        return BukkitRecipeMatcher.findMatchingRecipe(this.recipesById.values(), input);
    }

    @Override
    @NotNull
    public Collection<Recipe<ItemStack>> getAllRecipes() {
        return Collections.unmodifiableCollection(new ArrayList<>(this.recipesById.values()));
    }

    @Override
    public int getRecipeCount() {
        return this.recipesById.size();
    }

    /**
     * Finds a recipe that matches the given crafting matrix and type.
     */
    @Nullable
    public BukkitRecipe findMatchingRecipe(@NotNull ItemStack[] craftingMatrix,
                                                   @NotNull InventoryType type) {
        return BukkitRecipeMatcher.findMatchingRecipe(this.recipesById.values(), craftingMatrix, type);
    }

    /**
     * Gets all recipes that produce the given result.
     */
    @NotNull
    public Set<BukkitRecipe> getRecipesByResult(@NotNull ItemStack result) {
        String resultKey = BukkitRecipeMatcher.createResultKey(result);
        Set<BukkitRecipe> recipes = this.recipesByResult.get(resultKey);
        return recipes != null ? Collections.unmodifiableSet(new HashSet<>(recipes)) : Collections.emptySet();
    }

    /**
     * Gets all custom Bukkit recipes.
     */
    @NotNull
    public Collection<BukkitRecipe> getAllBukkitRecipes() {
        return Collections.unmodifiableCollection(new ArrayList<>(this.recipesById.values()));
    }

    /**
     * Checks if a recipe with the given ID exists.
     */
    public boolean hasRecipe(@NotNull String recipeId) {
        return this.recipesById.containsKey(recipeId);
    }

    /**
     * Gets a Bukkit recipe by ID.
     */
    @Nullable
    public BukkitRecipe getBukkitRecipe(@NotNull String recipeId) {
        return this.recipesById.get(recipeId);
    }

    /**
     * Clears all registered recipes.
     */
    public void clearAllRecipes() {
        List<String> recipeIds = new ArrayList<>(this.recipesById.keySet());
        for (String recipeId : recipeIds) {
            this.removeRecipe(recipeId);
        }
    }

    private void registerRecipe(@NotNull BukkitRecipe recipe) {
        try {
            this.registry.addRecipe(recipe);
            this.recipesById.put(recipe.id(), recipe);

            String resultKey = BukkitRecipeMatcher.createResultKey(recipe.result().item());
            this.recipesByResult
                    .computeIfAbsent(resultKey, key -> ConcurrentHashMap.newKeySet())
                    .add(recipe);

        } catch (Exception exception) {
            throw new RecipeException("Failed to register recipe: " + recipe.name(), exception);
        }
    }

    private void removeFromResultIndex(@NotNull BukkitRecipe recipe) {
        String resultKey = BukkitRecipeMatcher.createResultKey(recipe.result().item());
        Set<BukkitRecipe> recipes = this.recipesByResult.get(resultKey);

        if (recipes == null) {
            return;
        }

        recipes.remove(recipe);

        if (recipes.isEmpty()) {
            this.recipesByResult.remove(resultKey);
        }
    }
}