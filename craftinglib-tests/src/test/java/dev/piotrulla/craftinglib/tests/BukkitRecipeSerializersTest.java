package dev.piotrulla.craftinglib.tests;

import be.seeseemelk.mockbukkit.MockBukkit;
import dev.piotrulla.craftinglib.CraftingException;
import dev.piotrulla.craftinglib.bukkit.BukkitRecipes;
import dev.piotrulla.craftinglib.bukkit.config.BukkitRecipeSerializers;
import dev.piotrulla.craftinglib.item.MatchMode;
import dev.piotrulla.craftinglib.recipe.AnvilRecipe;
import dev.piotrulla.craftinglib.recipe.BrewingRecipe;
import dev.piotrulla.craftinglib.recipe.CraftingRecipe;
import dev.piotrulla.craftinglib.recipe.GrindstoneRecipe;
import dev.piotrulla.craftinglib.recipe.ShapedCraftingRecipe;
import dev.piotrulla.craftinglib.recipe.ShapelessCraftingRecipe;
import dev.piotrulla.craftinglib.recipe.SmithingRecipe;
import dev.piotrulla.craftinglib.recipe.StonecuttingRecipe;
import dev.piotrulla.craftinglib.recipe.book.RecipeBookCategory;
import dev.piotrulla.craftinglib.recipe.book.RecipeBookSettings;
import dev.piotrulla.craftinglib.recipe.smelting.CookingType;
import dev.piotrulla.craftinglib.recipe.smelting.SmeltingRecipe;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Round trips stay in memory: MockBukkit cannot deserialize item stacks from YAML text
 * ({@code UnsafeValues#getMaterial} is unimplemented). Hand-written configs use material names, which it can read.
 */
class BukkitRecipeSerializersTest {

    private static final String CRAFTING = "crafting";
    private static final String STATIONS = "stations";

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void shouldRoundTripShapedAndShapelessRecipes() {
        YamlConfiguration config = new YamlConfiguration();
        BukkitRecipeSerializers.crafting().writeAll(config.createSection(CRAFTING), Arrays.asList(
                BukkitRecipes.shaped("super-pickaxe")
                        .withPattern("DDD", " S ", " S ")
                        .withIngredient('D', Material.DIAMOND_BLOCK, 3)
                        .withIngredient('S', Material.STICK)
                        .withResult(Material.DIAMOND_PICKAXE)
                        .withMirroring(false)
                        .replaceVanillaRecipes(true)
                        .withRecipeBookGroup("pickaxes")
                        .withRecipeBookCategory(RecipeBookCategory.EQUIPMENT)
                        .discoverable(false)
                        .build(),
                BukkitRecipes.shapeless("quick-bread")
                        .withIngredient(Material.WHEAT, 2)
                        .withIngredient(Material.WHEAT)
                        .withResult(Material.BREAD, 3)
                        .withMatchMode(MatchMode.TYPE)
                        .build()
        ));

        List<CraftingRecipe<ItemStack>> recipes = BukkitRecipeSerializers.crafting().readAll(config.getConfigurationSection(CRAFTING));

        assertEquals(2, recipes.size());

        ShapedCraftingRecipe<ItemStack> shaped = (ShapedCraftingRecipe<ItemStack>) recipes.get(0);
        assertEquals("super-pickaxe", shaped.getKey());
        assertEquals(Arrays.asList("DDD", " S ", " S "), shaped.getPattern().getRows());
        assertEquals(new ItemStack(Material.DIAMOND_BLOCK, 3), shaped.getIngredients().get('D'));
        assertFalse(shaped.isMirrored());
        assertTrue(shaped.shouldReplaceVanilla());
        assertEquals(MatchMode.EXACT, shaped.getMatchMode());
        assertEquals(new RecipeBookSettings(false, "pickaxes", RecipeBookCategory.EQUIPMENT), shaped.getRecipeBook());

        ShapelessCraftingRecipe<ItemStack> shapeless = (ShapelessCraftingRecipe<ItemStack>) recipes.get(1);
        assertEquals("quick-bread", shapeless.getKey());
        assertEquals(2, shapeless.getIngredients().get(0).getAmount());
        assertEquals(new ItemStack(Material.BREAD, 3), shapeless.getResult());
        assertEquals(MatchMode.TYPE, shapeless.getMatchMode());
        assertEquals(RecipeBookSettings.DEFAULT, shapeless.getRecipeBook());
    }

    @Test
    void shouldRoundTripStationRecipes() {
        YamlConfiguration config = new YamlConfiguration();
        BukkitRecipeSerializers.smelting().write(config.createSection(STATIONS), BukkitRecipes.smelting("fast-bread")
                .withCookingType(CookingType.SMOKING).withInput(Material.WHEAT).withResult(Material.BREAD)
                .withExperience(0.7F).withCookingTime(40).withRecipeBookCategory(RecipeBookCategory.FOOD).build());
        BukkitRecipeSerializers.brewing().write(config.getConfigurationSection(STATIONS), BukkitRecipes.brewing("coal-potion")
                .withInput(new ItemStack(Material.EMERALD)).withIngredient(Material.COAL).withResult(Material.DIAMOND).build());
        BukkitRecipeSerializers.stonecutting().write(config.getConfigurationSection(STATIONS), BukkitRecipes.stonecutting("cheap-bricks")
                .withInput(Material.STONE).withResult(Material.STONE_BRICKS, 2).withRecipeBookGroup("bricks").build());
        BukkitRecipeSerializers.smithing().write(config.getConfigurationSection(STATIONS), BukkitRecipes.smithing("netherite-sword")
                .withTemplate(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE).withBase(Material.DIAMOND_SWORD)
                .withAddition(Material.DIAMOND_BLOCK).withResult(Material.NETHERITE_SWORD).build());
        BukkitRecipeSerializers.anvil().write(config.getConfigurationSection(STATIONS), BukkitRecipes.anvil("sharpen")
                .withLeft(Material.DIAMOND_SWORD).withRight(Material.FLINT, 4).withResult(Material.DIAMOND_SWORD)
                .withLevelCost(5).withMatchMode(MatchMode.TYPE).build());
        BukkitRecipeSerializers.grindstone().write(config.getConfigurationSection(STATIONS), BukkitRecipes.grindstone("scrap")
                .withInput(Material.DIAMOND_SWORD).withSecondInput(Material.IRON_SWORD).withResult(Material.DIAMOND)
                .withExperience(7).build());

        String yaml = config.saveToString();
        assertTrue(yaml.contains("experience: 0.7"), yaml);
        assertTrue(yaml.contains("cooking-type: SMOKING"), yaml);

        SmeltingRecipe<ItemStack> smelting = BukkitRecipeSerializers.smelting().read(this.station(config, "fast-bread"));
        assertEquals(CookingType.SMOKING, smelting.getCookingType());
        assertEquals(40, smelting.getCookingTime());
        assertEquals(0.7F, smelting.getExperience(), 0.0001F);
        assertEquals(RecipeBookCategory.FOOD, smelting.getRecipeBook().getCategory().orElse(null));

        BrewingRecipe<ItemStack> brewing = BukkitRecipeSerializers.brewing().read(this.station(config, "coal-potion"));
        assertEquals(Material.COAL, brewing.getIngredient().getType());

        StonecuttingRecipe<ItemStack> stonecutting = BukkitRecipeSerializers.stonecutting().read(this.station(config, "cheap-bricks"));
        assertEquals("bricks", stonecutting.getRecipeBook().getGroup());
        assertEquals(new ItemStack(Material.STONE_BRICKS, 2), stonecutting.getResult());

        SmithingRecipe<ItemStack> smithing = BukkitRecipeSerializers.smithing().read(this.station(config, "netherite-sword"));
        assertTrue(smithing.getTemplate().isPresent());
        assertEquals(Material.DIAMOND_BLOCK, smithing.getAddition().getType());

        AnvilRecipe<ItemStack> anvil = BukkitRecipeSerializers.anvil().read(this.station(config, "sharpen"));
        assertEquals(5, anvil.getLevelCost());
        assertEquals(4, anvil.getRight().getAmount());
        assertEquals(MatchMode.TYPE, anvil.getMatchMode());

        GrindstoneRecipe<ItemStack> grindstone = BukkitRecipeSerializers.grindstone().read(this.station(config, "scrap"));
        assertEquals(7, grindstone.getExperience());
        assertEquals(Material.IRON_SWORD, grindstone.getSecondInput().map(ItemStack::getType).orElse(null));
    }

    @Test
    void shouldReadMaterialNamesInHandWrittenConfig() throws InvalidConfigurationException {
        YamlConfiguration config = new YamlConfiguration();
        config.loadFromString(String.join("\n",
                "crafting:",
                "  ruby-block:",
                "    type: shaped",
                "    pattern: ['RR', 'RR']",
                "    ingredients:",
                "      R: emerald",
                "    result: EMERALD_BLOCK",
                "  wheat-mix:",
                "    type: SHAPELESS",
                "    ingredients: [WHEAT, WHEAT]",
                "    result: BREAD"
        ));

        List<CraftingRecipe<ItemStack>> recipes = BukkitRecipeSerializers.crafting().readAll(config.getConfigurationSection(CRAFTING));

        ShapedCraftingRecipe<ItemStack> shaped = (ShapedCraftingRecipe<ItemStack>) recipes.get(0);
        assertEquals(new ItemStack(Material.EMERALD), shaped.getIngredients().get('R'));
        assertEquals(new ItemStack(Material.EMERALD_BLOCK), shaped.getResult());
        assertEquals(MatchMode.EXACT, shaped.getMatchMode());

        ShapelessCraftingRecipe<ItemStack> shapeless = (ShapelessCraftingRecipe<ItemStack>) recipes.get(1);
        assertEquals(2, shapeless.getIngredients().size());
    }

    @Test
    void shouldFailWithRecipeKeyAndFieldInMessage() throws InvalidConfigurationException {
        YamlConfiguration config = new YamlConfiguration();
        config.loadFromString(String.join("\n",
                "stations:",
                "  broken-anvil:",
                "    left: IRON_SWORD",
                "    right: NOT_A_MATERIAL",
                "    result: DIAMOND",
                "  bad-cost:",
                "    left: IRON_SWORD",
                "    right: FLINT",
                "    level-cost: 2.5",
                "    result: DIAMOND",
                "  no-result:",
                "    input: STONE"
        ));

        CraftingException unknownMaterial = assertThrows(CraftingException.class,
                () -> BukkitRecipeSerializers.anvil().read(this.station(config, "broken-anvil")));
        assertTrue(unknownMaterial.getMessage().contains("broken-anvil"), unknownMaterial.getMessage());
        assertTrue(unknownMaterial.getMessage().contains("NOT_A_MATERIAL"), unknownMaterial.getMessage());

        CraftingException fractionalCost = assertThrows(CraftingException.class,
                () -> BukkitRecipeSerializers.anvil().read(this.station(config, "bad-cost")));
        assertTrue(fractionalCost.getMessage().contains("level-cost"), fractionalCost.getMessage());

        CraftingException missingResult = assertThrows(CraftingException.class,
                () -> BukkitRecipeSerializers.stonecutting().read(this.station(config, "no-result")));
        assertTrue(missingResult.getMessage().contains("'result'"), missingResult.getMessage());
    }

    @Test
    void shouldRejectKeyContainingPathSeparator() {
        YamlConfiguration config = new YamlConfiguration();
        StonecuttingRecipe<ItemStack> recipe = BukkitRecipes.stonecutting("bricks.cheap")
                .withInput(Material.STONE)
                .withResult(Material.STONE_BRICKS)
                .build();

        assertThrows(CraftingException.class,
                () -> BukkitRecipeSerializers.stonecutting().write(config.createSection(STATIONS), recipe));
    }

    private ConfigurationSection station(YamlConfiguration config, String key) {
        return config.getConfigurationSection(STATIONS + "." + key);
    }
}
