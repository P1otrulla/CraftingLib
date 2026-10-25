package dev.piotrulla.craftinglib.bukkit.config;

import dev.piotrulla.craftinglib.CraftingException;
import dev.piotrulla.craftinglib.item.MatchMode;
import dev.piotrulla.craftinglib.recipe.CustomRecipe;
import dev.piotrulla.craftinglib.recipe.book.RecipeBookCategory;
import dev.piotrulla.craftinglib.recipe.book.RecipeBookSettings;
import dev.piotrulla.craftinglib.recipe.builder.AbstractRecipeBuilder;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Writes and reads recipes of one type in a Bukkit {@link ConfigurationSection} (e.g. {@code FileConfiguration}).
 * Every recipe is a section named after its key; optional settings are written only when they differ from the default:
 * <pre>
 * super-pickaxe:
 *   ...recipe type fields...
 *   result: {item stack}
 *   match-mode: EXACT
 *   replace-vanilla: true           # optional, default false
 *   discoverable: false             # optional, default true
 *   recipe-book-group: pickaxes     # optional
 *   recipe-book-category: EQUIPMENT # optional
 * </pre>
 * Get instances from {@link BukkitRecipeSerializers}. Stateless and thread-safe.
 *
 * @param <R> recipe type
 */
public abstract class RecipeSectionSerializer<R extends CustomRecipe<ItemStack>> {

    protected static final String RESULT = "result";
    protected static final String MATCH_MODE = "match-mode";
    protected static final String REPLACE_VANILLA = "replace-vanilla";
    protected static final String DISCOVERABLE = "discoverable";
    protected static final String RECIPE_BOOK_GROUP = "recipe-book-group";
    protected static final String RECIPE_BOOK_CATEGORY = "recipe-book-category";

    private static final char DEFAULT_PATH_SEPARATOR = '.';

    RecipeSectionSerializer() {
    }

    /**
     * Writes the recipe as {@code parent.<recipe key>}, replacing anything stored there before.
     *
     * @throws CraftingException when the key contains the config path separator (by default {@code .})
     */
    public final void write(@NotNull ConfigurationSection parent, @NotNull R recipe) {
        String key = recipe.getKey();
        char separator = this.pathSeparator(parent);
        if (key.indexOf(separator) >= 0) {
            throw new CraftingException("Recipe '" + key + "': key contains the config path separator '" + separator
                    + "', so it cannot be used as a section name");
        }

        ConfigurationSection section = parent.createSection(key);
        this.writeFields(recipe, section);
        section.set(RESULT, recipe.getResult());
        section.set(MATCH_MODE, recipe.getMatchMode().name());

        if (recipe.shouldReplaceVanilla()) {
            section.set(REPLACE_VANILLA, true);
        }
        this.writeRecipeBook(recipe.getRecipeBook(), section);
    }

    /**
     * Writes every recipe as a child section of {@code parent}, in iteration order.
     */
    public final void writeAll(@NotNull ConfigurationSection parent, @NotNull Collection<? extends R> recipes) {
        for (R recipe : new ArrayList<>(recipes)) {
            this.write(parent, recipe);
        }
    }

    /**
     * Reads one recipe; its key is the name of the section.
     *
     * @throws CraftingException when a field is missing or invalid, the message names the recipe and the field
     */
    @NotNull
    public final R read(@NotNull ConfigurationSection recipeSection) {
        return this.readFields(new SectionReader(recipeSection));
    }

    /**
     * Reads every child section of {@code parent} as a recipe, in config order.
     *
     * @throws CraftingException when a child is not a section or a recipe is invalid
     */
    @NotNull
    public final List<R> readAll(@NotNull ConfigurationSection parent) {
        List<R> recipes = new ArrayList<>();
        for (String key : parent.getKeys(false)) {
            ConfigurationSection recipeSection = parent.getConfigurationSection(key);
            if (recipeSection == null) {
                throw new CraftingException("Recipe '" + key + "' must be a section, got: " + parent.get(key));
            }
            recipes.add(this.read(recipeSection));
        }

        return Collections.unmodifiableList(recipes);
    }

    /**
     * Writes fields specific to the recipe type, before the shared settings.
     */
    protected abstract void writeFields(@NotNull R recipe, @NotNull ConfigurationSection section);

    /**
     * Reads fields specific to the recipe type and builds it with {@link #buildWithSettings}.
     */
    @NotNull
    protected abstract R readFields(@NotNull SectionReader reader);

    @NotNull
    protected <X extends R, B extends AbstractRecipeBuilder<ItemStack, X, B>> X buildWithSettings(
            @NotNull B builder,
            @NotNull SectionReader reader
    ) {
        builder.withResult(reader.requireItem(RESULT));
        reader.findEnum(MATCH_MODE, MatchMode.class).ifPresent(builder::withMatchMode);
        reader.findBoolean(REPLACE_VANILLA).ifPresent(builder::replaceVanillaRecipes);
        reader.findBoolean(DISCOVERABLE).ifPresent(builder::discoverable);
        reader.findString(RECIPE_BOOK_GROUP).ifPresent(builder::withRecipeBookGroup);
        reader.findEnum(RECIPE_BOOK_CATEGORY, RecipeBookCategory.class).ifPresent(builder::withRecipeBookCategory);

        return builder.build();
    }

    /**
     * Writes an optional item only when present, so absence reads back as empty.
     */
    protected void setOptionalItem(@NotNull ConfigurationSection section, @NotNull String field, @NotNull Optional<ItemStack> item) {
        item.ifPresent(present -> section.set(field, present));
    }

    private void writeRecipeBook(RecipeBookSettings recipeBook, ConfigurationSection section) {
        if (!recipeBook.isDiscoverable()) {
            section.set(DISCOVERABLE, false);
        }
        if (recipeBook.hasGroup()) {
            section.set(RECIPE_BOOK_GROUP, recipeBook.getGroup());
        }
        recipeBook.getCategory().ifPresent(category -> section.set(RECIPE_BOOK_CATEGORY, category.name()));
    }

    private char pathSeparator(ConfigurationSection section) {
        Configuration root = section.getRoot();
        return root == null ? DEFAULT_PATH_SEPARATOR : root.options().pathSeparator();
    }
}
