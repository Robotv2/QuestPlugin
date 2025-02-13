package fr.robotv2.questplugin.storage.repository.json;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.storage.AbstractDatabaseManager;
import fr.robotv2.questplugin.storage.repository.ActiveQuestRepository;
import fr.robotv2.questplugin.storage.repository.ActiveTaskRepository;
import fr.robotv2.questplugin.storage.repository.GlobalQuestRepository;
import fr.robotv2.questplugin.storage.repository.QuestPlayerRepository;

import java.io.File;

public class JsonDatabaseManager extends AbstractDatabaseManager {

    private final QuestPlayerJsonRepository questPlayerJsonRepository;
    private final ActiveQuestJsonRepository activeQuestJsonRepository;
    private final ActiveTaskJsonRepository activeTaskJsonRepository;
    private final GlobalQuestJsonRepository globalQuestJsonRepository;

    public JsonDatabaseManager(QuestPlugin plugin) {
        super(plugin);
        this.questPlayerJsonRepository = new QuestPlayerJsonRepository(new File(plugin.getQuestDataFolder(), "players"));
        this.activeQuestJsonRepository = new ActiveQuestJsonRepository(this, new File(plugin.getQuestDataFolder(), "quests"));
        this.activeTaskJsonRepository = new ActiveTaskJsonRepository(new File(plugin.getQuestDataFolder(), "tasks"));
        this.globalQuestJsonRepository = new GlobalQuestJsonRepository(new File(plugin.getQuestDataFolder(), "global"));
    }

    @Override
    public QuestPlayerRepository getQuestPlayerRepository() {
        return questPlayerJsonRepository;
    }

    @Override
    public ActiveQuestRepository getActiveQuestRepository() {
        return activeQuestJsonRepository;
    }

    @Override
    public ActiveTaskRepository getActiveTaskRepository() {
        return activeTaskJsonRepository;
    }

    @Override
    public GlobalQuestRepository getGlobalQuestRepository() {
        return globalQuestJsonRepository;
    }
}
