package dev.piotrulla.craftinglib.bukkit.listener;

import dev.piotrulla.craftinglib.bukkit.registry.BukkitRecipeRegistrar;
import dev.piotrulla.craftinglib.grid.CraftingGrid;
import dev.piotrulla.craftinglib.recipe.CraftingRecipe;
import dev.piotrulla.craftinglib.recipe.RecipeMatch;
import dev.piotrulla.craftinglib.recipe.manager.CraftingRecipeManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * Crafting decisions shared by the workbench/inventory listener and the crafter listener.
 */
public final class CraftingGuard {

    private final CraftingRecipeManager<ItemStack> recipeManager;
    private final BukkitRecipeRegistrar registrar;

    public CraftingGuard(@NotNull CraftingRecipeManager<ItemStack> recipeManager, @NotNull BukkitRecipeRegistrar registrar) {
        this.recipeManager = Objects.requireNonNull(recipeManager, "recipeManager");
        this.registrar = Objects.requireNonNull(registrar, "registrar");
    }

    /**
     * @param matrix 4 or 9 slots, other sizes never match
     */
    @NotNull
    public Optional<RecipeMatch<ItemStack>> findMatch(@NotNull ItemStack[] matrix) {
        if (!CraftingGrid.isSupportedSlotCount(matrix.length)) {
            return Optional.empty();
        }

        return this.recipeManager.findMatch(CraftingGrid.fromArray(matrix));
    }

    /**
     * Our own server recipe whose grid did not pass exact matching, or a recipe whose result type
     * is replaced by one of our recipes.
     */
    public boolean isBlocked(@Nullable Recipe serverRecipe) {
        if (serverRecipe == null) {
            return false;
        }

        return this.registrar.isCustomRecipe(serverRecipe) || this.isReplacedResult(serverRecipe.getResult().getType());
    }

    private boolean isReplacedResult(Material resultType) {
        for (CraftingRecipe<ItemStack> recipe : this.recipeManager.getRecipes()) {
            if (recipe.shouldReplaceVanilla() && recipe.getResult().getType() == resultType) {
                return true;
            }
        }

        return false;
    }
}