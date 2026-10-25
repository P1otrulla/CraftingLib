package dev.piotrulla.craftinglib.bukkit;

import org.jetbrains.annotations.NotNull;

/**
 * Options of {@link BukkitCraftingLib#create(org.bukkit.plugin.Plugin, CraftingLibSettings)}.
 * <pre>
 * BukkitCraftingLib.create(this, CraftingLibSettings.builder()
 *     .metrics(false)
 *     .build());
 * </pre>
 */
public final class CraftingLibSettings {

    private static final boolean DEFAULT_METRICS = true;

    private final boolean metrics;

    private CraftingLibSettings(Builder builder) {
        this.metrics = builder.metrics;
    }

    @NotNull
    public static CraftingLibSettings defaults() {
        return builder().build();
    }

    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * @return true when CraftingLib reports anonymous usage to bStats (https://bstats.org/plugin/bukkit/CraftingLib/34932)
     */
    public boolean isMetricsEnabled() {
        return this.metrics;
    }

    public static final class Builder {

        private boolean metrics = DEFAULT_METRICS;

        private Builder() {
        }

        /**
         * Defaults to true. Server owners can also disable bStats globally in {@code plugins/bStats/config.yml}.
         */
        @NotNull
        public Builder metrics(boolean metrics) {
            this.metrics = metrics;
            return this;
        }

        @NotNull
        public CraftingLibSettings build() {
            return new CraftingLibSettings(this);
        }
    }
}
