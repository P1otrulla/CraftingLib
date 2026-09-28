package dev.piotrulla.craftinglib.recipe;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Smithing table recipe: template + base + addition turn into the result (like diamond to netherite).
 * The template is required on Minecraft 1.20+ and must be absent on 1.16-1.19.
 *
 * @param <T> platform item type
 */
public final class SmithingRecipe<T> extends AbstractRecipe<T> {

    private final T template;
    private final T base;
    private final T addition;

    public SmithingRecipe(@NotNull RecipeSettings<T> settings, @Nullable T template, @NotNull T base, @NotNull T addition) {
        super(settings);
        this.rejectRecipeBook();
        this.template = template == null ? null : this.requireSingleItem(template, "Template");
        this.base = this.requireSingleItem(base, "Base");
        this.addition = this.requireSingleItem(addition, "Addition");
    }

    /**
     * @return copy of the template, empty for recipes without one
     */
    @NotNull
    public Optional<T> getTemplate() {
        return this.template == null ? Optional.empty() : Optional.of(this.itemAdapter.copy(this.template));
    }

    /**
     * @return copy of the item being upgraded
     */
    @NotNull
    public T getBase() {
        return this.itemAdapter.copy(this.base);
    }

    /**
     * @return copy of the upgrade material
     */
    @NotNull
    public T getAddition() {
        return this.itemAdapter.copy(this.addition);
    }

    public boolean matches(@Nullable T template, @Nullable T base, @Nullable T addition) {
        boolean templateMatches = this.template == null
                ? this.itemAdapter.isEmpty(template)
                : this.matchesIngredient(this.template, template);

        return templateMatches
                && this.matchesIngredient(this.base, base)
                && this.matchesIngredient(this.addition, addition);
    }
}