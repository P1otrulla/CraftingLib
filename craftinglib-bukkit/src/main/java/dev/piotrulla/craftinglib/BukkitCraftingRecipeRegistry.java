package dev.piotrulla.craftinglib;

import dev.piotrulla.craftinglib.registry.CraftingRecipeRegistry;
import dev.piotrulla.craftinglib.registry.RegistryException;
import dev.piotrulla.craftinglib.version.VersionAdapter;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class BukkitCraftingRecipeRegistry implements CraftingRecipeRegistry<ItemStack> {

    private static final char[] CHARS_3X3 = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'};
    private static final char[] CHARS_2X2 = {'A', 'B', 'C', 'D'};

    private final Server server;
    private final VersionAdapter versionAdapter;
    private final AtomicInteger registeredCount = new AtomicInteger(0);

    public BukkitCraftingRecipeRegistry(@NotNull Server server, @NotNull VersionAdapter versionAdapter) {
        this.server = Objects.requireNonNull(server, "Server cannot be null");
        this.versionAdapter = Objects.requireNonNull(versionAdapter, "Version adapter cannot be null");
    }

    @Override
    public void addRecipe(@NotNull CraftingRecipe<ItemStack> recipe) throws RegistryException {
        if (!(recipe instanceof BukkitCraftingRecipe)) {
            throw new RegistryException("Recipe must be a BukkitRecipe, got: " + recipe.getClass().getSimpleName());
        }

        BukkitCraftingRecipe bukkitRecipe = (BukkitCraftingRecipe) recipe;

        try {
            ShapedRecipe shapedRecipe = createShapedRecipe(bukkitRecipe);
            this.server.addRecipe(shapedRecipe);
            this.registeredCount.incrementAndGet();
        }
        catch (Exception exception) {
            throw new RegistryException("Failed to add recipe: " + recipe.name(), exception);
        }
    }

    @Override
    public void removeRecipe(@NotNull CraftingRecipe<ItemStack> recipe) throws RegistryException {
        if (!(recipe instanceof BukkitCraftingRecipe)) {
            throw new RegistryException("Recipe must be a BukkitRecipe, got: " + recipe.getClass().getSimpleName());
        }

        BukkitCraftingRecipe bukkitRecipe = (BukkitCraftingRecipe) recipe;

        try {
            removeRecipesByResult(bukkitRecipe.result().item());
        }
        catch (Exception exception) {
            throw new RegistryException("Failed to remove recipe: " + recipe.name(), exception);
        }
    }

    @Override
    public void removeRecipesByResult(@NotNull ItemStack result) throws RegistryException {
        try {
            int removedCount = this.removeMatchingRecipes(result);
            this.registeredCount.addAndGet(-removedCount);
        }
        catch (Exception exception) {
            throw new RegistryException("Failed to remove recipes for result: " + result.getType(), exception);
        }
    }

    private int removeMatchingRecipes(@NotNull ItemStack itemStack) {
        int removedCount = 0;
        Iterator<Recipe> iterator = this.server.recipeIterator();

        while (iterator.hasNext()) {
            Recipe recipe = iterator.next();

            if (this.shouldRemoveRecipe(recipe, itemStack)) {
                iterator.remove();
                removedCount++;
            }
        }

        return removedCount;
    }

    private boolean shouldRemoveRecipe(@NotNull Recipe recipe, @NotNull ItemStack itemStack) {
        return recipe != null && recipe.getResult() == itemStack;
    }

    @Override
    public boolean supportsRecipe(@NotNull CraftingRecipe<ItemStack> recipe) {
        return recipe instanceof BukkitCraftingRecipe;
    }

    @Override
    public int getRegisteredRecipeCount() {
        return this.registeredCount.get();
    }

    /**
     * Removes all vanilla recipes that produce the given result.
     * This is used when a custom recipe should replace vanilla recipes.
     */
    public void removeVanillaRecipes(@NotNull ItemStack result) throws RegistryException {
        removeRecipesByResult(result);
    }

    /**
     * Gets the server this registry is bound to.
     */
    @NotNull
    public Server getServer() {
        return this.server;
    }

    /**
     * Gets the version adapter used by this registry.
     */
    @NotNull
    public VersionAdapter getVersionAdapter() {
        return this.versionAdapter;
    }

    private ShapedRecipe createShapedRecipe(BukkitCraftingRecipe bukkitRecipe) throws RegistryException {
        try {
            ShapedRecipe recipe = this.versionAdapter.createShapedRecipe(
                    bukkitRecipe.result().item(),
                    bukkitRecipe.id()
            );

            if (bukkitRecipe.getCraftingType() == BukkitCraftingRecipe.CraftingType.PLAYER_INVENTORY_2X2) {
                setup2x2Recipe(recipe, bukkitRecipe);
            }
            else {
                setup3x3Recipe(recipe, bukkitRecipe);
            }

            return recipe;
        } catch (Exception exception) {
            throw new RegistryException("Failed to create shaped recipe for: " + bukkitRecipe.name(), exception);
        }
    }

    private void setup2x2Recipe(ShapedRecipe recipe, BukkitCraftingRecipe bukkitRecipe) throws RegistryException {
        try {
            recipe.shape("AB", "CD");

            ItemStack[] ingredients = extractIngredients(bukkitRecipe, 2, 2);
            for (int i = 0; i < 4; i++) {
                ItemStack item = ingredients[i];
                if (item == null) {
                    item = new ItemStack(Material.AIR);
                }
                this.versionAdapter.setIngredient(recipe, CHARS_2X2[i], item);
            }
        } catch (Exception exception) {
            throw new RegistryException("Failed to setup 2x2 recipe: " + bukkitRecipe.name(), exception);
        }
    }

    private void setup3x3Recipe(ShapedRecipe recipe, BukkitCraftingRecipe bukkitRecipe) throws RegistryException {
        try {
            recipe.shape("ABC", "DEF", "GHI");

            ItemStack[] ingredients = extractIngredients(bukkitRecipe, 3, 3);
            for (int i = 0; i < 9; i++) {
                ItemStack item = ingredients[i];
                if (item == null) {
                    item = new ItemStack(Material.AIR);
                }
                this.versionAdapter.setIngredient(recipe, CHARS_3X3[i], item);
            }
        }
        catch (Exception exception) {
            throw new RegistryException("Failed to setup 3x3 recipe: " + bukkitRecipe.name(), exception);
        }
    }

    private ItemStack[] extractIngredients(BukkitCraftingRecipe bukkitRecipe, int width, int height) {
        ItemStack[] result = new ItemStack[width * height];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int index = row * width + col;
                try {
                    result[index] = bukkitRecipe.getPattern().getIngredientAt(row, col);
                }
                catch (Exception ignored) {
                    result[index] = null; // Handle out of bounds gracefully maybe?
                }
            }
        }

        return result;
    }
}
