package dev.piotrulla.craftinglib.bukkit.config;

import dev.piotrulla.craftinglib.CraftingException;
import dev.piotrulla.craftinglib.bukkit.BukkitRecipes;
import dev.piotrulla.craftinglib.bukkit.builder.BukkitShapedRecipeBuilder;
import dev.piotrulla.craftinglib.bukkit.builder.BukkitShapelessRecipeBuilder;
import dev.piotrulla.craftinglib.recipe.CraftingRecipe;
import dev.piotrulla.craftinglib.recipe.RecipeType;
import dev.piotrulla.craftinglib.recipe.ShapedCraftingRecipe;
import dev.piotrulla.craftinglib.recipe.ShapelessCraftingRecipe;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link CraftingRecipe} (shaped and shapeless):
 * <pre>
 * super-pickaxe:
 *   type: SHAPED
 *   pattern: ["DDD", " S ", " S "]
 *   ingredients:
 *     D: {item stack}
 *     S: STICK
 *   mirrored: true
 *   result: {item stack}
 *   match-mode: EXACT
 * </pre>
 * Shapeless recipes have {@code ingredients} as a list and no pattern. Shared settings: {@link RecipeSectionSerializer}.
 */
final class CraftingSectionSerializer extends RecipeSectionSerializer<CraftingRecipe<ItemStack>> {

    private static final String TYPE = "type";
    private static final String PATTERN = "pattern";
    private static final String INGREDIENTS = "ingredients";
    private static final String MIRRORED = "mirrored";

    @Override
    protected void writeFields(@NotNull CraftingRecipe<ItemStack> recipe, @NotNull ConfigurationSection section) {
        section.set(TYPE, recipe.getType().name());

        if (recipe instanceof ShapedCraftingRecipe) {
            this.writeShaped((ShapedCraftingRecipe<ItemStack>) recipe, section);
        }
        else if (recipe instanceof ShapelessCraftingRecipe) {
            section.set(INGREDIENTS, new ArrayList<>(((ShapelessCraftingRecipe<ItemStack>) recipe).getIngredients()));
        }
        else {
            throw new CraftingException("Unsupported recipe implementation: " + recipe.getClass().getName());
        }
    }

    @NotNull
    @Override
    protected CraftingRecipe<ItemStack> readFields(@NotNull SectionReader reader) {
        RecipeType type = reader.requireEnum(TYPE, RecipeType.class);

        switch (type) {
            case SHAPED:
                return this.readShaped(reader);
            case SHAPELESS:
                return this.readShapeless(reader);
            default:
                throw new CraftingException("Recipe '" + reader.getKey() + "': unsupported type " + type);
        }
    }

    private void writeShaped(ShapedCraftingRecipe<ItemStack> recipe, ConfigurationSection section) {
        section.set(PATTERN, new ArrayList<>(recipe.getPattern().getRows()));

        ConfigurationSection ingredients = section.createSection(INGREDIENTS);
        recipe.getIngredients().forEach((symbol, ingredient) -> ingredients.set(String.valueOf(symbol), ingredient));

        section.set(MIRRORED, recipe.isMirrored());
    }

    private ShapedCraftingRecipe<ItemStack> readShaped(SectionReader reader) {
        List<String> pattern = reader.requireStringList(PATTERN);

        BukkitShapedRecipeBuilder builder = BukkitRecipes.shaped(reader.getKey()).withPattern(pattern.toArray(new String[0]));
        reader.requireSymbolMap(INGREDIENTS).forEach(builder::withIngredient);
        reader.findBoolean(MIRRORED).ifPresent(builder::withMirroring);

        return this.buildWithSettings(builder, reader);
    }

    private ShapelessCraftingRecipe<ItemStack> readShapeless(SectionReader reader) {
        BukkitShapelessRecipeBuilder builder = BukkitRecipes.shapeless(reader.getKey());
        for (ItemStack ingredient : reader.requireItemList(INGREDIENTS)) {
            builder.withIngredient(ingredient);
        }

        return this.buildWithSettings(builder, reader);
    }
}
