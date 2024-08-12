package fr.robotv2.questplugin.api.database;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.group.QuestGroup;
import fr.robotv2.questplugin.storage.model.GlobalQuest;
import fr.robotv2.questplugin.storage.model.QuestPlayer;
import fr.robotv2.questplugin.storage.repository.ActiveQuestRepository;
import fr.robotv2.questplugin.storage.repository.ActiveTaskRepository;
import fr.robotv2.questplugin.storage.repository.GlobalQuestRepository;
import fr.robotv2.questplugin.storage.repository.QuestPlayerRepository;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IDatabaseManager {

    QuestPlugin getPlugin();

    void init();

    void close();

    CompletableFuture<Void> savePlayerAndRemoveFromCache(Player player);

    CompletableFuture<QuestPlayer> composePlayerAndCache(Player player);

    QuestPlayerRepository getQuestPlayerRepository();

    ActiveQuestRepository getActiveQuestRepository();

    ActiveTaskRepository getActiveTaskRepository();

    GlobalQuestRepository getGlobalQuestRepository();

    QuestPlayer getCachedQuestPlayer(Player player);

    QuestPlayer getCachedQuestPlayer(UUID playerId);

    Collection<QuestPlayer> getCachedQuestPlayers();
}
