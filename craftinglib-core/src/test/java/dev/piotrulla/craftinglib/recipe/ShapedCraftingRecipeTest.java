package dev.piotrulla.craftinglib.recipe;

import dev.piotrulla.craftinglib.item.MatchMode;
import dev.piotrulla.craftinglib.support.TestItem;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static dev.piotrulla.craftinglib.support.TestRecipes.grid;
import static dev.piotrulla.craftinglib.support.TestRecipes.shaped;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShapedCraftingRecipeTest {

    private static final TestItem DIAMOND = TestItem.of("diamond");
    private static final TestItem STICK = TestItem.of("stick");
    private static final TestItem LOG = TestItem.of("log");
    private static final TestItem RESULT = TestItem.of("pickaxe");

    private final ShapedCraftingRecipe<TestItem> pickaxe = shaped("pickaxe")
            .withPattern("DDD", " S ", " S ")
            .withIngredient('D', DIAMOND)
            .withIngredient('S', STICK)
            .withResult(RESULT)
            .build();

    @Test
    void shouldMatchExactLayout() {
        Optional<RecipeMatch<TestItem>> match = this.pickaxe.match(grid(
                DIAMOND, DIAMOND, DIAMOND,
                null, STICK, null,
                null, STICK, null
        ));

        assertTrue(match.isPresent());
        assertEquals(1, match.get().getMaxCrafts());
    }

    @Test
    void shouldRejectExtraItem() {
        assertFalse(this.pickaxe.match(grid(
                DIAMOND, DIAMOND, DIAMOND,
                LOG, STICK, null,
                null, STICK, null
        )).isPresent());
    }

    @Test
    void shouldRejectWrongIngredient() {
        assertFalse(this.pickaxe.match(grid(
                DIAMOND, LOG, DIAMOND,
                null, STICK, null,
                null, STICK, null
        )).isPresent());
    }

    @Test
    void shouldMatchSmallPatternAtAnyPosition() {
        ShapedCraftingRecipe<TestItem> torch = shaped("torch")
                .withPattern("C", "S")
                .withIngredient('C', TestItem.of("coal"))
                .withIngredient('S', STICK)
                .withResult(TestItem.of("torch", 8))
                .build();

        assertTrue(torch.match(grid(
                null, null, null,
                null, null, TestItem.of("coal"),
                null, null, STICK
        )).isPresent());
        assertTrue(torch.match(grid(
                TestItem.of("coal"), null,
                STICK, null
        )).isPresent());
    }

    @Test
    void shouldMatchMirroredLayoutByDefault() {
        ShapedCraftingRecipe<TestItem> axe = this.axe(true);

        assertTrue(axe.match(grid(
                DIAMOND, DIAMOND, null,
                DIAMOND, STICK, null,
                null, STICK, null
        )).isPresent());
        assertTrue(axe.match(grid(
                DIAMOND, DIAMOND, null,
                STICK, DIAMOND, null,
                STICK, null, null
        )).isPresent());
    }

    @Test
    void shouldRejectMirroredLayoutWhenDisabled() {
        ShapedCraftingRecipe<TestItem> axe = this.axe(false);

        assertFalse(axe.match(grid(
                DIAMOND, DIAMOND, null,
                STICK, DIAMOND, null,
                STICK, null, null
        )).isPresent());
    }

    @Test
    void shouldRequireIngredientAmountsAndReportConsumption() {
        ShapedCraftingRecipe<TestItem> compressed = shaped("compressed")
                .withPattern("D")
                .withIngredient('D', TestItem.of("diamond", 3))
                .withResult(TestItem.of("diamond_block"))
                .build();

        assertFalse(compressed.match(grid(TestItem.of("diamond", 2), null, null, null)).isPresent());

        RecipeMatch<TestItem> match = compressed.match(grid(null, TestItem.of("diamond", 10), null, null)).orElseThrow();
        assertEquals(3, match.getConsumption(1));
        assertEquals(0, match.getConsumption(0));
        assertEquals(3, match.getMaxCrafts());
    }

    @Test
    void shouldRespectMatchMode() {
        TestItem namedDiamond = DIAMOND.named("Magic");

        ShapedCraftingRecipe<TestItem> exact = shaped("exact")
                .withPattern("D")
                .withIngredient('D', namedDiamond)
                .withResult(RESULT)
                .build();
        ShapedCraftingRecipe<TestItem> typeOnly = shaped("type-only")
                .withPattern("D")
                .withIngredient('D', namedDiamond)
                .withResult(RESULT)
                .withMatchMode(MatchMode.TYPE)
                .build();

        assertFalse(exact.match(grid(DIAMOND, null, null, null)).isPresent());
        assertTrue(exact.match(grid(namedDiamond, null, null, null)).isPresent());
        assertTrue(typeOnly.match(grid(DIAMOND, null, null, null)).isPresent());
    }

    @Test
    void shouldNotFitPlayerInventoryWhenPatternIsBig() {
        assertFalse(this.pickaxe.fitsGrid(2));
        assertTrue(this.pickaxe.fitsGrid(3));
    }

    @Test
    void shouldReturnDefensiveCopiesOfIngredients() {
        this.pickaxe.getIngredients().clear();

        assertEquals(2, this.pickaxe.getIngredients().size());
    }

    private ShapedCraftingRecipe<TestItem> axe(boolean mirrored) {
        return shaped("axe")
                .withPattern("DD", "DS", " S")
                .withIngredient('D', DIAMOND)
                .withIngredient('S', STICK)
                .withResult(TestItem.of("axe"))
                .withMirroring(mirrored)
                .build();
    }
}
