package dev.piotrulla.craftinglib.recipe.builder;

import dev.piotrulla.craftinglib.item.ItemAdapter;
import dev.piotrulla.craftinglib.recipe.GrindstoneRecipe;
import org.jetbrains.annotations.NotNull;

/**
 * <pre>
 * builder.withInput(cursedSword)
 *     .withSecondInput(holyWater)
 *     .withResult(cleanSword)
 *     .withExperience(10)
 *     .build();
 * </pre>
 * Second input is optional, experience defaults to {@link GrindstoneRecipe#NO_EXPERIENCE}.
 *
 * @param <T> platform item type
 * @param <B> concrete builder type
 */
public abstract class AbstractGrindstoneRecipeBuilder<T, B extends AbstractGrindstoneRecipeBuilder<T, B>>
        extends AbstractRecipeBuilder<T, GrindstoneRecipe<T>, B> {

    private T input;
    private T secondInput;
    private int experience = GrindstoneRecipe.NO_EXPERIENCE;

    protected AbstractGrindstoneRecipeBuilder(@NotNull ItemAdapter<T> itemAdapter, @NotNull String key) {
        super(itemAdapter, key);
    }

    /**
     * @param input item in any grindstone slot, its amount is consumed
     */
    @NotNull
    public B withInput(@NotNull T input) {
        this.input = this.requireItem(input, "Input");
        return this.self();
    }

    /**
     * @param secondInput item in the other grindstone slot, its amount is consumed
     */
    @NotNull
    public B withSecondInput(@NotNull T secondInput) {
        this.secondInput = this.requireItem(secondInput, "Second input");
        return this.self();
    }

    @NotNull
    public B withExperience(int experience) {
        this.experience = experience;
        return this.self();
    }

    @NotNull
    @Override
    public GrindstoneRecipe<T> build() {
        return new GrindstoneRecipe<>(
                this.buildSettings(),
                this.requireSet(this.input, "Input"),
                this.secondInput,
                this.experience
        );
    }
}
