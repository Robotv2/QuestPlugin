package fr.robotv2.questplugin;

import fr.robotv2.questplugin.configurations.cosmetics.CosmeticMap;
import fr.robotv2.questplugin.configurations.cosmetics.Cosmeticable;
import fr.robotv2.questplugin.configurations.time.TimeFormatConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class QuestPluginConfiguration implements Cosmeticable {

    private boolean debug;

    private CosmeticMap cosmetics;

    private TimeFormatConfiguration timeFormatConfiguration;

    public void loadConfiguration(@NotNull FileConfiguration configuration) {
        this.debug = configuration.getBoolean("debug");
        this.cosmetics = new CosmeticMap(configuration.getConfigurationSection("cosmetics"));
        this.timeFormatConfiguration = new TimeFormatConfiguration(configuration.getConfigurationSection("time_format"));
    }

    public boolean isDebug() {
        return debug;
    }

    public TimeFormatConfiguration getTimeFormatConfiguration() {
        return timeFormatConfiguration;
    }

    @Override
    public CosmeticMap getCosmeticMap() {
        return cosmetics;
    }
}
