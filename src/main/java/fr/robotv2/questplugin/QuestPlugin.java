package fr.robotv2.questplugin;

import fr.robotv2.questplugin.group.QuestGroupManager;
import fr.robotv2.questplugin.quest.QuestManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public final class QuestPlugin extends JavaPlugin {

    private QuestManager questManager;
    private QuestGroupManager questGroupManager;

    public static QuestPlugin instance() {
        return JavaPlugin.getPlugin(QuestPlugin.class);
    }

    public static Logger logger() {
        return instance().getLogger();
    }

    @Override
    public void onEnable() {

        if(!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        saveDefaultConfig();

        this.questGroupManager = new QuestGroupManager(getRelativeFile("groups"));
        this.questManager = new QuestManager(this, getRelativeFile("quests"));

        getQuestGroupManager().loadGroups();
        getQuestManager().loadQuests();
    }

    public void onReload() {
        getQuestGroupManager().loadGroups();
        getQuestManager().loadQuests();
    }

    public QuestManager getQuestManager() {
        return questManager;
    }

    public QuestGroupManager getQuestGroupManager() {
        return questGroupManager;
    }

    public File getRelativeFile(String path) {
        return new File(getDataFolder(), path);
    }
}
