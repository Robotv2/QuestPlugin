package fr.robotv2.questplugin.storage;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.storage.loader.impl.MonoPlayerLoader;
import fr.robotv2.questplugin.storage.dto.ActiveQuestDto;
import fr.robotv2.questplugin.storage.dto.ActiveTaskDto;
import fr.robotv2.questplugin.storage.dto.QuestPlayerDto;
import fr.robotv2.questplugin.storage.model.ActiveQuest;
import fr.robotv2.questplugin.storage.model.ActiveTask;
import fr.robotv2.questplugin.storage.model.QuestPlayer;
import fr.robotv2.questplugin.storage.repository.ActiveQuestRepository;
import fr.robotv2.questplugin.storage.repository.ActiveTaskRepository;
import fr.robotv2.questplugin.storage.repository.GlobalQuestRepository;
import fr.robotv2.questplugin.storage.repository.QuestPlayerRepository;
import fr.robotv2.questplugin.storage.repository.json.ActiveQuestJsonRepository;
import fr.robotv2.questplugin.storage.repository.json.ActiveTaskJsonRepository;
import fr.robotv2.questplugin.storage.repository.json.GlobalQuestJsonRepository;
import fr.robotv2.questplugin.storage.repository.json.QuestPlayerJsonRepository;
import fr.robotv2.questplugin.util.Futures;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InternalDatabaseManager {

    private final QuestPlugin plugin;
    private final DatabaseType databaseType;

    private QuestPlayerRepository questPlayerRepository;
    private ActiveQuestRepository activeQuestRepository;
    private ActiveTaskRepository activeTaskRepository;
    private GlobalQuestRepository globalQuestRepository;

    private final Map<UUID, QuestPlayer> cache;


    public InternalDatabaseManager(QuestPlugin plugin) {
        this.plugin = plugin;
        this.databaseType = DatabaseType.getByLiteral(
                plugin.getConfig().getString("database.type", "JSON")
        );
        this.cache = new ConcurrentHashMap<>();
    }

    public QuestPlugin getPlugin() {
        return plugin;
    }

    /**
     * Initializes the database repositories and registers event listeners.
     */
    public void init() {
        plugin.getLogger().info("Initiating data storage with type: " + databaseType.name());

        this.questPlayerRepository = new QuestPlayerJsonRepository(
                new File(plugin.getQuestDataFolder(), "players")
        );
        this.activeQuestRepository = new ActiveQuestJsonRepository(
                this, new File(plugin.getQuestDataFolder(), "quests")
        );
        this.activeTaskRepository = new ActiveTaskJsonRepository(
                new File(plugin.getQuestDataFolder(), "tasks")
        );
        this.globalQuestRepository = new GlobalQuestJsonRepository(
                new File(plugin.getQuestDataFolder(), "global")
        );

        initializeRepositories();
        plugin.getLogger().info("Data storage initialization complete.");

        // Registering player loader
        plugin.getServer().getPluginManager().registerEvents(new MonoPlayerLoader(this), plugin);
    }

    private void initializeRepositories() {
        questPlayerRepository.init();
        activeQuestRepository.init();
        activeTaskRepository.init();
        globalQuestRepository.init();
    }

    /**
     * Closes all database repositories.
     */
    public void close() {
        questPlayerRepository.close();
        activeQuestRepository.close();
        activeTaskRepository.close();
        globalQuestRepository.close();
    }

    /**
     * Saves the player's data and removes it from the cache.
     *
     * @param player the player whose data is to be saved
     * @return a CompletableFuture indicating the completion of the operation
     */
    public CompletableFuture<Void> savePlayerAndRemoveFromCache(Player player) {
        QuestPlayer questPlayer = Objects.requireNonNull(getCachedQuestPlayer(player.getUniqueId()), "Quest player not found");

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        futures.add(questPlayerRepository.upsert(new QuestPlayerDto(questPlayer)));

        for (ActiveQuest quest : questPlayer.getActiveQuests()) {
            futures.add(activeQuestRepository.upsert(new ActiveQuestDto(quest)));
            for (ActiveTask task : quest.getTasks()) {
                futures.add(activeTaskRepository.upsert(new ActiveTaskDto(task)));
            }
        }

        return Futures.ofAll(futures).thenRun(() -> {
            cache.remove(player.getUniqueId());
            plugin.getLogger().info("Data of player '" + player.getName() + "' saved successfully.");
        });
    }

    /**
     * Composes a QuestPlayer object for the given player and caches it.
     *
     * @param player the player for whom to compose the QuestPlayer
     * @return a CompletableFuture containing the QuestPlayer
     */
    public CompletableFuture<QuestPlayer> composePlayerAndCache(Player player) {
        return fetchCompletedQuestPlayer(player).thenApply(questPlayer -> {
            cache.put(questPlayer.getId(), questPlayer);
            return questPlayer;
        });
    }

    private CompletableFuture<QuestPlayer> fetchCompletedQuestPlayer(Player player) {
        return questPlayerRepository.select(player.getUniqueId()).thenCompose(optional -> {
            if (!optional.isPresent()) {
                return CompletableFuture.completedFuture(new QuestPlayer(player));
            }

            QuestPlayerDto dto = optional.get();
            List<CompletableFuture<ActiveQuest>> futures = dto.getActiveQuests().stream()
                    .map(this::fetchCompletedActiveQuest)
                    .collect(Collectors.toList());

            return Futures.ofAll(futures).thenApply(ignored -> {
                Set<ActiveQuest> activeQuests = futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toSet());
                return new QuestPlayer(dto, activeQuests);
            });
        });
    }

    private CompletableFuture<ActiveQuest> fetchCompletedActiveQuest(UUID activeQuestId) {
        return activeQuestRepository.select(activeQuestId).thenCompose(optional -> {
            if (!optional.isPresent()) {
                return CompletableFuture.completedFuture(null);
            }

            ActiveQuestDto dto = optional.get();
            List<CompletableFuture<ActiveTask>> futures = dto.getActiveTasks().stream()
                    .map(this::fetchCompletedActiveTask)
                    .collect(Collectors.toList());

            return Futures.ofAll(futures).thenApply(ignored -> {
                Set<ActiveTask> tasks = futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toSet());
                return new ActiveQuest(dto, tasks);
            });
        });
    }

    private CompletableFuture<ActiveTask> fetchCompletedActiveTask(UUID activeTaskId) {
        return activeTaskRepository.select(activeTaskId)
                .thenApply(optional -> optional.map(ActiveTask::new).orElse(null));
    }

    public QuestPlayerRepository getQuestPlayerRepository() {
        return questPlayerRepository;
    }

    public ActiveQuestRepository getActiveQuestRepository() {
        return activeQuestRepository;
    }

    public ActiveTaskRepository getActiveTaskRepository() {
        return activeTaskRepository;
    }

    public GlobalQuestRepository getGlobalQuestRepository() {
        return globalQuestRepository;
    }

    @Nullable
    public QuestPlayer getCachedQuestPlayer(Player player) {
        return getCachedQuestPlayer(player.getUniqueId());
    }

    @Nullable
    public QuestPlayer getCachedQuestPlayer(UUID playerId) {
        return cache.get(playerId);
    }

    @UnmodifiableView
    public Collection<QuestPlayer> getCachedQuestPlayers() {
        return Collections.unmodifiableCollection(cache.values());
    }
}
