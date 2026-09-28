package dev.piotrulla.craftinglib.bukkit.registry;

import dev.piotrulla.craftinglib.CraftingException;
import dev.piotrulla.craftinglib.bukkit.book.RecipeBook;
import dev.piotrulla.craftinglib.bukkit.version.VersionAdapter;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.logging.Logger;

/**
 * Adds, removes and recognizes our server recipes of one kind (crafting, cooking, stonecutting...).
 * <p>
 * Keyed recipes are recognized by key. Recipes without keys (crafting on 1.8-1.11, cooking on 1.8-1.12)
 * count as ours when their result is similar to the result of a recipe we registered.
 * The kind filter keeps e.g. a smelting result from matching a vanilla crafting recipe.
 * Discoverable recipes are published to the {@link RecipeBook} while registered.
 */
public final class ServerRecipeRegistry {

    private final Server server;
    private final VersionAdapter versionAdapter;
    private final RecipeBook recipeBook;
    private final Logger logger;
    private final Predicate<Recipe> handledKind;
    private final Map<String, ItemStack> registeredResults = new HashMap<>();

    public ServerRecipeRegistry(@NotNull RegistrarContext context, @NotNull Predicate<Recipe> handledKind) {
        Objects.requireNonNull(context, "context");
        this.server = context.getServer();
        this.versionAdapter = context.getVersionAdapter();
        this.recipeBook = context.getRecipeBook();
        this.logger = context.getLogger();
        this.handledKind = Objects.requireNonNull(handledKind, "handledKind");
    }

    /**
     * Replaces a recipe registered under the same key. Re-adding keeps it discovered without a new unlock toast.
     *
     * @param discoverable true to unlock the recipe in the recipe book of every player
     */
    public void add(@NotNull String key, @NotNull Recipe serverRecipe, boolean discoverable) {
        this.removeServerRecipe(key);

        if (!this.versionAdapter.addRecipe(this.server, serverRecipe)) {
            throw new CraftingException("Server rejected recipe '" + key + "'");
        }
        this.registeredResults.put(key, serverRecipe.getResult());

        if (discoverable) {
            this.recipeBook.publish(key);
        }
        else {
            this.recipeBook.retract(key);
        }
    }

    public void remove(@NotNull String key) {
        this.removeServerRecipe(key);
        this.recipeBook.retract(key);
    }

    public void removeForeign(@NotNull Predicate<Recipe> filter) {
        this.removeIf(serverRecipe -> filter.test(serverRecipe) && !this.isOwned(serverRecipe));
    }

    public boolean isOwned(@Nullable Recipe serverRecipe) {
        if (serverRecipe == null) {
            return false;
        }

        for (String key : this.registeredResults.keySet()) {
            if (this.isOwnedBy(serverRecipe, key)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Scans all recipes when removal by key is unsupported, also cleaning up leftovers of a previous plugin instance.
     */
    private void removeServerRecipe(String key) {
        if (!this.versionAdapter.removeRecipe(this.server, key)) {
            this.removeIf(serverRecipe -> this.isOwnedBy(serverRecipe, key));
        }

        this.registeredResults.remove(key);
    }

    private boolean isOwnedBy(Recipe serverRecipe, String key) {
        if (!this.handledKind.test(serverRecipe)) {
            return false;
        }
        if (this.versionAdapter.isKeyed(serverRecipe)) {
            return key.equals(this.versionAdapter.getOwnedKey(serverRecipe));
        }

        ItemStack registeredResult = this.registeredResults.get(key);
        return registeredResult != null && registeredResult.isSimilar(serverRecipe.getResult());
    }

    private void removeIf(Predicate<Recipe> filter) {
        try {
            Iterator<Recipe> iterator = this.server.recipeIterator();
            while (iterator.hasNext()) {
                Recipe serverRecipe = iterator.next();
                if (serverRecipe != null && this.handledKind.test(serverRecipe) && filter.test(serverRecipe)) {
                    iterator.remove();
                }
            }
        }
        catch (UnsupportedOperationException exception) {
            this.logger.warning("This server does not support removing recipes, conflicting recipes stay registered");
        }
    }
}
