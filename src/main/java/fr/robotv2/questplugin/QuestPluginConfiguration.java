package fr.robotv2.questplugin;

import fr.robotv2.questplugin.configurations.ActionBarTitlesSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class QuestPluginConfiguration {

    private boolean debug;

    private Map<String, ActionBarTitlesSection> cosmetics;

    public void loadConfiguration(@NotNull FileConfiguration configuration) {
        this.debug = configuration.getBoolean("debug");

        this.cosmetics = new HashMap<>();
        this.cosmetics.put("quest_increment", new ActionBarTitlesSection(configuration.getConfigurationSection("cosmetics.quest_increment")));
        this.cosmetics.put("quest_done", new ActionBarTitlesSection(configuration.getConfigurationSection("cosmetics.quest_done")));
        this.cosmetics.put("task_done", new ActionBarTitlesSection(configuration.getConfigurationSection("cosmetics.task_done")));
    }

    public boolean isDebug() {
        return debug;
    }

    public ActionBarTitlesSection getQuestIncrementCosmetics() {
        return cosmetics.get("quest_increment");
    }

    public ActionBarTitlesSection getQuestDoneCosmetics() {
        return cosmetics.get("quest_done");
    }

    public ActionBarTitlesSection getTaskDoneCosmetics() {
        return cosmetics.get("task_done");
    }
}
