package dev.piotrulla.craftinglib.recipe.builder;

import dev.piotrulla.craftinglib.item.ItemAdapter;
import dev.piotrulla.craftinglib.recipe.smelting.CookingType;
import dev.piotrulla.craftinglib.recipe.smelting.SmeltingRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * <pre>
 * builder.withCookingType(CookingType.BLASTING)
 *     .withInput(rawOre)
 *     .withResult(ingot)
 *     .withExperience(0.7F)
 *     .build();
 * </pre>
 * Cooking time defaults to the vanilla time of the station.
 *
 * @param <T> platform item type
 * @param <B> concrete builder type
 */
public abstract class AbstractSmeltingRecipeBuilder<T, B extends AbstractSmeltingRecipeBuilder<T, B>>
        extends AbstractRecipeBuilder<T, SmeltingRecipe<T>, B> {

    private CookingType cookingType = CookingType.FURNACE;
    private T input;
    private float experience;
    private Integer cookingTime;

    protected AbstractSmeltingRecipeBuilder(@NotNull ItemAdapter<T> itemAdapter, @NotNull String key) {
        super(itemAdapter, key);
    }

    /**
     * Defaults to {@link CookingType#FURNACE}.
     */
    @NotNull
    public B withCookingType(@NotNull CookingType cookingType) {
        this.cookingType = Objects.requireNonNull(cookingType, "cookingType");
        return this.self();
    }

    @NotNull
    public B withInput(@NotNull T input) {
        this.input = this.requireItem(input, "Input");
        return this.self();
    }

    @NotNull
    public B withExperience(float experience) {
        this.experience = experience;
        return this.self();
    }

    /**
     * @param cookingTime cooking time in ticks (20 ticks = 1 second)
     */
    @NotNull
    public B withCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
        return this.self();
    }

    @NotNull
    @Override
    public SmeltingRecipe<T> build() {
        int resolvedCookingTime = this.cookingTime != null ? this.cookingTime : this.cookingType.getDefaultCookingTime();

        return new SmeltingRecipe<>(
                this.buildSettings(),
                this.cookingType,
                this.requireSet(this.input, "Input"),
                this.experience,
                resolvedCookingTime
        );
    }
}