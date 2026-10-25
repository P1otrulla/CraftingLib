package dev.piotrulla.craftinglib.bukkit.metrics;

import dev.piotrulla.craftinglib.bukkit.metrics.bstats.MetricsBase;
import dev.piotrulla.craftinglib.bukkit.metrics.bstats.charts.CustomChart;
import dev.piotrulla.craftinglib.bukkit.metrics.bstats.json.JsonObjectBuilder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

/**
 * bStats for CraftingLib itself (https://bstats.org/plugin/bukkit/CraftingLib/34932).
 * <p>
 * Same as bStats' Bukkit {@code Metrics} (MIT, Bastian Oppermann), except that it reports the CraftingLib version
 * instead of the host plugin version. Server owners opt out for every plugin at once with
 * {@code enabled: false} in {@code plugins/bStats/config.yml}; plugin authors with {@code CraftingLibSettings}.
 */
public final class CraftingLibMetrics {

    public static final int SERVICE_ID = 34932;

    private static final String PLATFORM = "bukkit";
    private static final String BSTATS_FOLDER = "bStats";
    private static final String CONFIG_FILE = "config.yml";
    private static final String FOLIA_SERVER_CLASS = "io.papermc.paper.threadedregions.RegionizedServer";
    private static final boolean SKIP_RELOCATE_CHECK = false;
    private static final int ONLINE = 1;
    private static final int OFFLINE = 0;

    private final Plugin plugin;
    private final MetricsBase metricsBase;

    @SuppressWarnings("deprecation")
    public CraftingLibMetrics(@NotNull Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");

        File bStatsFolder = new File(plugin.getDataFolder().getParentFile(), BSTATS_FOLDER);
        File configFile = new File(bStatsFolder, CONFIG_FILE);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        if (!config.isSet("serverUuid")) {
            config.addDefault("enabled", true);
            config.addDefault("serverUuid", UUID.randomUUID().toString());
            config.addDefault("logFailedRequests", false);
            config.addDefault("logSentData", false);
            config.addDefault("logResponseStatusText", false);

            config.options().header(
                    "bStats (https://bStats.org) collects some basic information for plugin authors, like how\n" +
                    "many people use their plugin and their total player count. It's recommended to keep bStats\n" +
                    "enabled, but if you're not comfortable with this, you can turn this setting off. There is no\n" +
                    "performance penalty associated with having metrics enabled, and data sent to bStats is fully\n" +
                    "anonymous.\n" +
                    "Learn more here: https://bstats.org/docs/server-owners"
            ).copyDefaults(true);
            try {
                config.save(configFile);
            }
            catch (IOException ignored) {
                // bStats works without a saved config, it is created again on the next start
            }
        }

        boolean enabled = config.getBoolean("enabled", true);
        String serverUuid = config.getString("serverUuid");
        boolean logErrors = config.getBoolean("logFailedRequests", false);
        boolean logSentData = config.getBoolean("logSentData", false);
        boolean logResponseStatusText = config.getBoolean("logResponseStatusText", false);
        boolean folia = this.isFolia();

        this.metricsBase = new MetricsBase(
                PLATFORM,
                serverUuid,
                SERVICE_ID,
                enabled,
                this::appendPlatformData,
                this::appendServiceData,
                folia ? null : submitDataTask -> Bukkit.getScheduler().runTask(plugin, submitDataTask),
                plugin::isEnabled,
                (message, error) -> this.plugin.getLogger().log(Level.WARNING, message, error),
                message -> this.plugin.getLogger().log(Level.INFO, message),
                logErrors,
                logSentData,
                logResponseStatusText,
                SKIP_RELOCATE_CHECK
        );
    }

    public void addCustomChart(@NotNull CustomChart chart) {
        this.metricsBase.addCustomChart(chart);
    }

    public void shutdown() {
        this.metricsBase.shutdown();
    }

    private void appendPlatformData(JsonObjectBuilder builder) {
        builder.appendField("playerAmount", this.getPlayerAmount());
        builder.appendField("onlineMode", Bukkit.getOnlineMode() ? ONLINE : OFFLINE);
        builder.appendField("bukkitVersion", Bukkit.getVersion());
        builder.appendField("bukkitName", Bukkit.getName());

        builder.appendField("javaVersion", System.getProperty("java.version"));
        builder.appendField("osName", System.getProperty("os.name"));
        builder.appendField("osArch", System.getProperty("os.arch"));
        builder.appendField("osVersion", System.getProperty("os.version"));
        builder.appendField("coreCount", Runtime.getRuntime().availableProcessors());
    }

    private void appendServiceData(JsonObjectBuilder builder) {
        builder.appendField("pluginVersion", CraftingLibVersion.VERSION);
    }

    private boolean isFolia() {
        try {
            Class.forName(FOLIA_SERVER_CLASS);
            return true;
        }
        catch (ClassNotFoundException exception) {
            return false;
        }
    }

    /**
     * Around 1.8 the return type changed from an array to a collection.
     */
    private int getPlayerAmount() {
        try {
            Method onlinePlayersMethod = Class.forName("org.bukkit.Server").getMethod("getOnlinePlayers");
            return onlinePlayersMethod.getReturnType().equals(Collection.class)
                    ? ((Collection<?>) onlinePlayersMethod.invoke(Bukkit.getServer())).size()
                    : ((Player[]) onlinePlayersMethod.invoke(Bukkit.getServer())).length;
        }
        catch (Exception exception) {
            return Bukkit.getOnlinePlayers().size();
        }
    }
}
