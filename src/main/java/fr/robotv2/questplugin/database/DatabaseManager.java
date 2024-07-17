package fr.robotv2.questplugin.database;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.storage.CompletableStorageManager;
import fr.robotv2.questplugin.storage.impl.PerValueFileStorage;
import fr.robotv2.questplugin.storage.model.QuestPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class DatabaseManager {

    private final QuestPlugin plugin;
    private final DatabaseType type;

    private CompletableStorageManager<UUID, QuestPlayer> storage;
    private final Map<UUID, QuestPlayer> cache;

    public DatabaseManager(QuestPlugin plugin) {
        this.plugin = plugin;
        this.type = DatabaseType.getByLiteral(plugin.getConfig().getString("database.type", "JSON"));
        this.cache = new ConcurrentHashMap<>();
    }

    public void init() {
        plugin.getLogger().info("Initiating data storage with type: " + type.name());
        this.storage = CompletableStorageManager.wrap(new PerValueFileStorage<>(new File(plugin.getDataFolder(), "players"), QuestPlayer.class));
        plugin.getLogger().info("Done initiating data storage.");
    }

    public void load(Player player) {
        storage.select(player.getUniqueId()).thenAccept((optional) -> {
            cache.put(player.getUniqueId(), optional.orElse(new QuestPlayer(player)));
        });
    }

    public CompletableFuture<Void> unloadAndSave(Player player) {
        final QuestPlayer questPlayer = getQuestPlayer(player);
        return storage.upsert(questPlayer).thenRun(() -> {
            cache.remove(player.getUniqueId());
        }).exceptionally((throwable) -> {
           plugin.getLogger().log(Level.SEVERE, "An error occurred while saving player " + player.getName(), throwable);
           return null;
        });
    }

    @Nullable
    public QuestPlayer getQuestPlayer(Player player) {
        return cache.get(player.getUniqueId());
    }

    @UnmodifiableView
    public Collection<QuestPlayer> getCachedQuestPlayer(Player player) {
        return Collections.unmodifiableCollection(cache.values());
    }

    public CompletableFuture<Optional<QuestPlayer>> getOfflineQuestPlayer(UUID uniqueId) {
        return storage.select(uniqueId);
    }

    public CompletableFuture<List<QuestPlayer>> getAllQuestPlayers() {
        return storage.sel
    }
}
