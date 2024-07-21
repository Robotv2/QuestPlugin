package fr.robotv2.questplugin.database;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.database.loader.impl.MonoPlayerLoader;
import fr.robotv2.questplugin.storage.dto.ActiveQuestDto;
import fr.robotv2.questplugin.storage.dto.ActiveTaskDto;
import fr.robotv2.questplugin.storage.dto.QuestPlayerDto;
import fr.robotv2.questplugin.storage.model.ActiveQuest;
import fr.robotv2.questplugin.storage.model.ActiveTask;
import fr.robotv2.questplugin.storage.model.QuestPlayer;
import fr.robotv2.questplugin.storage.repository.ActiveQuestRepository;
import fr.robotv2.questplugin.storage.repository.ActiveTaskRepository;
import fr.robotv2.questplugin.storage.repository.QuestPlayerRepository;
import fr.robotv2.questplugin.storage.repository.json.ActiveQuestJsonRepository;
import fr.robotv2.questplugin.storage.repository.json.ActiveTaskJsonRepository;
import fr.robotv2.questplugin.storage.repository.json.QuestPlayerJsonRepository;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DatabaseManager {

    private final QuestPlugin plugin;
    private final DatabaseType type;

    private QuestPlayerRepository questPlayerRepository;
    private ActiveQuestRepository activeQuestRepository;
    private ActiveTaskRepository activeTaskRepository;

    private final Map<UUID, QuestPlayer> cache;

    public DatabaseManager(QuestPlugin plugin) {
        this.plugin = plugin;
        this.type = DatabaseType.getByLiteral(plugin.getConfig().getString("database.type", "JSON"));
        this.cache = new ConcurrentHashMap<>();
    }

    public void init() {
        plugin.getLogger().info("Initiating data storage with type: " + type.name());

        this.questPlayerRepository = new QuestPlayerJsonRepository(this, new File(plugin.getQuestDataFolder(), "players"));
        this.activeQuestRepository = new ActiveQuestJsonRepository(this, new File(plugin.getQuestDataFolder(), "quests"));
        this.activeTaskRepository = new ActiveTaskJsonRepository(this, new File(plugin.getQuestDataFolder(), "tasks"));

        getQuestPlayerRepository().init();
        getActiveQuestRepository().init();
        getActiveTaskRepository().init();

        plugin.getLogger().info("Done initiating data storage.");

        // registering player loaded
        plugin.getServer().getPluginManager().registerEvents(new MonoPlayerLoader(this), plugin);
    }

    public void close() {
        getQuestPlayerRepository().close();
        getActiveQuestRepository().close();
        getActiveTaskRepository().close();
    }

    public CompletableFuture<Void> savePlayerAndRemoveFromCache(Player player) {

        final QuestPlayer questPlayer = Objects.requireNonNull(getCachedQuestPlayer(player.getUniqueId()), "quest player");
        final List<CompletableFuture<Void>> futures = new ArrayList<>();

        futures.add(getQuestPlayerRepository().insert(new QuestPlayerDto(questPlayer)));
        for (ActiveQuest quest : questPlayer.getActiveQuests()) {
            futures.add(getActiveQuestRepository().insert(new ActiveQuestDto(quest)));
            for (ActiveTask task : quest.getTasks()) {
                futures.add(getActiveTaskRepository().insert(new ActiveTaskDto(task)));
            }
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(() -> {
            cache.remove(player.getUniqueId());
            plugin.getLogger().info("Data of player '" + player.getName() + "' have been saved successfully.");
        });
    }

    public CompletableFuture<Void> composePlayerAndCache(Player player) {
        return fetchCompletedQuestPlayer(player.getUniqueId()).thenAccept((questPlayer) -> {
            cache.put(questPlayer.getId(), questPlayer);
        });
    }

    private CompletableFuture<QuestPlayer> fetchCompletedQuestPlayer(UUID playerId) {
        return getQuestPlayerRepository().select(playerId).thenCompose((optional) -> {

            if (!optional.isPresent()) {
                return CompletableFuture.completedFuture(null);
            }

            final QuestPlayerDto dto = optional.get();
            final List<CompletableFuture<ActiveQuest>> futures = dto.getActiveQuests().stream().map(this::fetchCompletedActiveQuest).collect(Collectors.toList());

            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenApply((ignored) -> {
                Set<ActiveQuest> activeQuests = futures.stream().map(CompletableFuture::join).collect(Collectors.toSet());
                return new QuestPlayer(dto, activeQuests);
            });
        });
    }

    private CompletableFuture<ActiveQuest> fetchCompletedActiveQuest(UUID activeQuestId) {
        return getActiveQuestRepository().select(activeQuestId).thenCompose((optional) -> {

            if (!optional.isPresent()) {
                return CompletableFuture.completedFuture(null);
            }

            final ActiveQuestDto dto = optional.get();
            final List<CompletableFuture<ActiveTask>> futures = dto.getActiveTasks().stream().map(this::fetchCompletedActiveTask).collect(Collectors.toList());

            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenApply((ignored) -> {
                Set<ActiveTask> tasks = futures.stream().map(CompletableFuture::join).collect(Collectors.toSet());
                return new ActiveQuest(dto, tasks);
            });
        });
    }

    private CompletableFuture<ActiveTask> fetchCompletedActiveTask(UUID activeTaskId) {
        return getActiveTaskRepository().select(activeTaskId).thenApply((optional) -> optional.map(ActiveTask::new).orElse(null));
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

    @Nullable
    public QuestPlayer getCachedQuestPlayer(Player player) {
        return getCachedQuestPlayer(player);
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
