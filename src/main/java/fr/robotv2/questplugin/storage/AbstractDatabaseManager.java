package fr.robotv2.questplugin.storage;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.storage.dto.ActiveQuestDto;
import fr.robotv2.questplugin.storage.dto.ActiveTaskDto;
import fr.robotv2.questplugin.storage.dto.QuestPlayerDto;
import fr.robotv2.questplugin.storage.model.ActiveQuest;
import fr.robotv2.questplugin.storage.model.ActiveTask;
import fr.robotv2.questplugin.storage.model.QuestPlayer;
import fr.robotv2.questplugin.util.Futures;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

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

public abstract class AbstractDatabaseManager implements DatabaseManager {

    private final QuestPlugin plugin;
    private final Map<UUID, QuestPlayer> cache;

    public AbstractDatabaseManager(QuestPlugin plugin) {
        this.plugin = plugin;
        this.cache = new ConcurrentHashMap<>();
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
        getGlobalQuestRepository().init();
    }

    @Override
    public void close() {
        getQuestPlayerRepository().close();
        getActiveQuestRepository().close();
        getActiveTaskRepository().close();
        getGlobalQuestRepository().close();
    }

    @Override
    public CompletableFuture<Void> savePlayer(Player player, boolean removeFromCache) {
        final QuestPlayer questPlayer = Objects.requireNonNull(getCachedQuestPlayer(player.getUniqueId()), "Quest player not found");

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        futures.add(getQuestPlayerRepository().upsert(new QuestPlayerDto(questPlayer)));

        for (ActiveQuest quest : questPlayer.getActiveQuests()) {
            futures.add(getActiveQuestRepository().upsert(new ActiveQuestDto(quest)));
            for (ActiveTask task : quest.getTasks()) {
                futures.add(getActiveTaskRepository().upsert(new ActiveTaskDto(task)));
            }
        }

        return Futures.ofAll(futures).thenRun(() -> {
            if(removeFromCache) {
                cache.remove(player.getUniqueId());
            }
            plugin.getLogger().info("Data of player '" + player.getName() + "' saved successfully.");
        });
    }

    @Override
    public CompletableFuture<QuestPlayer> composePlayer(Player player, boolean shouldCache) {
        return fetchCompletedQuestPlayer(player).thenApply(questPlayer -> {
            if (shouldCache) {
                cache.put(player.getUniqueId(), questPlayer);
            }
            return questPlayer;
        });
    }

    private CompletableFuture<QuestPlayer> fetchCompletedQuestPlayer(Player player) {
        return getQuestPlayerRepository().select(player.getUniqueId()).thenCompose(optional -> {
            if (!optional.isPresent()) {
                return CompletableFuture.completedFuture(new QuestPlayer(player));
            }

            QuestPlayerDto dto = optional.get();
            List<CompletableFuture<ActiveQuest>> futures = dto.getActiveQuests().stream().map(this::fetchCompletedActiveQuest).collect(Collectors.toList());

            return Futures.ofAll(futures).thenApply(ignored -> {
                Set<ActiveQuest> activeQuests = futures.stream().map(CompletableFuture::join).collect(Collectors.toSet());
                return new QuestPlayer(dto, activeQuests);
            });
        });
    }

    private CompletableFuture<ActiveQuest> fetchCompletedActiveQuest(UUID activeQuestId) {
        return getActiveQuestRepository().select(activeQuestId).thenCompose(optional -> {
            if (!optional.isPresent()) {
                return CompletableFuture.completedFuture(null);
            }

            ActiveQuestDto dto = optional.get();
            List<CompletableFuture<ActiveTask>> futures = dto.getActiveTasks().stream().map(this::fetchCompletedActiveTask).collect(Collectors.toList());

            return Futures.ofAll(futures).thenApply(ignored -> {
                Set<ActiveTask> tasks = futures.stream().map(CompletableFuture::join).collect(Collectors.toSet());
                return new ActiveQuest(dto, tasks);
            });
        });
    }

    private CompletableFuture<ActiveTask> fetchCompletedActiveTask(UUID activeTaskId) {
        return getActiveTaskRepository().select(activeTaskId).thenApply(optional -> optional.map(ActiveTask::new).orElse(null));
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
}
