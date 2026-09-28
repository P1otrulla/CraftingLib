package dev.piotrulla.craftinglib.recipe.manager;

import dev.piotrulla.craftinglib.recipe.KeyedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Default manager keeping recipes in registration order.
 * Not thread-safe: use it from the server main thread, like the rest of the platform recipe API.
 *
 * @param <R> recipe type
 */
public class SimpleRecipeManager<R extends KeyedRecipe> implements RecipeManager<R> {

    private final Map<String, R> recipes = new LinkedHashMap<>();
    private final PlatformRecipeRegistrar<R> registrar;

    public SimpleRecipeManager(@NotNull PlatformRecipeRegistrar<R> registrar) {
        this.registrar = Objects.requireNonNull(registrar, "registrar");
    }

    @Override
    public void registerRecipe(@NotNull R recipe) {
        Objects.requireNonNull(recipe, "recipe");

        this.unregisterRecipe(recipe.getKey());
        this.registrar.register(recipe);
        this.recipes.put(recipe.getKey(), recipe);
    }

    @Override
    public boolean unregisterRecipe(@NotNull String key) {
        Objects.requireNonNull(key, "key");

        R removed = this.recipes.remove(key);
        if (removed == null) {
            return false;
        }

        this.registrar.unregister(removed);
        return true;
    }

    @Override
    public void unregisterAll() {
        List<String> keys = new ArrayList<>(this.recipes.keySet());
        for (String key : keys) {
            this.unregisterRecipe(key);
        }
    }

    @NotNull
    @Override
    public Optional<R> findRecipe(@NotNull String key) {
        Objects.requireNonNull(key, "key");
        return Optional.ofNullable(this.recipes.get(key));
    }

    @NotNull
    @Override
    public Collection<R> getRecipes() {
        return Collections.unmodifiableList(new ArrayList<>(this.recipes.values()));
    }

    /**
     * Live read-only view for subclasses, without the snapshot copy.
     */
    @NotNull
    protected Collection<R> recipesView() {
        return Collections.unmodifiableCollection(this.recipes.values());
    }
}