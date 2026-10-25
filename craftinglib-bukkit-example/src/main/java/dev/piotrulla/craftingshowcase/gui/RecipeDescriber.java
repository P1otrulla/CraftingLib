package dev.piotrulla.craftingshowcase.gui;

import dev.piotrulla.craftinglib.recipe.AnvilRecipe;
import dev.piotrulla.craftinglib.recipe.BrewingRecipe;
import dev.piotrulla.craftinglib.recipe.CustomRecipe;
import dev.piotrulla.craftinglib.recipe.GrindstoneRecipe;
import dev.piotrulla.craftinglib.recipe.ShapedCraftingRecipe;
import dev.piotrulla.craftinglib.recipe.ShapelessCraftingRecipe;
import dev.piotrulla.craftinglib.recipe.SmithingRecipe;
import dev.piotrulla.craftinglib.recipe.StonecuttingRecipe;
import dev.piotrulla.craftinglib.recipe.book.RecipeBookSettings;
import dev.piotrulla.craftinglib.recipe.smelting.SmeltingRecipe;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Builds menu lore straight from CraftingLib recipe objects, so the menu always shows what is registered.
 */
public final class RecipeDescriber {

    private static final int INVENTORY_GRID = 2;
    private static final float TICKS_PER_SECOND = 20.0F;
    private static final char EMPTY_SLOT = ' ';
    private static final char SHOWN_EMPTY_SLOT = '·';
    private static final String INDENT = "  ";

    @NotNull
    public List<Component> describe(@NotNull CustomRecipe<ItemStack> recipe) {
        List<Component> lines = new ArrayList<>();
        lines.add(this.line("Key", recipe.getKey()));

        if (recipe instanceof ShapedCraftingRecipe) {
            this.describeShaped((ShapedCraftingRecipe<ItemStack>) recipe, lines);
        }
        else if (recipe instanceof ShapelessCraftingRecipe) {
            this.describeShapeless((ShapelessCraftingRecipe<ItemStack>) recipe, lines);
        }
        else if (recipe instanceof SmeltingRecipe) {
            this.describeSmelting((SmeltingRecipe<ItemStack>) recipe, lines);
        }
        else if (recipe instanceof BrewingRecipe) {
            this.describeBrewing((BrewingRecipe<ItemStack>) recipe, lines);
        }
        else if (recipe instanceof StonecuttingRecipe) {
            this.describeStonecutting((StonecuttingRecipe<ItemStack>) recipe, lines);
        }
        else if (recipe instanceof SmithingRecipe) {
            this.describeSmithing((SmithingRecipe<ItemStack>) recipe, lines);
        }
        else if (recipe instanceof AnvilRecipe) {
            this.describeAnvil((AnvilRecipe<ItemStack>) recipe, lines);
        }
        else if (recipe instanceof GrindstoneRecipe) {
            this.describeGrindstone((GrindstoneRecipe<ItemStack>) recipe, lines);
        }

        this.describeSettings(recipe, lines);
        return lines;
    }

    private void describeShaped(ShapedCraftingRecipe<ItemStack> recipe, List<Component> lines) {
        lines.add(this.line("Type", "Shaped crafting"));
        recipe.getPattern().getRows().forEach(row ->
                lines.add(this.text(INDENT + "[" + row.replace(EMPTY_SLOT, SHOWN_EMPTY_SLOT) + "]", NamedTextColor.WHITE)));
        recipe.getIngredients().forEach((symbol, ingredient) ->
                lines.add(this.text(INDENT + symbol + " = " + ItemNames.withAmount(ingredient), NamedTextColor.WHITE)));
        this.describeGridFeatures(recipe.fitsGrid(INVENTORY_GRID), lines);
        if (recipe.isMirrored()) {
            lines.add(this.feature("Can be mirrored"));
        }
    }

    private void describeShapeless(ShapelessCraftingRecipe<ItemStack> recipe, List<Component> lines) {
        lines.add(this.line("Type", "Shapeless crafting"));
        recipe.getIngredients().forEach(ingredient ->
                lines.add(this.text(INDENT + "+ " + ItemNames.withAmount(ingredient), NamedTextColor.WHITE)));
        this.describeGridFeatures(recipe.fitsGrid(INVENTORY_GRID), lines);
    }

    private void describeGridFeatures(boolean fitsInventory, List<Component> lines) {
        if (fitsInventory) {
            lines.add(this.feature("Craftable in the player inventory (2x2)"));
        }
    }

    private void describeSmelting(SmeltingRecipe<ItemStack> recipe, List<Component> lines) {
        lines.add(this.line("Station", ItemNames.readable(recipe.getCookingType().name())));
        lines.add(this.line("Input", ItemNames.withAmount(recipe.getInput())));
        lines.add(this.line("Time", String.format(Locale.ROOT, "%.1fs", recipe.getCookingTime() / TICKS_PER_SECOND)));
        lines.add(this.line("Experience", String.format(Locale.ROOT, "%.2f", recipe.getExperience())));
    }

    private void describeBrewing(BrewingRecipe<ItemStack> recipe, List<Component> lines) {
        lines.add(this.line("Type", "Brewing"));
        lines.add(this.line("Potion", ItemNames.name(recipe.getInput())));
        lines.add(this.line("Ingredient", ItemNames.withAmount(recipe.getIngredient())));
    }

    private void describeStonecutting(StonecuttingRecipe<ItemStack> recipe, List<Component> lines) {
        lines.add(this.line("Type", "Stonecutter"));
        lines.add(this.line("Input", ItemNames.withAmount(recipe.getInput())));
    }

    private void describeSmithing(SmithingRecipe<ItemStack> recipe, List<Component> lines) {
        lines.add(this.line("Type", "Smithing table"));
        recipe.getTemplate().ifPresent(template -> lines.add(this.line("Template", ItemNames.name(template))));
        lines.add(this.line("Base", ItemNames.name(recipe.getBase())));
        lines.add(this.line("Addition", ItemNames.withAmount(recipe.getAddition())));
    }

    private void describeAnvil(AnvilRecipe<ItemStack> recipe, List<Component> lines) {
        lines.add(this.line("Type", "Anvil"));
        lines.add(this.line("Left", ItemNames.withAmount(recipe.getLeft())));
        lines.add(this.line("Right", ItemNames.withAmount(recipe.getRight())));
        lines.add(this.line("Cost", recipe.getLevelCost() + " levels"));
    }

    private void describeGrindstone(GrindstoneRecipe<ItemStack> recipe, List<Component> lines) {
        lines.add(this.line("Type", "Grindstone"));
        lines.add(this.line("Input", ItemNames.withAmount(recipe.getInput())));
        recipe.getSecondInput().ifPresent(second -> lines.add(this.line("Second input", ItemNames.withAmount(second))));
        lines.add(this.line("Experience", String.valueOf(recipe.getExperience())));
    }

    private void describeSettings(CustomRecipe<ItemStack> recipe, List<Component> lines) {
        lines.add(this.line("Match mode", recipe.getMatchMode().name()));
        if (recipe.shouldReplaceVanilla()) {
            lines.add(this.feature("Replaces vanilla recipes"));
        }

        RecipeBookSettings recipeBook = recipe.getRecipeBook();
        if (recipeBook.hasGroup()) {
            lines.add(this.line("Recipe book group", recipeBook.getGroup()));
        }
        recipeBook.getCategory().ifPresent(category -> lines.add(this.line("Recipe book tab", category.name())));
    }

    private Component line(String label, String value) {
        return this.text(label + ": ", NamedTextColor.GRAY).append(this.text(value, NamedTextColor.WHITE));
    }

    private Component feature(String text) {
        return this.text("✔ " + text, NamedTextColor.GREEN);
    }

    private Component text(String text, NamedTextColor color) {
        return Component.text(text, color).decoration(TextDecoration.ITALIC, false);
    }
}
