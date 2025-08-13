package dev.piotrulla.craftinglib;

import dev.piotrulla.craftinglib.exception.CraftingException;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bukkit implementation of RecipeManager.
 */
public class BukkitCraftingRecipeManager implements CraftingRecipeManager<ItemStack> {

    private static final String RECIPE_TYPE_MISMATCH = "Recipe must be a BukkitCraftingRecipe";

    private final Map<String, BukkitCraftingRecipe> recipesById = new ConcurrentHashMap<>();
    private final Map<String, Set<BukkitCraftingRecipe>> recipesByResult = new ConcurrentHashMap<>();
    private final BukkitCraftingRecipeRegistry registry;

    public BukkitCraftingRecipeManager(@NotNull BukkitCraftingRecipeRegistry registry) {
        this.registry = Objects.requireNonNull(registry, "Registry cannot be null");
    }

    @Override
    public void addRecipe(@NotNull CraftingRecipe<ItemStack> recipe) {
        if (!(recipe instanceof BukkitCraftingRecipe)) {
            throw new IllegalArgumentException(RECIPE_TYPE_MISMATCH);
        }

        BukkitCraftingRecipe bukkitRecipe = (BukkitCraftingRecipe) recipe;
        String recipeId = bukkitRecipe.id();

        if (this.recipesById.containsKey(recipeId)) {
            this.removeRecipe(recipeId);
        }

        if (bukkitRecipe.shouldReplaceVanilla()) {
            this.registry.removeRecipesByResult(bukkitRecipe.result().item());
        }

        try {
            this.registry.addRecipe(bukkitRecipe);
            this.recipesById.put(recipeId, bukkitRecipe);

            String resultKey = this.createResultKey(bukkitRecipe.result().item());
            this.recipesByResult
                    .computeIfAbsent(resultKey, key -> ConcurrentHashMap.newKeySet())
                    .add(bukkitRecipe);

        } catch (Exception exception) {
            throw new CraftingException("Failed to register recipe: " + bukkitRecipe.name(), exception);
        }
    }

    @Override
    public boolean removeRecipe(@NotNull String recipeId) {
        BukkitCraftingRecipe recipeToRemove = this.recipesById.remove(recipeId);
        if (recipeToRemove == null) {
            return false;
        }

        try {
            this.registry.removeRecipe(recipeToRemove);
            this.removeFromResultIndex(recipeToRemove);
            return true;
        } catch (Exception exception) {
            // Rollback
            this.recipesById.put(recipeId, recipeToRemove);
            throw new CraftingException("Failed to unregister recipe: " + recipeToRemove.name(), exception);
        }
    }

    @Override
    public boolean removeRecipe(@NotNull CraftingRecipe<ItemStack> recipe) {
        if (!(recipe instanceof BukkitCraftingRecipe)) {
            return false;
        }

        BukkitCraftingRecipe bukkitRecipe = (BukkitCraftingRecipe) recipe;
        return this.removeRecipe(bukkitRecipe.id());
    }

    @Override
    @Nullable
    public CraftingRecipe<ItemStack> findRecipe(@NotNull String recipeId) {
        return this.recipesById.get(recipeId);
    }

    @Override
    @Nullable
    public CraftingRecipe<ItemStack> findMatchingRecipe(@NotNull List<CraftingRecipeIngredient<ItemStack>> input) {
        for (BukkitCraftingRecipe recipe : this.recipesById.values()) {
            if (recipe.matches(input)) {
                return recipe;
            }
        }
        return null;
    }

    @Override
    @NotNull
    public Collection<CraftingRecipe<ItemStack>> getAllRecipes() {
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
    public BukkitCraftingRecipe findMatchingRecipe(@NotNull ItemStack[] craftingMatrix,
                                                   @NotNull BukkitCraftingRecipe.CraftingType type) {
        for (BukkitCraftingRecipe recipe : this.recipesById.values()) {
            if (recipe.getCraftingType() == type && recipe.matchesCraftingMatrix(craftingMatrix)) {
                return recipe;
            }
        }
        return null;
    }

    /**
     * Gets all recipes that produce the given result.
     */
    @NotNull
    public Set<BukkitCraftingRecipe> getRecipesByResult(@NotNull ItemStack result) {
        String resultKey = this.createResultKey(result);
        Set<BukkitCraftingRecipe> recipes = this.recipesByResult.get(resultKey);
        return recipes != null ? new HashSet<>(recipes) : Collections.emptySet();
    }

    /**
     * Gets all Bukkit recipes.
     */
    @NotNull
    public Collection<BukkitCraftingRecipe> getAllBukkitRecipes() {
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
    public BukkitCraftingRecipe getBukkitRecipe(@NotNull String recipeId) {
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

    private String createResultKey(@NotNull ItemStack result) {
        return result.getType().name();
    }

    private void removeFromResultIndex(@NotNull BukkitCraftingRecipe recipe) {
        String resultKey = this.createResultKey(recipe.result().item());
        Set<BukkitCraftingRecipe> recipes = this.recipesByResult.get(resultKey);

        if (recipes == null) {
            return;
        }

        recipes.remove(recipe);

        if (!recipes.isEmpty()) {
            return;
        }

        this.recipesById.remove(resultKey);
    }
}