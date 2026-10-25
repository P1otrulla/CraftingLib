/*
 * Copied from bStats Metrics (https://github.com/Bastian/bstats-metrics), MIT License,
 * Copyright (c) 2021 Bastian Oppermann. Only the package was changed (relocation).
 * Full license: META-INF/LICENSE-bStats
 */
package dev.piotrulla.craftinglib.bukkit.metrics.bstats.charts;

import dev.piotrulla.craftinglib.bukkit.metrics.bstats.json.JsonObjectBuilder;

import java.util.function.BiConsumer;

public abstract class CustomChart {

    private final String chartId;

    protected CustomChart(String chartId) {
        if (chartId == null) {
            throw new IllegalArgumentException("chartId must not be null");
        }
        this.chartId = chartId;
    }

    public JsonObjectBuilder.JsonObject getRequestJsonObject(BiConsumer<String, Throwable> errorLogger, boolean logErrors) {
        JsonObjectBuilder builder = new JsonObjectBuilder();
        builder.appendField("chartId", chartId);
        try {
            JsonObjectBuilder.JsonObject data = getChartData();
            if (data == null) {
                // If the data is null we don't send the chart.
                return null;
            }
            builder.appendField("data", data);
        } catch (Throwable t) {
            if (logErrors) {

                errorLogger.accept("Failed to get data for custom chart with id " + chartId, t);
            }
            return null;
        }
        return builder.build();
    }

    protected abstract JsonObjectBuilder.JsonObject getChartData() throws Exception;

}
