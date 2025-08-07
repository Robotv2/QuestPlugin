package fr.robotv2.questplugin.storage.repository.json;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.storage.AbstractDatabaseManager;
import fr.robotv2.questplugin.storage.repository.ActiveQuestRepository;
import fr.robotv2.questplugin.storage.repository.ActiveTaskRepository;
import fr.robotv2.questplugin.storage.repository.QuestPlayerRepository;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class JsonDatabaseManager extends AbstractDatabaseManager {

    private final QuestPlayerJsonRepository questPlayerJsonRepository;
    private final ActiveQuestJsonRepository activeQuestJsonRepository;
    private final ActiveTaskJsonRepository activeTaskJsonRepository;

    public JsonDatabaseManager(QuestPlugin plugin) {
        super(plugin);
        this.questPlayerJsonRepository = new QuestPlayerJsonRepository(new File(plugin.getQuestDataFolder(), "players"));
        this.activeQuestJsonRepository = new ActiveQuestJsonRepository(new File(plugin.getQuestDataFolder(), "quests"));
        this.activeTaskJsonRepository = new ActiveTaskJsonRepository(new File(plugin.getQuestDataFolder(), "tasks"));
    }

    @Override
    public @NotNull QuestPlayerRepository getQuestPlayerRepository() {
        return questPlayerJsonRepository;
    }

    @Override
    public @NotNull ActiveQuestRepository getActiveQuestRepository() {
        return activeQuestJsonRepository;
    }

    @Override
    public @NotNull ActiveTaskRepository getActiveTaskRepository() {
        return activeTaskJsonRepository;
    }
}
