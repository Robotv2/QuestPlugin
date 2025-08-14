package fr.robotv2.questplugin.storage;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.group.QuestGroup;
import fr.robotv2.questplugin.storage.model.ActiveQuest;
import fr.robotv2.questplugin.storage.model.QuestPlayer;
import fr.robotv2.questplugin.storage.repository.ActiveQuestRepository;
import fr.robotv2.questplugin.storage.repository.ActiveTaskRepository;
import fr.robotv2.questplugin.storage.repository.QuestPlayerRepository;
import fr.robotv2.questplugin.storage.repository.hsql.HsqlDatabaseManager;
import fr.robotv2.questplugin.storage.repository.json.JsonDatabaseManager;
import fr.robotv2.questplugin.storage.repository.mariadb.MariaDbDatabaseManager;
import fr.robotv2.questplugin.storage.repository.sqlite.SqliteDatabaseManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public interface DatabaseManager {

    Map<String, Supplier<DatabaseManager>> SUPPLIERS = new HashMap<>() {{
        put("JSON", () -> new JsonDatabaseManager(QuestPlugin.instance()));
        put("SQLITE", () -> new SqliteDatabaseManager(QuestPlugin.instance()));
        put("MARIADB", () -> new MariaDbDatabaseManager(QuestPlugin.instance()));
        put("HSQL", () -> new HsqlDatabaseManager(QuestPlugin.instance()));
    }};

    void init();

    void close();

    CompletableFuture<QuestPlayer> composePlayer(Player player, boolean shouldCache);

    CompletableFuture<Void> savePlayer(Player player, boolean removeFromCache);

    @NotNull
    QuestPlayerRepository getQuestPlayerRepository();

    @NotNull
    ActiveQuestRepository getActiveQuestRepository();

    @NotNull
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

    CompletableFuture<Void> removeQuestsAndTasks(ActiveQuest quest);
}
