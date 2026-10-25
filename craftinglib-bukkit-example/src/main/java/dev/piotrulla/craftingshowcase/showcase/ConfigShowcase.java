package dev.piotrulla.craftingshowcase.showcase;

import dev.piotrulla.craftinglib.bukkit.BukkitCraftingLib;
import dev.piotrulla.craftinglib.okaeri.CraftingLibSerdesPack;
import dev.piotrulla.craftingshowcase.config.ShowcaseRecipesConfig;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * Recipes loaded from {@code plugins/CraftingLibShowcase/recipes.yml} (okaeri-configs).
 */
public final class ConfigShowcase extends AbstractRecipeShowcase {

    private static final String FILE_NAME = "recipes.yml";
    private static final boolean UPDATE_FILE = true;

    private final File dataFolder;

    public ConfigShowcase(@NotNull File dataFolder) {
        super("config", "Config (recipes.yml)", "Recipes loaded from YAML with okaeri-configs", Material.WRITABLE_BOOK);
        this.dataFolder = Objects.requireNonNull(dataFolder, "dataFolder");
    }

    @Override
    public void register(@NotNull BukkitCraftingLib craftingLib) {
        ShowcaseRecipesConfig config = this.loadConfig();

        config.getCrafting().forEach(recipe -> this.registerRecipe(craftingLib.getRecipeManager(), recipe));
        config.getSmelting().forEach(recipe -> this.registerRecipe(craftingLib.getSmeltingManager(), recipe));
    }

    @NotNull
    @Override
    public List<ItemStack> getKit() {
        return List.of(
                new ItemStack(Material.LEATHER, KitAmounts.MEDIUM),
                new ItemStack(Material.GRAVEL, KitAmounts.MEDIUM),
                new ItemStack(Material.COAL, KitAmounts.SMALL)
        );
    }

    private ShowcaseRecipesConfig loadConfig() {
        return ConfigManager.create(ShowcaseRecipesConfig.class, config -> {
            config.configure(options -> {
                options.configurer(new YamlBukkitConfigurer(), new SerdesBukkit(), new CraftingLibSerdesPack());
                options.bindFile(new File(this.dataFolder, FILE_NAME));
            });
            config.saveDefaults();
            config.load(UPDATE_FILE);
        });
    }
}
