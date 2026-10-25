package dev.piotrulla.craftinglib.bukkit.config;

import dev.piotrulla.craftinglib.recipe.AnvilRecipe;
import dev.piotrulla.craftinglib.recipe.BrewingRecipe;
import dev.piotrulla.craftinglib.recipe.CraftingRecipe;
import dev.piotrulla.craftinglib.recipe.GrindstoneRecipe;
import dev.piotrulla.craftinglib.recipe.SmithingRecipe;
import dev.piotrulla.craftinglib.recipe.StonecuttingRecipe;
import dev.piotrulla.craftinglib.recipe.smelting.SmeltingRecipe;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Serializers that store recipes in Bukkit's own YAML configs ({@code FileConfiguration}, any {@code ConfigurationSection}),
 * with no extra dependency. Works on 1.8+.
 * <pre>{@code
 * FileConfiguration config = this.getConfig();
 * List<CraftingRecipe<ItemStack>> recipes = BukkitRecipeSerializers.crafting().readAll(config.getConfigurationSection("crafting"));
 * BukkitRecipeSerializers.crafting().writeAll(config.createSection("crafting"), recipes);
 * }</pre>
 * The format matches the okaeri-configs serializers, except that the recipe key is the section name.
 */
public final class BukkitRecipeSerializers {

    private static final RecipeSectionSerializer<CraftingRecipe<ItemStack>> CRAFTING = new CraftingSectionSerializer();
    private static final RecipeSectionSerializer<SmeltingRecipe<ItemStack>> SMELTING = new SmeltingSectionSerializer();
    private static final RecipeSectionSerializer<BrewingRecipe<ItemStack>> BREWING = new BrewingSectionSerializer();
    private static final RecipeSectionSerializer<StonecuttingRecipe<ItemStack>> STONECUTTING = new StonecuttingSectionSerializer();
    private static final RecipeSectionSerializer<SmithingRecipe<ItemStack>> SMITHING = new SmithingSectionSerializer();
    private static final RecipeSectionSerializer<AnvilRecipe<ItemStack>> ANVIL = new AnvilSectionSerializer();
    private static final RecipeSectionSerializer<GrindstoneRecipe<ItemStack>> GRINDSTONE = new GrindstoneSectionSerializer();

    private BukkitRecipeSerializers() {
    }

    /**
     * Shaped and shapeless crafting recipes.
     */
    @NotNull
    public static RecipeSectionSerializer<CraftingRecipe<ItemStack>> crafting() {
        return CRAFTING;
    }

    /**
     * Furnace, blast furnace, smoker and campfire recipes.
     */
    @NotNull
    public static RecipeSectionSerializer<SmeltingRecipe<ItemStack>> smelting() {
        return SMELTING;
    }

    @NotNull
    public static RecipeSectionSerializer<BrewingRecipe<ItemStack>> brewing() {
        return BREWING;
    }

    @NotNull
    public static RecipeSectionSerializer<StonecuttingRecipe<ItemStack>> stonecutting() {
        return STONECUTTING;
    }

    @NotNull
    public static RecipeSectionSerializer<SmithingRecipe<ItemStack>> smithing() {
        return SMITHING;
    }

    @NotNull
    public static RecipeSectionSerializer<AnvilRecipe<ItemStack>> anvil() {
        return ANVIL;
    }

    @NotNull
    public static RecipeSectionSerializer<GrindstoneRecipe<ItemStack>> grindstone() {
        return GRINDSTONE;
    }
}
