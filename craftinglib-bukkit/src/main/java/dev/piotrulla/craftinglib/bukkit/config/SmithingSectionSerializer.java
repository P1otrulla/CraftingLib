package dev.piotrulla.craftinglib.bukkit.config;

import dev.piotrulla.craftinglib.bukkit.BukkitRecipes;
import dev.piotrulla.craftinglib.bukkit.builder.BukkitSmithingRecipeBuilder;
import dev.piotrulla.craftinglib.recipe.SmithingRecipe;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * {@link SmithingRecipe}:
 * <pre>
 * mithril-sword:
 *   template: NETHERITE_UPGRADE_SMITHING_TEMPLATE   # 1.20+: required, before: not allowed
 *   base: DIAMOND_SWORD
 *   addition: {item stack}
 *   result: {item stack}
 *   match-mode: EXACT
 * </pre>
 */
final class SmithingSectionSerializer extends RecipeSectionSerializer<SmithingRecipe<ItemStack>> {

    private static final String TEMPLATE = "template";
    private static final String BASE = "base";
    private static final String ADDITION = "addition";

    @Override
    protected void writeFields(@NotNull SmithingRecipe<ItemStack> recipe, @NotNull ConfigurationSection section) {
        this.setOptionalItem(section, TEMPLATE, recipe.getTemplate());
        section.set(BASE, recipe.getBase());
        section.set(ADDITION, recipe.getAddition());
    }

    @NotNull
    @Override
    protected SmithingRecipe<ItemStack> readFields(@NotNull SectionReader reader) {
        BukkitSmithingRecipeBuilder builder = BukkitRecipes.smithing(reader.getKey())
                .withBase(reader.requireItem(BASE))
                .withAddition(reader.requireItem(ADDITION));

        reader.findItem(TEMPLATE).ifPresent(builder::withTemplate);

        return this.buildWithSettings(builder, reader);
    }
}
