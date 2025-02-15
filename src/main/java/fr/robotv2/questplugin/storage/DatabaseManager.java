package fr.robotv2.questplugin.storage;

import fr.robotv2.questplugin.group.QuestGroup;
import fr.robotv2.questplugin.storage.model.QuestPlayer;
import fr.robotv2.questplugin.storage.repository.ActiveQuestRepository;
import fr.robotv2.questplugin.storage.repository.ActiveTaskRepository;
import fr.robotv2.questplugin.storage.repository.GlobalQuestRepository;
import fr.robotv2.questplugin.storage.repository.QuestPlayerRepository;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface DatabaseManager {

    void init();

    void close();

    CompletableFuture<QuestPlayer> composePlayer(Player player, boolean shouldCache);

    CompletableFuture<Void> savePlayer(Player player, boolean removeFromCache);

    QuestPlayerRepository getQuestPlayerRepository();

    ActiveQuestRepository getActiveQuestRepository();

    ActiveTaskRepository getActiveTaskRepository();

    @Nullable
    QuestPlayer getCachedQuestPlayer(Player player);

    @Nullable
    QuestPlayer getCachedQuestPlayer(UUID playerId);

    @UnmodifiableView
    Collection<QuestPlayer> getCachedPlayers();

    CompletableFuture<Void> removeQuests(QuestGroup group);

    CompletableFuture<Void> removeQuests(QuestPlayer group);

    CompletableFuture<Void> removeQuests(QuestPlayer player, QuestGroup group);

    CompletableFuture<Void> removeQuestsIfEnded(QuestPlayer player);
}
