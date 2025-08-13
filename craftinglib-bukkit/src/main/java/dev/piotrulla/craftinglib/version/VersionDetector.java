package dev.piotrulla.craftinglib.version;

import dev.piotrulla.craftinglib.version.adapter.LegacyVersionAdapter;
import dev.piotrulla.craftinglib.version.adapter.ModernVersionAdapter;
import dev.piotrulla.craftinglib.version.adapter.NamespacedVersionAdapter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * Detects the server version and returns appropriate adapter.
 */
public final class VersionDetector {

    private VersionDetector() {}

    @NotNull
    public static VersionAdapter detectVersion(@NotNull Plugin plugin) {
        String serverPackage = plugin.getServer().getClass().getPackage().getName();
        String version = serverPackage.substring(serverPackage.lastIndexOf('.') + 1);

        switch (version) {
            // Legacy versions (1.8-1.11)
            case "v1_8_R1":
            case "v1_8_R2":
            case "v1_8_R3":
            case "v1_9_R1":
            case "v1_9_R2":
            case "v1_10_R1":
            case "v1_11_R1":
                return new LegacyVersionAdapter(version);

            // Namespaced version (1.12)
            case "v1_12_R1":
                return new NamespacedVersionAdapter(plugin, version);

            // Modern versions (1.13+)
            default:
                return new ModernVersionAdapter(plugin, version);
        }
    }
}
