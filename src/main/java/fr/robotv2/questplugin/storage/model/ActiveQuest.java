package fr.robotv2.questplugin.storage.model;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.quest.Quest;
import fr.robotv2.questplugin.quest.enums.QuestStatus;
import fr.robotv2.questplugin.quest.options.Optionnable;
import fr.robotv2.questplugin.storage.DirtyAware;
import fr.robotv2.questplugin.storage.Identifiable;
import fr.robotv2.questplugin.storage.dto.ActiveQuestDto;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class ActiveQuest implements java.io.Serializable, Identifiable<UUID>, DirtyAware {

    private final UUID activeQuestUniqueId;

    private final UUID owner;

    private final String questId;

    private final String groupId;

    private final long nextReset;

    private final Set<ActiveTask> tasks;

    private boolean started;

    private int rerollCount;

    private transient boolean dirty;

    public ActiveQuest(Player player, Quest quest) {
        this.activeQuestUniqueId = UUID.randomUUID();
        this.owner = player.getUniqueId();
        this.questId = quest.getQuestId();
        this.groupId = quest.getQuestGroup().getGroupId();
        this.nextReset = quest.getQuestGroup().getNextReset();
        this.tasks = quest.getTasks().stream().map((task) -> new ActiveTask(quest, getUID(), task)).collect(Collectors.toSet());
        this.started = !quest.getOptionValue(Optionnable.Option.NEED_STARTING);
        this.rerollCount = 0;
    }

    public ActiveQuest(ActiveQuestDto dto, Collection<ActiveTask> tasks) {
        this.activeQuestUniqueId = dto.getUID();
        this.owner = dto.getOwner();
        this.questId = dto.getQuestId();
        this.groupId = dto.getGroupId();
        this.nextReset = dto.getNextReset();
        this.tasks = new HashSet<>(tasks);
        this.started = dto.isStarted();
        this.rerollCount = dto.getRerollCount();
    }

    @Override
    public UUID getUID() {
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

    @Nullable
    public Quest getQuest() {
        return QuestPlugin.instance().getQuestManager().fromId(questId, groupId);
    }

    public long getNextReset() {
        return nextReset;
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

    public void setStarted(boolean started) {
        this.started = started;
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

    public boolean isRerolled() {
        return rerollCount > 0;
    }

    public int getRerollCount() {
        return rerollCount;
    }

    public void setRerollCount(int rerollCount) {
        this.rerollCount = rerollCount;
    }
}
