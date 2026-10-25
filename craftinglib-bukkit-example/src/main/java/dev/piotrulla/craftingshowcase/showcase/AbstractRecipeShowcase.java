package dev.piotrulla.craftingshowcase.showcase;

import dev.piotrulla.craftinglib.recipe.CustomRecipe;
import dev.piotrulla.craftinglib.recipe.manager.RecipeManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Menu metadata and the list of registered recipes, shared by every showcase.
 */
public abstract class AbstractRecipeShowcase implements RecipeShowcase {

    private final String name;
    private final String title;
    private final String description;
    private final Material icon;
    private final List<CustomRecipe<ItemStack>> recipes = new ArrayList<>();

    protected AbstractRecipeShowcase(@NotNull String name, @NotNull String title, @NotNull String description, @NotNull Material icon) {
        this.name = Objects.requireNonNull(name, "name");
        this.title = Objects.requireNonNull(title, "title");
        this.description = Objects.requireNonNull(description, "description");
        this.icon = Objects.requireNonNull(icon, "icon");
    }

    @NotNull
    @Override
    public final String getName() {
        return this.name;
    }

    @NotNull
    @Override
    public final String getTitle() {
        return this.title;
    }

    @NotNull
    @Override
    public final String getDescription() {
        return this.description;
    }

    @NotNull
    @Override
    public final Material getIcon() {
        return this.icon;
    }

    @NotNull
    @Override
    public final List<CustomRecipe<ItemStack>> getRecipes() {
        return List.copyOf(this.recipes);
    }

    /**
     * Registers the recipe and remembers it for the menu.
     */
    protected final <R extends CustomRecipe<ItemStack>> void registerRecipe(@NotNull RecipeManager<R> manager, @NotNull R recipe) {
        manager.registerRecipe(recipe);
        this.recipes.add(recipe);
    }
}
