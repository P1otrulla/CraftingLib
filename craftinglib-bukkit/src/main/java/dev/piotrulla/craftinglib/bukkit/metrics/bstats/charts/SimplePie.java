/*
 * Copied from bStats Metrics (https://github.com/Bastian/bstats-metrics), MIT License,
 * Copyright (c) 2021 Bastian Oppermann. Only the package was changed (relocation).
 * Full license: META-INF/LICENSE-bStats
 */
package dev.piotrulla.craftinglib.bukkit.metrics.bstats.charts;

import dev.piotrulla.craftinglib.bukkit.metrics.bstats.json.JsonObjectBuilder;

import java.util.concurrent.Callable;

public class SimplePie extends CustomChart {

    private final Callable<String> callable;

    /**
     * Class constructor.
     *
     * @param chartId The id of the chart.
     * @param callable The callable which is used to request the chart data.
     */
    public SimplePie(String chartId, Callable<String> callable) {
        super(chartId);
        this.callable = callable;
    }

    @Override
    protected JsonObjectBuilder.JsonObject getChartData() throws Exception {
        String value = callable.call();
        if (value == null || value.isEmpty()) {
            // Null = skip the chart
            return null;
        }
        return new JsonObjectBuilder()
                .appendField("value", value)
                .build();
    }
}