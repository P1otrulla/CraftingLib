package dev.piotrulla.craftinglib.bukkit.metrics;

import dev.piotrulla.craftinglib.bukkit.metrics.bstats.charts.AdvancedPie;
import dev.piotrulla.craftinglib.bukkit.metrics.bstats.charts.SimplePie;
import dev.piotrulla.craftinglib.bukkit.metrics.bstats.charts.SingleLineChart;
import dev.piotrulla.craftinglib.recipe.manager.RecipeManager;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Custom charts of the CraftingLib bStats page. Chart ids must match the charts created on bstats.org.
 * Data is collected on the main thread (bStats schedules it there), so reading the managers is safe.
 */
public final class MetricsCharts {

    public static final String LIBRARY_VERSION_CHART = "craftinglib_version";
    public static final String VERSION_ADAPTER_CHART = "version_adapter";
    public static final String REGISTERED_RECIPES_CHART = "registered_recipes";
    public static final String RECIPE_TYPES_CHART = "recipe_types";

    private static final int NONE = 0;

    private MetricsCharts() {
    }

    /**
     * @param managersByType recipe type name (e.g. {@code crafting}) to its manager
     */
    public static void register(
            @NotNull CraftingLibMetrics metrics,
            @NotNull String versionAdapterName,
            @NotNull Map<String, RecipeManager<?>> managersByType
    ) {
        Objects.requireNonNull(metrics, "metrics");
        Objects.requireNonNull(versionAdapterName, "versionAdapterName");
        Map<String, RecipeManager<?>> managers = new LinkedHashMap<>(managersByType);

        metrics.addCustomChart(new SimplePie(LIBRARY_VERSION_CHART, () -> CraftingLibVersion.VERSION));
        metrics.addCustomChart(new SimplePie(VERSION_ADAPTER_CHART, () -> versionAdapterName));
        metrics.addCustomChart(new SingleLineChart(REGISTERED_RECIPES_CHART, () -> countAll(managers)));
        metrics.addCustomChart(new AdvancedPie(RECIPE_TYPES_CHART, () -> countByType(managers)));
    }

    private static int countAll(Map<String, RecipeManager<?>> managers) {
        int count = NONE;
        for (RecipeManager<?> manager : managers.values()) {
            count += manager.getRecipes().size();
        }

        return count;
    }

    /**
     * Types without recipes are left out, bStats skips the chart when the map is empty.
     */
    private static Map<String, Integer> countByType(Map<String, RecipeManager<?>> managers) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        managers.forEach((type, manager) -> {
            int count = manager.getRecipes().size();
            if (count > NONE) {
                counts.put(type, count);
            }
        });

        return counts;
    }
}
