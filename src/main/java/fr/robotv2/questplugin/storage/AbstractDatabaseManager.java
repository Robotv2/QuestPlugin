package fr.robotv2.questplugin.storage;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.group.QuestGroup;
import fr.robotv2.questplugin.storage.dto.ActiveQuestDto;
import fr.robotv2.questplugin.storage.dto.ActiveTaskDto;
import fr.robotv2.questplugin.storage.dto.QuestPlayerDto;
import fr.robotv2.questplugin.storage.loader.impl.MonoPlayerLoader;
import fr.robotv2.questplugin.storage.model.ActiveQuest;
import fr.robotv2.questplugin.storage.model.ActiveTask;
import fr.robotv2.questplugin.storage.model.QuestPlayer;
import fr.robotv2.questplugin.util.Futures;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public abstract class AbstractDatabaseManager implements DatabaseManager {

    protected final QuestPlugin plugin;
    private final Map<UUID, QuestPlayer> cache;
    private final Map<UUID, ReentrantLock> locks;

    public AbstractDatabaseManager(QuestPlugin plugin) {
        this.plugin = plugin;
        this.cache = new ConcurrentHashMap<>();
        this.locks = new ConcurrentHashMap<>();
        Bukkit.getPluginManager().registerEvents(new MonoPlayerLoader(this), plugin);
    }

    @NotNull
    public QuestPlugin getPlugin() {
        return plugin;
    }

    @UnmodifiableView
    public Map<UUID, QuestPlayer> getCache() {
        return Collections.unmodifiableMap(cache);
    }

    @Override
    public void init() {
        getQuestPlayerRepository().init();
        getActiveQuestRepository().init();
        getActiveTaskRepository().init();
    }

    @Override
    public void close() {
        getQuestPlayerRepository().close();
        getActiveQuestRepository().close();
        getActiveTaskRepository().close();
    }

    public ReentrantLock getLock(UUID playerId) {
        return locks.computeIfAbsent(playerId, (id) -> new ReentrantLock());
    }

    @Override
    public CompletableFuture<Void> savePlayer(Player player, boolean removeFromCache) {
        final UUID playerId = player.getUniqueId();
        final Lock lock = getLock(playerId);

        return CompletableFuture.runAsync(() -> {
            lock.lock();
            try {
                final QuestPlayer questPlayer = cache.get(playerId);
                if (questPlayer == null) return;

                List<CompletableFuture<Void>> futures = new ArrayList<>();
                futures.add(getQuestPlayerRepository().upsert(new QuestPlayerDto(questPlayer)));
                for (ActiveQuest quest : questPlayer.getActiveQuests()) {
                    futures.add(getActiveQuestRepository().upsert(new ActiveQuestDto(quest)));
                    for (ActiveTask task : quest.getTasks()) {
                        futures.add(getActiveTaskRepository().upsert(new ActiveTaskDto(task)));
                    }
                }

                Futures.ofAll(futures).join();

            } finally {
                lock.unlock(); // Release lock
            }
        }).thenRun(() -> {
            if (removeFromCache) cache.remove(playerId);
            locks.remove(playerId);
            plugin.getLogger().info("Data of player '" + player.getName() + "' saved successfully.");
        });
    }

    @Override
    public CompletableFuture<QuestPlayer> composePlayer(Player player, boolean shouldCache) {
        final UUID playerId = player.getUniqueId();
        final Lock lock = getLock(playerId);
        return CompletableFuture.supplyAsync(() -> {
            lock.lock(); // Acquire lock
            try {
                return populateQuestPlayer(player);
            } finally {
                lock.unlock();
            }
        }).thenCompose((future) -> future).thenApply((questPlayer) -> {
            if (shouldCache) {
                cache.put(player.getUniqueId(), questPlayer);
            }
            return questPlayer;
        });
    }

    private CompletableFuture<QuestPlayer> populateQuestPlayer(Player player) {
        return fetchQuestPlayer(player).thenCompose(this::fetchAndPopulateActiveQuests);
    }

    private CompletableFuture<QuestPlayer> fetchQuestPlayer(Player player) {
        return getQuestPlayerRepository()
                .select(player.getUniqueId())
                .thenApply(optional -> optional.map(QuestPlayer::new).orElse(new QuestPlayer(player)));
    }

    private CompletableFuture<QuestPlayer> fetchAndPopulateActiveQuests(QuestPlayer questPlayer) {
        if (questPlayer == null) return CompletableFuture.completedFuture(null);
        return getActiveQuestRepository().fetchByPlayer(questPlayer.getUID()).thenCompose((dtos) -> populateActiveQuests(questPlayer, dtos));
    }

    private CompletableFuture<QuestPlayer> populateActiveQuests(QuestPlayer questPlayer, Collection<ActiveQuestDto> activeQuestDtos) {
        if (activeQuestDtos.isEmpty()) {
            return CompletableFuture.completedFuture(questPlayer);
        }

        final Set<UUID> activeQuestIds = activeQuestDtos.stream().map(ActiveQuestDto::getUID).collect(Collectors.toSet());
        return getActiveTaskRepository().bulkFetchByActiveQuest(activeQuestIds)
                .thenApply((taskMap) -> {
                    for (ActiveQuestDto dto : activeQuestDtos) {
                        final List<ActiveTask> tasks = taskMap.get(dto.getUID()).stream().map(ActiveTask::new).toList();
                        final ActiveQuest activeQuest = new ActiveQuest(dto, tasks);
                        questPlayer.addActiveQuest(activeQuest);
                    }
                    return questPlayer;
                });
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
    public Collection<QuestPlayer> getCachedPlayers() {
        return Collections.unmodifiableCollection(cache.values());
    }

    @Override
    public CompletableFuture<Void> removeQuests(QuestGroup group) {
        return CompletableFuture.allOf(
            getActiveQuestRepository().remove(group),
            getActiveTaskRepository().remove(group)
        );
    }

    @Override
    public CompletableFuture<Void> removeQuests(QuestPlayer player) {
        final List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (ActiveQuest active : player.getActiveQuests()) {
            futures.add(removeQuestsAndTasks(active));
            player.removeActiveQuestById(active.getQuestId());
        }
        return Futures.ofAll(futures);
    }

    @Override
    public CompletableFuture<Void> removeQuests(QuestPlayer player, QuestGroup group) {
        final List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (ActiveQuest active : player.getActiveQuests(group)) {
            futures.add(removeQuestsAndTasks(active));
            player.removeActiveQuestById(active.getQuestId());
        }
        return Futures.ofAll(futures);
    }

    @Override
    public CompletableFuture<Void> removeQuestsIfEnded(QuestPlayer player) {
        final List<CompletableFuture<Void>> futures = new ArrayList<>();
        final List<String> ended = new ArrayList<>();

        for (ActiveQuest active : player.getActiveQuests()) {
            if (active.hasEnded()) {
                futures.add(removeQuestsAndTasks(active));
                ended.add(active.getQuestId());
            }
        }

        for(String questId : ended) {
            player.removeActiveQuestById(questId);
        }

        return Futures.ofAll(futures);
    }

    @Override
    public CompletableFuture<Void> removeQuestsAndTasks(ActiveQuest quest) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        futures.add(getActiveQuestRepository().removeFromId(quest.getUID()));
        futures.addAll(quest.getTasks().stream().map(task -> getActiveTaskRepository().removeFromId(task.getUID())).toList());
        return Futures.ofAll(futures);
    }
}
