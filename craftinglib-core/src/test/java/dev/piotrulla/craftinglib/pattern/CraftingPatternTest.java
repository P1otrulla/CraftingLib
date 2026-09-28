package dev.piotrulla.craftinglib.pattern;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CraftingPatternTest {

    @Test
    void shouldTrimEmptyBorders() {
        CraftingPattern pattern = CraftingPattern.of("   ", " A ", "   ");

        assertEquals(Arrays.asList("A"), pattern.getRows());
        assertEquals(1, pattern.getWidth());
        assertEquals(1, pattern.getHeight());
    }

    @Test
    void shouldKeepInnerSpaces() {
        CraftingPattern pattern = CraftingPattern.of("A A", "   ", "A A");

        assertEquals(Arrays.asList("A A", "   ", "A A"), pattern.getRows());
        assertEquals(3, pattern.getWidth());
        assertEquals(3, pattern.getHeight());
    }

    @Test
    void shouldCollectSymbolsInReadingOrder() {
        CraftingPattern pattern = CraftingPattern.of("DDD", " S ", " S ");

        assertEquals(Arrays.asList('D', 'S'), Arrays.asList(pattern.getSymbols().toArray()));
    }

    @Test
    void shouldKnowWhetherItFitsPlayerInventory() {
        assertTrue(CraftingPattern.of("L ", "  ").fitsGrid(2));
        assertFalse(CraftingPattern.of("DDD", " S ", " S ").fitsGrid(2));
    }

    @Test
    void shouldRejectRaggedRows() {
        assertThrows(IllegalArgumentException.class, () -> CraftingPattern.of("AB", "ABC"));
    }

    @Test
    void shouldRejectTooManyRows() {
        assertThrows(IllegalArgumentException.class, () -> CraftingPattern.of("A", "A", "A", "A"));
    }

    @Test
    void shouldRejectTooLongRow() {
        assertThrows(IllegalArgumentException.class, () -> CraftingPattern.of("ABCD"));
    }

    @Test
    void shouldRejectBlankPattern() {
        assertThrows(IllegalArgumentException.class, () -> CraftingPattern.of("   ", "   "));
    }
}
