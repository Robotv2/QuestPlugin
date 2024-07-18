package fr.robotv2.questplugin.database;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.database.loader.impl.MonoPlayerLoader;
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

        // registering player loaded
        plugin.getServer().getPluginManager().registerEvents(new MonoPlayerLoader(this), plugin);
    }

    public CompletableFuture<QuestPlayer> fetchAndCache(Player player) {
        return storage.select(player.getUniqueId()).thenApply((optional) -> {
            final QuestPlayer questPlayer = optional.orElse(new QuestPlayer(player));
            cache.put(player.getUniqueId(), questPlayer);
            return questPlayer;
        }).exceptionally((throwable) -> {
            plugin.getLogger().log(Level.SEVERE, "An error occurred while loading player: '" + player.getName() + "'", throwable);
            return null;
        });
    }

    public CompletableFuture<Void> saveAndUnload(Player player) {
        final QuestPlayer questPlayer = getCachedQuestPlayer(player);
        return storage.upsert(questPlayer).thenRun(() -> {
            cache.remove(player.getUniqueId());
        }).exceptionally((throwable) -> {
           plugin.getLogger().log(Level.SEVERE, "An error occurred while saving player: '" + player.getName() + "'", throwable);
           return null;
        });
    }

    @Nullable
    public QuestPlayer getCachedQuestPlayer(Player player) {
        return cache.get(player.getUniqueId());
    }

    @UnmodifiableView
    public Collection<QuestPlayer> getCachedQuestPlayers() {
        return Collections.unmodifiableCollection(cache.values());
    }

    public CompletableFuture<Optional<QuestPlayer>> getOfflineQuestPlayer(UUID uniqueId) {
        return storage.select(uniqueId);
    }

    public CompletableFuture<List<QuestPlayer>> getAllQuestPlayers() {
        return storage.selectAll();
    }
}
