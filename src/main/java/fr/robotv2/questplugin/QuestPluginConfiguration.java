package fr.robotv2.questplugin;

import fr.robotv2.questplugin.configurations.cosmetics.CosmeticMap;
import fr.robotv2.questplugin.configurations.cosmetics.Cosmeticable;
import fr.robotv2.questplugin.configurations.time.TimeFormatConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class QuestPluginConfiguration implements Cosmeticable {

    private boolean debug;

    private CosmeticMap cosmetics;

    private TimeFormatConfiguration timeFormatConfiguration;

    private MariaDbCredentials credentials;

    public void loadConfiguration(@NotNull FileConfiguration configuration) {
        this.debug = configuration.getBoolean("debug");
        this.cosmetics = new CosmeticMap(configuration.getConfigurationSection("cosmetics"));
        this.timeFormatConfiguration = new TimeFormatConfiguration(configuration.getConfigurationSection("time_format"));
        this.credentials = new MariaDbCredentials(configuration.getConfigurationSection("database.mariadb"));
    }

    public boolean isDebug() {
        return debug;
    }

    public TimeFormatConfiguration getTimeFormatConfiguration() {
        return timeFormatConfiguration;
    }

    public MariaDbCredentials getCredentials() {
        return credentials;
    }

    @Override
    public CosmeticMap getCosmeticMap() {
        return cosmetics;
    }

    public static class MariaDbCredentials {

        private final String host;
        private final int port;
        private final String database;
        private final String username;
        private final String password;

        public MariaDbCredentials(ConfigurationSection section) {
            this.host = section.getString("host", "localhost");
            this.port = section.getInt("port", 3306);
            this.database = section.getString("database", "quest");
            this.username = section.getString("username", "user");
            this.password = section.getString("password", "password");
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        public String getDatabase() {
            return database;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}
