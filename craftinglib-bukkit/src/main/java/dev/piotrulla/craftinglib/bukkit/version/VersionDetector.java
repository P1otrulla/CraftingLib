package dev.piotrulla.craftinglib.bukkit.version;

import dev.piotrulla.craftinglib.bukkit.version.adapter.LegacyVersionAdapter;
import dev.piotrulla.craftinglib.bukkit.version.adapter.ModernVersionAdapter;
import dev.piotrulla.craftinglib.bukkit.version.adapter.NamespacedVersionAdapter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Detects server capabilities by class/method presence instead of parsing version strings,
 * so forks and backports work too.
 */
public final class VersionDetector {

    private static final String RECIPE_CHOICE_CLASS = "org.bukkit.inventory.RecipeChoice";
    private static final String NAMESPACED_KEY_CLASS = "org.bukkit.NamespacedKey";
    private static final String HUMAN_ENTITY_CLASS = "org.bukkit.entity.HumanEntity";
    private static final String INVENTORY_VIEW_CLASS = "org.bukkit.inventory.InventoryView";
    private static final String POTION_MIX_CLASS = "io.papermc.paper.potion.PotionMix";
    private static final String STONECUTTING_RECIPE_CLASS = "org.bukkit.inventory.StonecuttingRecipe";
    private static final String SMITHING_RECIPE_CLASS = "org.bukkit.inventory.SmithingRecipe";
    private static final String CRAFTER_CRAFT_EVENT_CLASS = "org.bukkit.event.block.CrafterCraftEvent";
    private static final String PREPARE_ANVIL_EVENT_CLASS = "org.bukkit.event.inventory.PrepareAnvilEvent";
    private static final String ANVIL_INVENTORY_CLASS = "org.bukkit.inventory.AnvilInventory";
    private static final String ANVIL_VIEW_CLASS = "org.bukkit.inventory.view.AnvilView";
    private static final String PREPARE_GRINDSTONE_EVENT_CLASS = "org.bukkit.event.inventory.PrepareGrindstoneEvent";
    private static final String GRINDSTONE_INVENTORY_CLASS = "org.bukkit.inventory.GrindstoneInventory";
    private static final String PERSISTENT_DATA_HOLDER_CLASS = "org.bukkit.persistence.PersistentDataHolder";
    private static final String RECIPE_BOOK_CLICK_EVENT_CLASS = "com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent";

    private static final String SET_REPAIR_COST_METHOD = "setRepairCost";
    private static final String DISCOVER_RECIPE_METHOD = "discoverRecipe";
    private static final String CREATE_PREDICATE_CHOICE_METHOD = "createPredicateChoice";

    private VersionDetector() {
    }

    @NotNull
    public static VersionAdapter detect(@NotNull Plugin plugin) {
        Objects.requireNonNull(plugin, "plugin");

        if (isClassPresent(RECIPE_CHOICE_CLASS)) {
            return new ModernVersionAdapter(plugin);
        }
        if (isClassPresent(NAMESPACED_KEY_CLASS)) {
            return new NamespacedVersionAdapter(plugin);
        }

        return new LegacyVersionAdapter();
    }

    public static boolean isPotionMixSupported() {
        return isClassPresent(POTION_MIX_CLASS);
    }

    /**
     * Paper {@code PotionMix#createPredicateChoice}, needed by {@code MatchMode.PERSISTENT_DATA} brewing recipes.
     */
    public static boolean isPotionMixPredicateSupported() {
        return isMethodPresent(POTION_MIX_CLASS, CREATE_PREDICATE_CHOICE_METHOD, Predicate.class);
    }

    public static boolean isStonecuttingSupported() {
        return isClassPresent(STONECUTTING_RECIPE_CLASS);
    }

    public static boolean isSmithingSupported() {
        return isClassPresent(SMITHING_RECIPE_CLASS);
    }

    public static boolean isCrafterSupported() {
        return isClassPresent(CRAFTER_CRAFT_EVENT_CLASS);
    }

    public static boolean isAnvilSupported() {
        return isClassPresent(PREPARE_ANVIL_EVENT_CLASS)
                && (isAnvilViewSupported() || isMethodPresent(ANVIL_INVENTORY_CLASS, SET_REPAIR_COST_METHOD, int.class));
    }

    /**
     * 1.21+: repair cost lives in {@code AnvilView}, {@code AnvilInventory#setRepairCost} is deprecated.
     */
    public static boolean isAnvilViewSupported() {
        return isClassPresent(ANVIL_VIEW_CLASS);
    }

    public static boolean isGrindstoneSupported() {
        return isClassPresent(PREPARE_GRINDSTONE_EVENT_CLASS) && isClassPresent(GRINDSTONE_INVENTORY_CLASS);
    }

    public static boolean isPersistentDataSupported() {
        return isClassPresent(PERSISTENT_DATA_HOLDER_CLASS);
    }

    /**
     * 1.13+: recipes can be unlocked in the recipe book of a player.
     */
    public static boolean isRecipeBookSupported() {
        return loadClass(NAMESPACED_KEY_CLASS)
                .map(keyClass -> isMethodPresent(HUMAN_ENTITY_CLASS, DISCOVER_RECIPE_METHOD, keyClass))
                .orElse(false);
    }

    /**
     * Paper recipe book click event plus {@code InventoryView} as an interface (1.21+), which the autofill listener is compiled against.
     */
    public static boolean isRecipeBookAutofillSupported() {
        return isClassPresent(RECIPE_BOOK_CLICK_EVENT_CLASS)
                && loadClass(INVENTORY_VIEW_CLASS).map(Class::isInterface).orElse(false);
    }

    public static boolean isClassPresent(@NotNull String className) {
        return loadClass(className).isPresent();
    }

    public static boolean isMethodPresent(@NotNull String className, @NotNull String methodName, @NotNull Class<?>... parameterTypes) {
        Optional<Class<?>> type = loadClass(className);
        if (!type.isPresent()) {
            return false;
        }

        try {
            type.get().getMethod(methodName, parameterTypes);
            return true;
        }
        catch (NoSuchMethodException exception) {
            return false;
        }
    }

    /**
     * Checks the runtime implementation, not the API interface: a server (or test mock) built against an older API
     * can declare a method through the interface without implementing it.
     */
    public static boolean isMethodImplemented(@NotNull Class<?> type, @NotNull String methodName, @NotNull Class<?>... parameterTypes) {
        try {
            Method method = type.getMethod(methodName, parameterTypes);
            return !Modifier.isAbstract(method.getModifiers());
        }
        catch (NoSuchMethodException exception) {
            return false;
        }
    }

    /**
     * Loads without initializing, so probing never runs static initializers of server classes.
     */
    @NotNull
    public static Optional<Class<?>> loadClass(@NotNull String className) {
        try {
            return Optional.of(Class.forName(className, false, VersionDetector.class.getClassLoader()));
        }
        catch (ClassNotFoundException | LinkageError exception) {
            return Optional.empty();
        }
    }
}
