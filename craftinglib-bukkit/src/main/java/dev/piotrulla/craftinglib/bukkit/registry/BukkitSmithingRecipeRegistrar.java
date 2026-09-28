package dev.piotrulla.craftinglib.bukkit.registry;

import dev.piotrulla.craftinglib.CraftingException;
import dev.piotrulla.craftinglib.bukkit.item.RecipeChoices;
import dev.piotrulla.craftinglib.bukkit.version.VersionDetector;
import dev.piotrulla.craftinglib.item.MatchMode;
import dev.piotrulla.craftinglib.recipe.SmithingRecipe;
import dev.piotrulla.craftinglib.recipe.manager.PlatformRecipeRegistrar;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Registers smithing recipes as native server recipes.
 * 1.20+: transform recipes (template required). 1.16-1.19: legacy smithing recipes (no template).
 * Create it only when {@code VersionDetector.isSmithingSupported()} returns true.
 * <p>
 * Like vanilla netherite upgrades, the server copies the base item's data (enchantments, name...) onto the result.
 * {@code replaceVanilla} removes other transform recipes accepting the same template, base and addition.
 */
public final class BukkitSmithingRecipeRegistrar implements PlatformRecipeRegistrar<SmithingRecipe<ItemStack>> {

    private static final String TRANSFORM_RECIPE_CLASS = "org.bukkit.inventory.SmithingTransformRecipe";

    private static final boolean NOT_IN_RECIPE_BOOK = false;

    private final Plugin plugin;
    private final boolean templatesRequired;
    private final ServerRecipeRegistry serverRecipes;

    public BukkitSmithingRecipeRegistrar(@NotNull RegistrarContext context) {
        Objects.requireNonNull(context, "context");
        this.plugin = context.getPlugin();
        this.templatesRequired = VersionDetector.isClassPresent(TRANSFORM_RECIPE_CLASS);
        this.serverRecipes = new ServerRecipeRegistry(context, this::isHandledRecipe);
    }

    @Override
    public void register(@NotNull SmithingRecipe<ItemStack> recipe) {
        Objects.requireNonNull(recipe, "recipe");
        this.validateTemplate(recipe);

        if (recipe.shouldReplaceVanilla()) {
            this.serverRecipes.removeForeign(serverRecipe -> this.acceptsSameItems(serverRecipe, recipe));
        }

        this.serverRecipes.add(recipe.getKey(), this.toServerRecipe(recipe), NOT_IN_RECIPE_BOOK);
    }

    @Override
    public void unregister(@NotNull SmithingRecipe<ItemStack> recipe) {
        Objects.requireNonNull(recipe, "recipe");
        this.serverRecipes.remove(recipe.getKey());
    }

    private void validateTemplate(SmithingRecipe<ItemStack> recipe) {
        boolean hasTemplate = recipe.getTemplate().isPresent();

        if (this.templatesRequired && !hasTemplate) {
            throw new CraftingException("Smithing recipe '" + recipe.getKey() + "' needs a template on Minecraft 1.20+");
        }
        if (!this.templatesRequired && hasTemplate) {
            throw new CraftingException("Smithing recipe '" + recipe.getKey() + "': templates require Minecraft 1.20+");
        }
    }

    private Recipe toServerRecipe(SmithingRecipe<ItemStack> recipe) {
        NamespacedKey key = new NamespacedKey(this.plugin, recipe.getKey());
        MatchMode matchMode = recipe.getMatchMode();
        RecipeChoice base = RecipeChoices.of(recipe.getBase(), matchMode);
        RecipeChoice addition = RecipeChoices.of(recipe.getAddition(), matchMode);

        if (!this.templatesRequired) {
            return this.createLegacyRecipe(key, recipe.getResult(), base, addition);
        }

        RecipeChoice template = RecipeChoices.of(recipe.getTemplate().orElseThrow(IllegalStateException::new), matchMode);
        return new SmithingTransformRecipe(key, recipe.getResult(), template, base, addition);
    }

    @SuppressWarnings("deprecation")
    private Recipe createLegacyRecipe(NamespacedKey key, ItemStack result, RecipeChoice base, RecipeChoice addition) {
        return new org.bukkit.inventory.SmithingRecipe(key, result, base, addition);
    }

    /**
     * Trim recipes are skipped on 1.20+, only transform recipes are handled.
     */
    private boolean isHandledRecipe(Recipe serverRecipe) {
        return this.templatesRequired
                ? serverRecipe instanceof SmithingTransformRecipe
                : serverRecipe instanceof org.bukkit.inventory.SmithingRecipe;
    }

    private boolean acceptsSameItems(Recipe serverRecipe, SmithingRecipe<ItemStack> recipe) {
        if (this.templatesRequired) {
            SmithingTransformRecipe transform = (SmithingTransformRecipe) serverRecipe;
            return transform.getTemplate().test(recipe.getTemplate().orElseThrow(IllegalStateException::new))
                    && transform.getBase().test(recipe.getBase())
                    && transform.getAddition().test(recipe.getAddition());
        }

        org.bukkit.inventory.SmithingRecipe smithing = (org.bukkit.inventory.SmithingRecipe) serverRecipe;
        return smithing.getBase().test(recipe.getBase()) && smithing.getAddition().test(recipe.getAddition());
    }
}