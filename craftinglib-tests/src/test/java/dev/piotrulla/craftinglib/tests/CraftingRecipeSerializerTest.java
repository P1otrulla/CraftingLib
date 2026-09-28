package dev.piotrulla.craftinglib.tests;

import be.seeseemelk.mockbukkit.MockBukkit;
import dev.piotrulla.craftinglib.bukkit.BukkitRecipes;
import dev.piotrulla.craftinglib.item.MatchMode;
import dev.piotrulla.craftinglib.okaeri.CraftingLibSerdesPack;
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
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CraftingRecipeSerializerTest {

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
        RecipesConfig config = this.createConfig();
        config.getRecipes().add(BukkitRecipes.shaped("super-pickaxe")
                .withPattern("DDD", " S ", " S ")
                .withIngredient('D', Material.DIAMOND_BLOCK)
                .withIngredient('S', Material.STICK)
                .withResult(Material.DIAMOND_PICKAXE)
                .withMirroring(false)
                .replaceVanillaRecipes(true)
                .withRecipeBookGroup("pickaxes")
                .withRecipeBookCategory(RecipeBookCategory.EQUIPMENT)
                .discoverable(false)
                .build());
        config.getRecipes().add(BukkitRecipes.shapeless("quick-bread")
                .withIngredient(Material.WHEAT, 2)
                .withIngredient(Material.WHEAT)
                .withResult(Material.BREAD, 3)
                .withMatchMode(MatchMode.TYPE)
                .build());

        String yaml = config.saveToString();
        RecipesConfig loaded = this.createConfig();
        loaded.load(yaml);

        assertEquals(2, loaded.getRecipes().size());

        ShapedCraftingRecipe<ItemStack> shaped = (ShapedCraftingRecipe<ItemStack>) loaded.getRecipes().get(0);
        assertEquals("super-pickaxe", shaped.getKey());
        assertEquals(Arrays.asList("DDD", " S ", " S "), shaped.getPattern().getRows());
        assertEquals(Material.DIAMOND_BLOCK, shaped.getIngredients().get('D').getType());
        assertFalse(shaped.isMirrored());
        assertTrue(shaped.shouldReplaceVanilla());
        assertEquals(MatchMode.EXACT, shaped.getMatchMode());
        assertEquals(new RecipeBookSettings(false, "pickaxes", RecipeBookCategory.EQUIPMENT), shaped.getRecipeBook());

        ShapelessCraftingRecipe<ItemStack> shapeless = (ShapelessCraftingRecipe<ItemStack>) loaded.getRecipes().get(1);
        assertEquals("quick-bread", shapeless.getKey());
        assertEquals(2, shapeless.getIngredients().get(0).getAmount());
        assertEquals(new ItemStack(Material.BREAD, 3), shapeless.getResult());
        assertEquals(MatchMode.TYPE, shapeless.getMatchMode());
        assertEquals(RecipeBookSettings.DEFAULT, shapeless.getRecipeBook());
    }

    @Test
    void shouldRoundTripStationRecipes() {
        StationsConfig config = this.createConfig(StationsConfig.class);
        config.smelting = BukkitRecipes.smelting("fast-bread").withCookingType(CookingType.SMOKING)
                .withInput(Material.WHEAT).withResult(Material.BREAD).withExperience(0.5F).withCookingTime(40)
                .withRecipeBookCategory(RecipeBookCategory.FOOD).build();
        config.brewing = BukkitRecipes.brewing("coal-potion").withInput(new ItemStack(Material.EMERALD)).withIngredient(Material.COAL)
                .withResult(Material.DIAMOND).build();
        config.stonecutting = BukkitRecipes.stonecutting("cheap-bricks").withInput(Material.STONE)
                .withResult(Material.STONE_BRICKS, 2).withRecipeBookGroup("bricks").build();
        config.smithing = BukkitRecipes.smithing("netherite-sword").withTemplate(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
                .withBase(Material.DIAMOND_SWORD).withAddition(Material.DIAMOND_BLOCK).withResult(Material.NETHERITE_SWORD).build();
        config.anvil = BukkitRecipes.anvil("sharpen").withLeft(Material.DIAMOND_SWORD).withRight(Material.FLINT, 4)
                .withResult(Material.DIAMOND_SWORD).withLevelCost(5).withMatchMode(MatchMode.TYPE).build();
        config.grindstone = BukkitRecipes.grindstone("scrap").withInput(Material.DIAMOND_SWORD)
                .withResult(Material.DIAMOND).withExperience(7).build();

        StationsConfig loaded = this.createConfig(StationsConfig.class);
        loaded.load(config.saveToString());

        assertEquals(CookingType.SMOKING, loaded.smelting.getCookingType());
        assertEquals(40, loaded.smelting.getCookingTime());
        assertEquals(0.5F, loaded.smelting.getExperience(), 0.0001F);
        assertEquals(RecipeBookCategory.FOOD, loaded.smelting.getRecipeBook().getCategory().orElse(null));
        assertEquals(Material.COAL, loaded.brewing.getIngredient().getType());
        assertEquals("bricks", loaded.stonecutting.getRecipeBook().getGroup());
        assertEquals(new ItemStack(Material.STONE_BRICKS, 2), loaded.stonecutting.getResult());
        assertTrue(loaded.smithing.getTemplate().isPresent());
        assertEquals(Material.DIAMOND_BLOCK, loaded.smithing.getAddition().getType());
        assertEquals(4, loaded.anvil.getRight().getAmount());
        assertEquals(5, loaded.anvil.getLevelCost());
        assertEquals(MatchMode.TYPE, loaded.anvil.getMatchMode());
        assertFalse(loaded.grindstone.getSecondInput().isPresent());
        assertEquals(7, loaded.grindstone.getExperience());
    }

    private RecipesConfig createConfig() {
        return this.createConfig(RecipesConfig.class);
    }

    private <C extends OkaeriConfig> C createConfig(Class<C> configClass) {
        return ConfigManager.create(configClass, it -> it.configure(opt ->
                opt.configurer(new YamlBukkitConfigurer(), new SerdesBukkit(), new CraftingLibSerdesPack())));
    }

    public static final class StationsConfig extends OkaeriConfig {

        private SmeltingRecipe<ItemStack> smelting;
        private BrewingRecipe<ItemStack> brewing;
        private StonecuttingRecipe<ItemStack> stonecutting;
        private SmithingRecipe<ItemStack> smithing;
        private AnvilRecipe<ItemStack> anvil;
        private GrindstoneRecipe<ItemStack> grindstone;
    }

    public static final class RecipesConfig extends OkaeriConfig {

        private List<CraftingRecipe<ItemStack>> recipes = new ArrayList<>();

        public List<CraftingRecipe<ItemStack>> getRecipes() {
            return this.recipes;
        }
    }
}
