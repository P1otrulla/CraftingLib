package dev.piotrulla.craftinglib.support;

import dev.piotrulla.craftinglib.grid.CraftingGrid;
import dev.piotrulla.craftinglib.recipe.builder.AbstractAnvilRecipeBuilder;
import dev.piotrulla.craftinglib.recipe.builder.AbstractBrewingRecipeBuilder;
import dev.piotrulla.craftinglib.recipe.builder.AbstractGrindstoneRecipeBuilder;
import dev.piotrulla.craftinglib.recipe.builder.AbstractShapedRecipeBuilder;
import dev.piotrulla.craftinglib.recipe.builder.AbstractShapelessRecipeBuilder;
import dev.piotrulla.craftinglib.recipe.builder.AbstractSmeltingRecipeBuilder;
import dev.piotrulla.craftinglib.recipe.builder.AbstractSmithingRecipeBuilder;
import dev.piotrulla.craftinglib.recipe.builder.AbstractStonecuttingRecipeBuilder;

import java.util.Arrays;

/**
 * Test platform: concrete builders exactly like a real platform (Bukkit, Nukkit...) would provide them.
 */
public final class TestRecipes {

    private TestRecipes() {
    }

    public static ShapedBuilder shaped(String key) {
        return new ShapedBuilder(key);
    }

    public static ShapelessBuilder shapeless(String key) {
        return new ShapelessBuilder(key);
    }

    public static SmeltingBuilder smelting(String key) {
        return new SmeltingBuilder(key);
    }

    public static BrewingBuilder brewing(String key) {
        return new BrewingBuilder(key);
    }

    public static StonecuttingBuilder stonecutting(String key) {
        return new StonecuttingBuilder(key);
    }

    public static SmithingBuilder smithing(String key) {
        return new SmithingBuilder(key);
    }

    public static AnvilBuilder anvil(String key) {
        return new AnvilBuilder(key);
    }

    public static GrindstoneBuilder grindstone(String key) {
        return new GrindstoneBuilder(key);
    }

    public static CraftingGrid<TestItem> grid(TestItem... slots) {
        return CraftingGrid.of(Arrays.asList(slots));
    }

    public static final class ShapedBuilder extends AbstractShapedRecipeBuilder<TestItem, ShapedBuilder> {

        private ShapedBuilder(String key) {
            super(TestItemAdapter.INSTANCE, key);
        }

        @Override
        protected ShapedBuilder self() {
            return this;
        }
    }

    public static final class ShapelessBuilder extends AbstractShapelessRecipeBuilder<TestItem, ShapelessBuilder> {

        private ShapelessBuilder(String key) {
            super(TestItemAdapter.INSTANCE, key);
        }

        @Override
        protected ShapelessBuilder self() {
            return this;
        }
    }

    public static final class SmeltingBuilder extends AbstractSmeltingRecipeBuilder<TestItem, SmeltingBuilder> {

        private SmeltingBuilder(String key) {
            super(TestItemAdapter.INSTANCE, key);
        }

        @Override
        protected SmeltingBuilder self() {
            return this;
        }
    }

    public static final class BrewingBuilder extends AbstractBrewingRecipeBuilder<TestItem, BrewingBuilder> {

        private BrewingBuilder(String key) {
            super(TestItemAdapter.INSTANCE, key);
        }

        @Override
        protected BrewingBuilder self() {
            return this;
        }
    }

    public static final class StonecuttingBuilder extends AbstractStonecuttingRecipeBuilder<TestItem, StonecuttingBuilder> {

        private StonecuttingBuilder(String key) {
            super(TestItemAdapter.INSTANCE, key);
        }

        @Override
        protected StonecuttingBuilder self() {
            return this;
        }
    }

    public static final class SmithingBuilder extends AbstractSmithingRecipeBuilder<TestItem, SmithingBuilder> {

        private SmithingBuilder(String key) {
            super(TestItemAdapter.INSTANCE, key);
        }

        @Override
        protected SmithingBuilder self() {
            return this;
        }
    }

    public static final class AnvilBuilder extends AbstractAnvilRecipeBuilder<TestItem, AnvilBuilder> {

        private AnvilBuilder(String key) {
            super(TestItemAdapter.INSTANCE, key);
        }

        @Override
        protected AnvilBuilder self() {
            return this;
        }
    }

    public static final class GrindstoneBuilder extends AbstractGrindstoneRecipeBuilder<TestItem, GrindstoneBuilder> {

        private GrindstoneBuilder(String key) {
            super(TestItemAdapter.INSTANCE, key);
        }

        @Override
        protected GrindstoneBuilder self() {
            return this;
        }
    }
}
