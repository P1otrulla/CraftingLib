package dev.piotrulla.craftinglib.bukkit.registry;

import dev.piotrulla.craftinglib.CraftingException;
import dev.piotrulla.craftinglib.bukkit.item.BukkitItemAdapter;
import dev.piotrulla.craftinglib.bukkit.item.RecipeChoices;
import dev.piotrulla.craftinglib.bukkit.version.VersionDetector;
import dev.piotrulla.craftinglib.item.MatchMode;
import dev.piotrulla.craftinglib.recipe.BrewingRecipe;
import dev.piotrulla.craftinglib.recipe.manager.PlatformRecipeRegistrar;
import io.papermc.paper.potion.PotionMix;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionBrewer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Registers brewing recipes as Paper {@link PotionMix}es.
 * Paper-only: create it only when {@code VersionDetector.isPotionMixSupported()} returns true.
 * {@link MatchMode#PERSISTENT_DATA} uses a predicate choice, available on newer Paper builds.
 */
public final class PaperBrewingRecipeRegistrar implements PlatformRecipeRegistrar<BrewingRecipe<ItemStack>> {

    private static final String PREDICATE_UNSUPPORTED = "Brewing recipes with " + MatchMode.PERSISTENT_DATA
            + " match mode require Paper with PotionMix#createPredicateChoice";

    private final Plugin plugin;
    private final BukkitItemAdapter itemAdapter = BukkitItemAdapter.INSTANCE;
    private final boolean predicateChoiceSupported = VersionDetector.isPotionMixPredicateSupported();

    public PaperBrewingRecipeRegistrar(@NotNull Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    @Override
    public void register(@NotNull BrewingRecipe<ItemStack> recipe) {
        Objects.requireNonNull(recipe, "recipe");

        NamespacedKey key = this.createKey(recipe);
        MatchMode matchMode = recipe.getMatchMode();
        PotionMix potionMix = new PotionMix(
                key,
                recipe.getResult(),
                this.choiceOf(recipe.getInput(), matchMode),
                this.choiceOf(recipe.getIngredient(), matchMode)
        );

        this.potionBrewer().removePotionMix(key);
        this.potionBrewer().addPotionMix(potionMix);
    }

    @Override
    public void unregister(@NotNull BrewingRecipe<ItemStack> recipe) {
        Objects.requireNonNull(recipe, "recipe");
        this.potionBrewer().removePotionMix(this.createKey(recipe));
    }

    private RecipeChoice choiceOf(ItemStack required, MatchMode matchMode) {
        if (matchMode != MatchMode.PERSISTENT_DATA) {
            return RecipeChoices.of(required, matchMode);
        }
        if (!this.predicateChoiceSupported) {
            throw new CraftingException(PREDICATE_UNSUPPORTED);
        }

        return PotionMix.createPredicateChoice(candidate -> !this.itemAdapter.isEmpty(candidate)
                && this.itemAdapter.isSimilar(required, candidate, matchMode));
    }

    private NamespacedKey createKey(BrewingRecipe<ItemStack> recipe) {
        return new NamespacedKey(this.plugin, recipe.getKey());
    }

    private PotionBrewer potionBrewer() {
        return this.plugin.getServer().getPotionBrewer();
    }
}
