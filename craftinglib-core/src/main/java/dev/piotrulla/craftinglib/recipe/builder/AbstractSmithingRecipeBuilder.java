package dev.piotrulla.craftinglib.recipe.builder;

import dev.piotrulla.craftinglib.item.ItemAdapter;
import dev.piotrulla.craftinglib.recipe.SmithingRecipe;
import org.jetbrains.annotations.NotNull;

/**
 * <pre>
 * builder.withTemplate(upgradeTemplate)
 *     .withBase(diamondSword)
 *     .withAddition(customIngot)
 *     .withResult(customSword)
 *     .build();
 * </pre>
 * Template is required on 1.20+ and must be skipped on 1.16-1.19.
 *
 * @param <T> platform item type
 * @param <B> concrete builder type
 */
public abstract class AbstractSmithingRecipeBuilder<T, B extends AbstractSmithingRecipeBuilder<T, B>>
        extends AbstractRecipeBuilder<T, SmithingRecipe<T>, B> {

    private T template;
    private T base;
    private T addition;

    protected AbstractSmithingRecipeBuilder(@NotNull ItemAdapter<T> itemAdapter, @NotNull String key) {
        super(itemAdapter, key);
    }

    @NotNull
    public B withTemplate(@NotNull T template) {
        this.template = this.requireItem(template, "Template");
        return this.self();
    }

    @NotNull
    public B withBase(@NotNull T base) {
        this.base = this.requireItem(base, "Base");
        return this.self();
    }

    @NotNull
    public B withAddition(@NotNull T addition) {
        this.addition = this.requireItem(addition, "Addition");
        return this.self();
    }

    @NotNull
    @Override
    public SmithingRecipe<T> build() {
        return new SmithingRecipe<>(
                this.buildSettings(),
                this.template,
                this.requireSet(this.base, "Base"),
                this.requireSet(this.addition, "Addition")
        );
    }
}