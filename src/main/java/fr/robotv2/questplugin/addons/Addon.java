package fr.robotv2.questplugin.addons;

import fr.robotv2.questplugin.QuestPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public abstract class Addon {

    private FileConfiguration config = null;
    private File configFile = null;
    private ClassLoader loader;

    public void onLoad() {}

    public void onPreEnable() {}

    public void onPostEnable() {}

    public void onDisable() {}

    public void onReload() {}

    public abstract String getName();

    public abstract String getVersion();

    public QuestPlugin getPlugin() {
        return QuestPlugin.instance();
    }

    public File getFolder() {
        return getPlugin().getRelativeFile("addons/" + getName());
    }

    public String getConfigFileName() {
        return getName().toLowerCase() + "-config.yml";
    }

    public File getConfigFile() {
        return new File(getFolder(), getConfigFileName());
    }

    public ClassLoader getLoader() {
        return loader;
    }

    public void setLoader(ClassLoader loader) {
        this.loader = loader;
    }

    public void saveDefaultConfig() {
        if(getConfigFile().exists()) {
            return;
        }

        try (InputStream inputStream = this.getClass().getResourceAsStream("/" + getConfigFileName())) {
            if (inputStream != null) {
                Files.copy(inputStream, getConfigFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
            } else {
                getPlugin().getLogger().warning("Could not save default config for addon " + getName() + ": config.yml not found in resources.");
            }
        } catch (IOException exception) {
            getPlugin().getLogger().severe("Could not save default config for addon " + getName() + ": " + exception.getMessage());
        }
    }

    public void reloadConfig() {
        if (configFile == null) {
            configFile = getConfigFile();
        }
        config = YamlConfiguration.loadConfiguration(configFile);

        InputStream defConfigStream = this.getClass().getResourceAsStream("/" + getConfigFileName());
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
            config.setDefaults(defConfig);
        }
    }

    public FileConfiguration getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }
}
