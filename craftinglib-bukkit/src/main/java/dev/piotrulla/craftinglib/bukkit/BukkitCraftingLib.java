package dev.piotrulla.craftinglib.bukkit;

import dev.piotrulla.craftinglib.CraftingLib;
import dev.piotrulla.craftinglib.bukkit.book.NoOpRecipeBook;
import dev.piotrulla.craftinglib.bukkit.book.PlayerRecipeBook;
import dev.piotrulla.craftinglib.bukkit.book.RecipeBook;
import dev.piotrulla.craftinglib.bukkit.listener.AnvilListener;
import dev.piotrulla.craftinglib.bukkit.listener.CrafterListener;
import dev.piotrulla.craftinglib.bukkit.listener.CraftingGuard;
import dev.piotrulla.craftinglib.bukkit.listener.CraftingListener;
import dev.piotrulla.craftinglib.bukkit.listener.GrindstoneListener;
import dev.piotrulla.craftinglib.bukkit.listener.RecipeBookAutofillListener;
import dev.piotrulla.craftinglib.bukkit.registry.BukkitRecipeRegistrar;
import dev.piotrulla.craftinglib.bukkit.registry.BukkitSmeltingRecipeRegistrar;
import dev.piotrulla.craftinglib.bukkit.registry.BukkitSmithingRecipeRegistrar;
import dev.piotrulla.craftinglib.bukkit.registry.BukkitStonecuttingRecipeRegistrar;
import dev.piotrulla.craftinglib.bukkit.registry.PaperBrewingRecipeRegistrar;
import dev.piotrulla.craftinglib.bukkit.registry.RegistrarContext;
import dev.piotrulla.craftinglib.bukkit.version.VersionAdapter;
import dev.piotrulla.craftinglib.bukkit.version.VersionDetector;
import dev.piotrulla.craftinglib.recipe.AnvilRecipe;
import dev.piotrulla.craftinglib.recipe.BrewingRecipe;
import dev.piotrulla.craftinglib.recipe.GrindstoneRecipe;
import dev.piotrulla.craftinglib.recipe.KeyedRecipe;
import dev.piotrulla.craftinglib.recipe.SmithingRecipe;
import dev.piotrulla.craftinglib.recipe.StonecuttingRecipe;
import dev.piotrulla.craftinglib.recipe.manager.CraftingRecipeManager;
import dev.piotrulla.craftinglib.recipe.manager.NoOpRecipeRegistrar;
import dev.piotrulla.craftinglib.recipe.manager.PlatformRecipeRegistrar;
import dev.piotrulla.craftinglib.recipe.manager.RecipeManager;
import dev.piotrulla.craftinglib.recipe.manager.SimpleCraftingRecipeManager;
import dev.piotrulla.craftinglib.recipe.manager.SimpleRecipeManager;
import dev.piotrulla.craftinglib.recipe.manager.UnsupportedRecipeRegistrar;
import dev.piotrulla.craftinglib.recipe.smelting.SmeltingRecipe;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * <pre>
 * // onEnable
 * this.craftingLib = BukkitCraftingLib.create(this);
 * this.craftingLib.getRecipeManager().registerRecipe(craftingRecipe);
 * this.craftingLib.getSmeltingManager().registerRecipe(smeltingRecipe);
 *
 * // onDisable
 * this.craftingLib.shutdown();
 * </pre>
 * Recipe types the server cannot handle fail on registration with {@link UnsupportedOperationException}.
 * Recipe book features (discovery, groups, categories, Paper autofill) are enabled where the server supports them.
 */
public final class BukkitCraftingLib implements CraftingLib<ItemStack> {

    private static final String BREWING_UNSUPPORTED = "Brewing recipes require Paper with the PotionMix API";
    private static final String STONECUTTING_UNSUPPORTED = "Stonecutting recipes require Minecraft 1.14+";
    private static final String SMITHING_UNSUPPORTED = "Smithing recipes require Minecraft 1.16+";
    private static final String ANVIL_UNSUPPORTED = "Anvil recipes require Minecraft 1.11+";
    private static final String GRINDSTONE_UNSUPPORTED = "Grindstone recipes require a server with PrepareGrindstoneEvent (1.19.3+)";

    private final CraftingRecipeManager<ItemStack> recipeManager;
    private final RecipeManager<SmeltingRecipe<ItemStack>> smeltingManager;
    private final RecipeManager<BrewingRecipe<ItemStack>> brewingManager;
    private final RecipeManager<StonecuttingRecipe<ItemStack>> stonecuttingManager;
    private final RecipeManager<SmithingRecipe<ItemStack>> smithingManager;
    private final RecipeManager<AnvilRecipe<ItemStack>> anvilManager;
    private final RecipeManager<GrindstoneRecipe<ItemStack>> grindstoneManager;
    private final List<Listener> listeners = new ArrayList<>();

    /**
     * Composition root: wires managers and listeners, registers nothing in the server yet.
     */
    private BukkitCraftingLib(Plugin plugin) {
        Logger logger = plugin.getLogger();
        VersionAdapter versionAdapter = VersionDetector.detect(plugin);
        RegistrarContext context = new RegistrarContext(plugin, versionAdapter, this.createRecipeBook(plugin));
        BukkitRecipeRegistrar craftingRegistrar = new BukkitRecipeRegistrar(context);

        this.recipeManager = new SimpleCraftingRecipeManager<>(craftingRegistrar);
        this.smeltingManager = new SimpleRecipeManager<>(new BukkitSmeltingRecipeRegistrar(context));
        this.brewingManager = managerFor(VersionDetector.isPotionMixSupported(),
                () -> new PaperBrewingRecipeRegistrar(plugin), BREWING_UNSUPPORTED);
        this.stonecuttingManager = managerFor(VersionDetector.isStonecuttingSupported(),
                () -> new BukkitStonecuttingRecipeRegistrar(context), STONECUTTING_UNSUPPORTED);
        this.smithingManager = managerFor(VersionDetector.isSmithingSupported(),
                () -> new BukkitSmithingRecipeRegistrar(context), SMITHING_UNSUPPORTED);
        this.anvilManager = managerFor(VersionDetector.isAnvilSupported(), NoOpRecipeRegistrar::new, ANVIL_UNSUPPORTED);
        this.grindstoneManager = managerFor(VersionDetector.isGrindstoneSupported(), NoOpRecipeRegistrar::new,
                GRINDSTONE_UNSUPPORTED);

        CraftingGuard craftingGuard = new CraftingGuard(this.recipeManager, craftingRegistrar);
        this.listeners.add(new CraftingListener(craftingGuard));
        if (VersionDetector.isCrafterSupported()) {
            this.listeners.add(new CrafterListener(craftingGuard));
        }
        if (VersionDetector.isAnvilSupported()) {
            this.listeners.add(new AnvilListener(this.anvilManager));
        }
        if (VersionDetector.isGrindstoneSupported()) {
            this.listeners.add(new GrindstoneListener(this.grindstoneManager));
        }
        if (VersionDetector.isRecipeBookAutofillSupported()) {
            this.listeners.add(new RecipeBookAutofillListener(this.recipeManager, versionAdapter));
        }

        logger.info("CraftingLib uses " + versionAdapter.getName() + " recipe adapter");
    }

    /**
     * Detects server capabilities and registers the listeners.
     */
    @NotNull
    public static BukkitCraftingLib create(@NotNull Plugin plugin) {
        Objects.requireNonNull(plugin, "plugin");

        BukkitCraftingLib craftingLib = new BukkitCraftingLib(plugin);
        for (Listener listener : craftingLib.listeners) {
            plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }

        return craftingLib;
    }

    /**
     * The factory runs only when supported, so version-specific classes are never loaded on older servers.
     */
    private static <R extends KeyedRecipe> RecipeManager<R> managerFor(
            boolean supported,
            Supplier<PlatformRecipeRegistrar<R>> registrarFactory,
            String unsupportedReason
    ) {
        PlatformRecipeRegistrar<R> registrar = supported
                ? registrarFactory.get()
                : new UnsupportedRecipeRegistrar<>(unsupportedReason);

        return new SimpleRecipeManager<>(registrar);
    }

    private RecipeBook createRecipeBook(Plugin plugin) {
        if (!VersionDetector.isRecipeBookSupported()) {
            return NoOpRecipeBook.INSTANCE;
        }

        PlayerRecipeBook recipeBook = new PlayerRecipeBook(plugin);
        this.listeners.add(recipeBook);
        return recipeBook;
    }

    @NotNull
    @Override
    public CraftingRecipeManager<ItemStack> getRecipeManager() {
        return this.recipeManager;
    }

    @NotNull
    @Override
    public RecipeManager<SmeltingRecipe<ItemStack>> getSmeltingManager() {
        return this.smeltingManager;
    }

    @NotNull
    @Override
    public RecipeManager<BrewingRecipe<ItemStack>> getBrewingManager() {
        return this.brewingManager;
    }

    @NotNull
    @Override
    public RecipeManager<StonecuttingRecipe<ItemStack>> getStonecuttingManager() {
        return this.stonecuttingManager;
    }

    @NotNull
    @Override
    public RecipeManager<SmithingRecipe<ItemStack>> getSmithingManager() {
        return this.smithingManager;
    }

    @NotNull
    @Override
    public RecipeManager<AnvilRecipe<ItemStack>> getAnvilManager() {
        return this.anvilManager;
    }

    @NotNull
    @Override
    public RecipeManager<GrindstoneRecipe<ItemStack>> getGrindstoneManager() {
        return this.grindstoneManager;
    }

    @Override
    public void shutdown() {
        List<RecipeManager<?>> managers = Arrays.asList(
                this.recipeManager,
                this.smeltingManager,
                this.brewingManager,
                this.stonecuttingManager,
                this.smithingManager,
                this.anvilManager,
                this.grindstoneManager
        );

        managers.forEach(RecipeManager::unregisterAll);
        this.listeners.forEach(HandlerList::unregisterAll);
    }
}
