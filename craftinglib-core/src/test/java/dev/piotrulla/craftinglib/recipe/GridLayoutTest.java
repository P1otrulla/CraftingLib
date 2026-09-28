package dev.piotrulla.craftinglib.recipe;

import dev.piotrulla.craftinglib.grid.CraftingGrid;
import dev.piotrulla.craftinglib.item.MatchMode;
import dev.piotrulla.craftinglib.support.TestItem;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static dev.piotrulla.craftinglib.support.TestRecipes.grid;
import static dev.piotrulla.craftinglib.support.TestRecipes.shaped;
import static dev.piotrulla.craftinglib.support.TestRecipes.shapeless;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GridLayoutTest {

    private static final TestItem STICK = TestItem.of("stick");
    private static final TestItem DIAMOND = TestItem.of("diamond", 2);
    private static final TestItem BLADE = TestItem.of("sword").tagged("blade");

    @Test
    void shouldPlaceShapedRecipeFromTopLeftCorner() {
        CraftingRecipe<TestItem> recipe = shaped("sword").withPattern(" D", " D", " S")
                .withIngredient('D', DIAMOND).withIngredient('S', STICK).withResult(TestItem.of("sword")).build();

        Map<Integer, TestItem> layout = recipe.getGridLayout(CraftingGrid.WORKBENCH_SIZE);

        assertEquals(3, layout.size());
        assertEquals(DIAMOND, layout.get(0));
        assertEquals(DIAMOND, layout.get(3));
        assertEquals(STICK, layout.get(6));
    }

    @Test
    void shouldPlaceShapelessRecipeInFirstSlots() {
        CraftingRecipe<TestItem> recipe = shapeless("mix").withIngredient(STICK).withIngredient(DIAMOND)
                .withResult(TestItem.of("wand")).build();

        Map<Integer, TestItem> layout = recipe.getGridLayout(CraftingGrid.INVENTORY_SIZE);

        assertEquals(STICK, layout.get(0));
        assertEquals(DIAMOND, layout.get(1));
    }

    @Test
    void shouldProduceLayoutMatchedByRecipe() {
        CraftingRecipe<TestItem> recipe = shaped("stairs").withPattern("S  ", "SS ", "SSS")
                .withIngredient('S', STICK).withResult(TestItem.of("stairs")).build();

        Map<Integer, TestItem> layout = recipe.getGridLayout(CraftingGrid.WORKBENCH_SIZE);
        TestItem[] slots = new TestItem[CraftingGrid.WORKBENCH_SIZE * CraftingGrid.WORKBENCH_SIZE];
        layout.forEach((slot, item) -> slots[slot] = item);

        assertTrue(recipe.match(grid(slots)).isPresent());
    }

    @Test
    void shouldRejectLayoutForTooSmallGrid() {
        CraftingRecipe<TestItem> recipe = shaped("big").withPattern("SSS").withIngredient('S', STICK)
                .withResult(TestItem.of("rail")).build();

        assertThrows(IllegalArgumentException.class, () -> recipe.getGridLayout(CraftingGrid.INVENTORY_SIZE));
    }

    @Test
    void shouldMatchPersistentDataIgnoringDisplayName() {
        CraftingRecipe<TestItem> recipe = shapeless("reforge").withIngredient(BLADE).withResult(TestItem.of("sword"))
                .withMatchMode(MatchMode.PERSISTENT_DATA).build();

        assertTrue(recipe.match(grid(BLADE.named("Renamed"), null, null, null)).isPresent());
        assertFalse(recipe.match(grid(TestItem.of("sword"), null, null, null)).isPresent());
        assertFalse(recipe.match(grid(BLADE.tagged("other"), null, null, null)).isPresent());
    }
}
