package fr.robotv2.questplugin.storage.model;

import fr.robotv2.questplugin.quest.Quest;
import fr.robotv2.questplugin.quest.enums.QuestStatus;
import fr.robotv2.questplugin.quest.options.Optionnable;
import fr.robotv2.questplugin.storage.DirtyAware;
import fr.robotv2.questplugin.storage.Identifiable;
import fr.robotv2.questplugin.storage.dto.ActiveQuestDto;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ActiveQuest implements java.io.Serializable, Identifiable<UUID>, DirtyAware {

    private UUID activeQuestUniqueId;

    private UUID owner;

    private String questId;

    private String groupId;

    private long nextReset;

    private transient Set<ActiveTask> tasks;

    private boolean started;

    private transient boolean dirty;

    public ActiveQuest(Player player, Quest quest) {
        this(
                UUID.randomUUID(),
                player.getUniqueId(),
                quest.getQuestId(),
                quest.getQuestGroup().getGroupId(),
                quest.getQuestGroup().getNextReset(),
                quest.getTasks().stream().map((task) -> new ActiveTask(quest, task)).collect(Collectors.toSet()),
                !quest.getOptionValue(Optionnable.Option.NEED_STARTING)
        );
    }

    @ApiStatus.Internal
    public ActiveQuest(ActiveQuestDto dto, Set<ActiveTask> tasks) {
        this(
                dto.getId(),
                dto.getOwner(),
                dto.getQuestId(),
                dto.getGroupId(),
                dto.getNextReset(),
                tasks,
                dto.isStarted()
        );
    }

    public ActiveQuest(UUID activeQuestUniqueId, UUID owner, String questId, String groupId, long nextReset, @Nullable Set<ActiveTask> tasks, boolean started) {
        this.activeQuestUniqueId = activeQuestUniqueId;
        this.owner = owner;
        this.questId = questId;
        this.groupId = groupId;
        this.nextReset = nextReset;
        this.tasks = tasks;
        this.started = started;
    }

    @Override
    public UUID getId() {
        return activeQuestUniqueId;
    }

    public UUID getOwner() {
        return owner;
    }

    public String getQuestId() {
        return questId;
    }

    public String getGroupId() {
        return groupId;
    }

    public long getNextReset() {
        return nextReset;
    }

    @ApiStatus.Internal
    public void addTask(ActiveTask task) {
        if(tasks == null) {
            tasks = new HashSet<>();
        }
        tasks.add(task);
    }

    public Set<ActiveTask> getTasks() {
        return tasks == null ? Collections.emptySet() : tasks;
    }

    public ActiveTask getActiveTask(int taskId) {
        return getTasks().stream().filter(activeTask -> activeTask.getTaskId() == taskId).findAny().orElse(null);
    }

    public boolean isDone() {
        return getTasks().stream().allMatch(ActiveTask::isDone);
    }

    public boolean isStarted() {
        return started;
    }

    public boolean hasEnded() {
        return System.currentTimeMillis() >= this.nextReset;
    }

    public QuestStatus getStatus() {

        if(isDone()) {
            return QuestStatus.DONE;
        }

        return isStarted() ? QuestStatus.STARTED : QuestStatus.NOT_STARTED;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
