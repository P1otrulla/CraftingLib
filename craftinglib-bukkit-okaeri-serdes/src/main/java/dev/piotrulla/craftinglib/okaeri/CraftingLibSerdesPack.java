package dev.piotrulla.craftinglib.okaeri;

import eu.okaeri.configs.serdes.OkaeriSerdes;
import eu.okaeri.configs.serdes.SerdesRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * <pre>
 * ConfigManager.create(RecipesConfig.class, it -> {
 *     it.configure(opt -> {
 *         opt.configurer(new YamlBukkitConfigurer(), new SerdesBukkit(), new CraftingLibSerdesPack());
 *         opt.bindFile(new File(this.getDataFolder(), "recipes.yml"));
 *     });
 *     it.saveDefaults();
 *     it.load(true);
 * });
 * </pre>
 * Registers serializers of every recipe type.
 */
public final class CraftingLibSerdesPack implements OkaeriSerdes {

    @Override
    public void register(@NotNull SerdesRegistry registry) {
        registry.add(
                new CraftingRecipeSerializer(),
                new SmeltingRecipeSerializer(),
                new BrewingRecipeSerializer(),
                new StonecuttingRecipeSerializer(),
                new SmithingRecipeSerializer(),
                new AnvilRecipeSerializer(),
                new GrindstoneRecipeSerializer()
        );
    }
}
