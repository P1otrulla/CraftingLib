package dev.piotrulla.craftinglib.okaeri;

import dev.piotrulla.craftinglib.CraftingException;
import dev.piotrulla.craftinglib.item.MatchMode;
import dev.piotrulla.craftinglib.recipe.CustomRecipe;
import dev.piotrulla.craftinglib.recipe.book.RecipeBookCategory;
import dev.piotrulla.craftinglib.recipe.book.RecipeBookSettings;
import dev.piotrulla.craftinglib.recipe.builder.AbstractRecipeBuilder;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Settings shared by every recipe type. Optional settings are written only when they differ from the default:
 * <pre>
 * key: my-recipe
 * ...recipe type fields...
 * result: {item stack}
 * match-mode: EXACT
 * replace-vanilla: true           # optional, default false
 * discoverable: false             # optional, default true
 * recipe-book-group: pickaxes     # optional
 * recipe-book-category: EQUIPMENT # optional
 * </pre>
 * Item stacks are handled by okaeri {@code SerdesBukkit}, which must be registered too.
 * Written for okaeri-configs 6.1 ({@code set*} writers, {@code supports(Class<?>)}).
 *
 * @param <R> recipe type
 */
public abstract class AbstractRecipeSerializer<R extends CustomRecipe<ItemStack>> implements ObjectSerializer<R> {

    protected static final String KEY = "key";
    protected static final String RESULT = "result";
    protected static final String MATCH_MODE = "match-mode";
    protected static final String REPLACE_VANILLA = "replace-vanilla";
    protected static final String DISCOVERABLE = "discoverable";
    protected static final String RECIPE_BOOK_GROUP = "recipe-book-group";
    protected static final String RECIPE_BOOK_CATEGORY = "recipe-book-category";

    @Override
    public final void serialize(@NotNull R recipe, @NotNull SerializationData data, @NotNull GenericsDeclaration generics) {
        data.set(KEY, recipe.getKey());
        this.serializeFields(recipe, data);
        data.set(RESULT, recipe.getResult(), ItemStack.class);
        data.set(MATCH_MODE, recipe.getMatchMode());

        if (recipe.shouldReplaceVanilla()) {
            data.set(REPLACE_VANILLA, true);
        }
        this.serializeRecipeBook(recipe.getRecipeBook(), data);
    }

    @Override
    public final R deserialize(@NotNull DeserializationData data, @NotNull GenericsDeclaration generics) {
        String key = this.find(data, KEY, String.class)
                .orElseThrow(() -> new CraftingException("Recipe is missing required field '" + KEY + "'"));

        return this.deserializeFields(key, data);
    }

    /**
     * Writes fields specific to the recipe type, between the key and the shared settings.
     */
    protected abstract void serializeFields(@NotNull R recipe, @NotNull SerializationData data);

    /**
     * Reads fields specific to the recipe type and builds it with {@link #buildWithSettings}.
     */
    @NotNull
    protected abstract R deserializeFields(@NotNull String key, @NotNull DeserializationData data);

    @NotNull
    protected <X extends R, B extends AbstractRecipeBuilder<ItemStack, X, B>> X buildWithSettings(
            @NotNull String key,
            @NotNull B builder,
            @NotNull DeserializationData data
    ) {
        builder.withResult(this.require(data, key, RESULT, ItemStack.class));
        this.find(data, MATCH_MODE, MatchMode.class).ifPresent(builder::withMatchMode);
        this.find(data, REPLACE_VANILLA, Boolean.class).ifPresent(builder::replaceVanillaRecipes);
        this.find(data, DISCOVERABLE, Boolean.class).ifPresent(builder::discoverable);
        this.find(data, RECIPE_BOOK_GROUP, String.class).ifPresent(builder::withRecipeBookGroup);
        this.find(data, RECIPE_BOOK_CATEGORY, RecipeBookCategory.class).ifPresent(builder::withRecipeBookCategory);

        return builder.build();
    }

    /**
     * Writes an optional item only when present, so absence reads back as empty.
     */
    protected void setOptionalItem(@NotNull SerializationData data, @NotNull String field, @NotNull Optional<ItemStack> item) {
        item.ifPresent(present -> data.set(field, present, ItemStack.class));
    }

    @NotNull
    protected <V> Optional<V> find(@NotNull DeserializationData data, @NotNull String field, @NotNull Class<V> type) {
        return data.containsKey(field) ? Optional.ofNullable(data.get(field, type)) : Optional.empty();
    }

    @NotNull
    protected <V> V require(@NotNull DeserializationData data, @NotNull String key, @NotNull String field, @NotNull Class<V> type) {
        return this.find(data, field, type)
                .orElseThrow(() -> new CraftingException("Recipe '" + key + "' is missing required field '" + field + "'"));
    }

    /**
     * For collection fields read with {@code getAsList}/{@code getAsMap}.
     */
    protected void requirePresent(@NotNull DeserializationData data, @NotNull String key, @NotNull String field) {
        if (!data.containsKey(field)) {
            throw new CraftingException("Recipe '" + key + "' is missing required field '" + field + "'");
        }
    }

    private void serializeRecipeBook(RecipeBookSettings recipeBook, SerializationData data) {
        if (!recipeBook.isDiscoverable()) {
            data.set(DISCOVERABLE, false);
        }
        if (recipeBook.hasGroup()) {
            data.set(RECIPE_BOOK_GROUP, recipeBook.getGroup());
        }
        recipeBook.getCategory().ifPresent(category -> data.set(RECIPE_BOOK_CATEGORY, category));
    }
}
