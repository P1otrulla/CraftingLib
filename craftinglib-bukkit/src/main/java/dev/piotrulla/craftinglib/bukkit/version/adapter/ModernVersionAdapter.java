package dev.piotrulla.craftinglib.bukkit.version.adapter;

import dev.piotrulla.craftinglib.bukkit.item.RecipeChoices;
import dev.piotrulla.craftinglib.bukkit.version.VersionDetector;
import dev.piotrulla.craftinglib.recipe.book.RecipeBookSettings;
import dev.piotrulla.craftinglib.recipe.smelting.SmeltingRecipe;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.SmokingRecipe;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * 1.13+: {@code RecipeChoice}, recipe book groups; categories where the server has them and recipe resending on Paper when available.
 */
public class ModernVersionAdapter extends NamespacedVersionAdapter {

    private static final String SHAPED_RECIPE_CLASS = "org.bukkit.inventory.ShapedRecipe";
    private static final String COOKING_RECIPE_CLASS = "org.bukkit.inventory.CookingRecipe";
    private static final String CRAFTING_RECIPE_CLASS = "org.bukkit.inventory.CraftingRecipe";
    private static final String CRAFTING_BOOK_CATEGORY_CLASS = "org.bukkit.inventory.recipe.CraftingBookCategory";
    private static final String COOKING_BOOK_CATEGORY_CLASS = "org.bukkit.inventory.recipe.CookingBookCategory";
    private static final String ADD_RECIPE_METHOD = "addRecipe";
    private static final String REMOVE_RECIPE_METHOD = "removeRecipe";
    private static final String SET_GROUP_METHOD = "setGroup";
    private static final boolean RESEND_RECIPES = true;

    private final boolean cookingStationsSupported;
    private final boolean craftingGroupsSupported;
    private final boolean craftingCategoriesSupported;
    private final boolean cookingCategoriesSupported;
    private final boolean recipeResendSupported;
    private final boolean keyedRemovalSupported;

    public ModernVersionAdapter(@NotNull Plugin plugin) {
        super(plugin);
        this.cookingStationsSupported = VersionDetector.isClassPresent(COOKING_RECIPE_CLASS);
        this.craftingGroupsSupported = VersionDetector.isMethodPresent(SHAPED_RECIPE_CLASS, SET_GROUP_METHOD, String.class);
        this.craftingCategoriesSupported = VersionDetector.isClassPresent(CRAFTING_RECIPE_CLASS)
                && VersionDetector.isClassPresent(CRAFTING_BOOK_CATEGORY_CLASS);
        this.cookingCategoriesSupported = this.cookingStationsSupported && VersionDetector.isClassPresent(COOKING_BOOK_CATEGORY_CLASS);
        Class<?> serverClass = plugin.getServer().getClass();
        this.recipeResendSupported = VersionDetector.isMethodImplemented(serverClass, ADD_RECIPE_METHOD, Recipe.class, boolean.class);
        this.keyedRemovalSupported = VersionDetector.isMethodImplemented(serverClass, REMOVE_RECIPE_METHOD, NamespacedKey.class, boolean.class);
    }

    @NotNull
    @Override
    public ShapedRecipe createShapedRecipe(@NotNull String key, @NotNull ItemStack result, @NotNull RecipeBookSettings recipeBook) {
        ShapedRecipe serverRecipe = super.createShapedRecipe(key, result, recipeBook);
        if (this.craftingGroupsSupported && recipeBook.hasGroup()) {
            serverRecipe.setGroup(recipeBook.getGroup());
        }

        this.applyCraftingCategory(serverRecipe, recipeBook);
        return serverRecipe;
    }

    @NotNull
    @Override
    public ShapelessRecipe createShapelessRecipe(@NotNull String key, @NotNull ItemStack result, @NotNull RecipeBookSettings recipeBook) {
        ShapelessRecipe serverRecipe = super.createShapelessRecipe(key, result, recipeBook);
        if (this.craftingGroupsSupported && recipeBook.hasGroup()) {
            serverRecipe.setGroup(recipeBook.getGroup());
        }

        this.applyCraftingCategory(serverRecipe, recipeBook);
        return serverRecipe;
    }

    @Override
    public void setIngredient(@NotNull ShapedRecipe recipe, char symbol, @NotNull ItemStack ingredient) {
        recipe.setIngredient(symbol, ingredient.getType());
    }

    @Override
    public void addIngredient(@NotNull ShapelessRecipe recipe, @NotNull ItemStack ingredient) {
        recipe.addIngredient(ingredient.getType());
    }

    @NotNull
    @Override
    public Recipe createCookingRecipe(@NotNull SmeltingRecipe<ItemStack> recipe) {
        NamespacedKey key = this.createKey(recipe.getKey());

        if (!this.cookingStationsSupported) {
            this.requireFurnace(recipe);
            return new FurnaceRecipe(key, recipe.getResult(), recipe.getInput().getType(),
                    recipe.getExperience(), recipe.getCookingTime());
        }

        CookingRecipe<?> serverRecipe = this.createStationRecipe(key, recipe);
        RecipeBookSettings recipeBook = recipe.getRecipeBook();
        if (recipeBook.hasGroup()) {
            serverRecipe.setGroup(recipeBook.getGroup());
        }
        if (this.cookingCategoriesSupported) {
            recipeBook.getCategory().ifPresent(category -> RecipeBookCategories.applyCooking(serverRecipe, category));
        }

        return serverRecipe;
    }

    @Override
    public boolean isCookingRecipe(@NotNull Recipe serverRecipe) {
        if (!this.cookingStationsSupported) {
            return super.isCookingRecipe(serverRecipe);
        }

        return serverRecipe instanceof CookingRecipe;
    }

    @Override
    public boolean isCookingRecipeFor(@NotNull Recipe serverRecipe, @NotNull SmeltingRecipe<ItemStack> recipe) {
        if (!this.cookingStationsSupported) {
            return super.isCookingRecipeFor(serverRecipe, recipe);
        }

        return this.stationClassOf(recipe).isInstance(serverRecipe)
                && ((CookingRecipe<?>) serverRecipe).getInputChoice().test(recipe.getInput());
    }

    /**
     * Falls back to the plain variant on servers (and test mocks) that declare but do not implement resending.
     * {@link AbstractMethodError} is caught as a last resort for implementations built against an older API.
     */
    @Override
    public boolean addRecipe(@NotNull Server server, @NotNull Recipe serverRecipe) {
        if (!this.recipeResendSupported) {
            return super.addRecipe(server, serverRecipe);
        }

        try {
            return server.addRecipe(serverRecipe, RESEND_RECIPES);
        }
        catch (UnsupportedOperationException | AbstractMethodError exception) {
            return super.addRecipe(server, serverRecipe);
        }
    }

    @Override
    public boolean removeRecipe(@NotNull Server server, @NotNull String key) {
        if (!this.keyedRemovalSupported) {
            return super.removeRecipe(server, key);
        }

        try {
            return server.removeRecipe(this.createKey(key), RESEND_RECIPES);
        }
        catch (UnsupportedOperationException | AbstractMethodError exception) {
            return super.removeRecipe(server, key);
        }
    }

    @NotNull
    @Override
    public String getName() {
        return this.cookingStationsSupported ? "modern (1.14+)" : "modern (1.13)";
    }

    private void applyCraftingCategory(Recipe serverRecipe, RecipeBookSettings recipeBook) {
        if (this.craftingCategoriesSupported) {
            recipeBook.getCategory().ifPresent(category -> RecipeBookCategories.applyCrafting(serverRecipe, category));
        }
    }

    private CookingRecipe<?> createStationRecipe(NamespacedKey key, SmeltingRecipe<ItemStack> recipe) {
        RecipeChoice input = RecipeChoices.of(recipe.getInput(), recipe.getMatchMode());
        ItemStack result = recipe.getResult();
        float experience = recipe.getExperience();
        int cookingTime = recipe.getCookingTime();

        switch (recipe.getCookingType()) {
            case FURNACE:
                return new FurnaceRecipe(key, result, input, experience, cookingTime);
            case BLASTING:
                return new BlastingRecipe(key, result, input, experience, cookingTime);
            case SMOKING:
                return new SmokingRecipe(key, result, input, experience, cookingTime);
            case CAMPFIRE:
                return new CampfireRecipe(key, result, input, experience, cookingTime);
            default:
                throw new IllegalArgumentException("Unsupported cooking type: " + recipe.getCookingType());
        }
    }

    private Class<? extends CookingRecipe<?>> stationClassOf(SmeltingRecipe<ItemStack> recipe) {
        switch (recipe.getCookingType()) {
            case FURNACE:
                return FurnaceRecipe.class;
            case BLASTING:
                return BlastingRecipe.class;
            case SMOKING:
                return SmokingRecipe.class;
            case CAMPFIRE:
                return CampfireRecipe.class;
            default:
                throw new IllegalArgumentException("Unsupported cooking type: " + recipe.getCookingType());
        }
    }
}
