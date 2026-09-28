package dev.piotrulla.craftinglib.manager;

import dev.piotrulla.craftinglib.recipe.CraftingRecipe;
import dev.piotrulla.craftinglib.recipe.manager.PlatformRecipeRegistrar;
import dev.piotrulla.craftinglib.recipe.manager.SimpleCraftingRecipeManager;
import dev.piotrulla.craftinglib.support.TestItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dev.piotrulla.craftinglib.support.TestRecipes.grid;
import static dev.piotrulla.craftinglib.support.TestRecipes.shaped;
import static dev.piotrulla.craftinglib.support.TestRecipes.shapeless;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleCraftingRecipeManagerTest {

    private static final TestItem STICK = TestItem.of("stick");

    private RecordingRegistrar registrar;
    private SimpleCraftingRecipeManager<TestItem> manager;

    @BeforeEach
    void setUp() {
        this.registrar = new RecordingRegistrar();
        this.manager = new SimpleCraftingRecipeManager<>(this.registrar);
    }

    @Test
    void shouldRegisterInPlatform() {
        CraftingRecipe<TestItem> recipe = this.stickRecipe("sticks", 4);

        this.manager.registerRecipe(recipe);

        assertEquals(Arrays.asList("+sticks"), this.registrar.events);
        assertSame(recipe, this.manager.findRecipe("sticks").orElseThrow());
    }

    @Test
    void shouldReplaceRecipeWithSameKey() {
        this.manager.registerRecipe(this.stickRecipe("sticks", 4));
        CraftingRecipe<TestItem> replacement = this.stickRecipe("sticks", 8);

        this.manager.registerRecipe(replacement);

        assertEquals(Arrays.asList("+sticks", "-sticks", "+sticks"), this.registrar.events);
        assertEquals(1, this.manager.getRecipes().size());
        assertSame(replacement, this.manager.findRecipe("sticks").orElseThrow());
    }

    @Test
    void shouldUnregister() {
        this.manager.registerRecipe(this.stickRecipe("sticks", 4));

        assertTrue(this.manager.unregisterRecipe("sticks"));
        assertFalse(this.manager.unregisterRecipe("sticks"));
        assertFalse(this.manager.findRecipe("sticks").isPresent());
    }

    @Test
    void shouldUnregisterAll() {
        this.manager.registerRecipe(this.stickRecipe("first", 1));
        this.manager.registerRecipe(this.stickRecipe("second", 2));

        this.manager.unregisterAll();

        assertTrue(this.manager.getRecipes().isEmpty());
        assertEquals(Arrays.asList("+first", "+second", "-first", "-second"), this.registrar.events);
    }

    @Test
    void shouldReturnFirstRegisteredMatch() {
        this.manager.registerRecipe(this.stickRecipe("first", 1));
        this.manager.registerRecipe(this.stickRecipe("second", 2));

        String matchedKey = this.manager.findMatch(grid(STICK, null, null, null))
                .orElseThrow()
                .getRecipe()
                .getKey();

        assertEquals("first", matchedKey);
    }

    @Test
    void shouldSkipRecipesThatDoNotFitGrid() {
        this.manager.registerRecipe(shaped("row")
                .withPattern("SSS")
                .withIngredient('S', STICK)
                .withResult(TestItem.of("rail"))
                .build());

        assertFalse(this.manager.findMatch(grid(STICK, STICK, null, null)).isPresent());
        assertTrue(this.manager.findMatch(grid(STICK, STICK, STICK, null, null, null, null, null, null)).isPresent());
    }

    @Test
    void shouldExposeReadOnlySnapshot() {
        this.manager.registerRecipe(this.stickRecipe("sticks", 4));

        assertThrows(UnsupportedOperationException.class, () -> this.manager.getRecipes().clear());
    }

    private CraftingRecipe<TestItem> stickRecipe(String key, int amount) {
        return shapeless(key)
                .withIngredient(STICK)
                .withResult(TestItem.of("torch", amount))
                .build();
    }

    private static final class RecordingRegistrar implements PlatformRecipeRegistrar<CraftingRecipe<TestItem>> {

        private final List<String> events = new ArrayList<>();

        @Override
        public void register(CraftingRecipe<TestItem> recipe) {
            this.events.add("+" + recipe.getKey());
        }

        @Override
        public void unregister(CraftingRecipe<TestItem> recipe) {
            this.events.add("-" + recipe.getKey());
        }
    }
}
