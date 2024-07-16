package fr.robotv2.questplugin.storage.model;

import fr.robotv2.questplugin.quest.Quest;
import fr.robotv2.questplugin.quest.enums.QuestStatus;
import fr.robotv2.questplugin.quest.options.Optionnable;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ActiveQuest implements Serializable {

    private UUID activeQuestUniqueId;

    private UUID owner;

    private String questId;

    private String groupId;

    private long nextReset;

    // HashSet is used directly for serialization reason.
    private HashSet<ActiveTask> tasks;

    private boolean started;

    public ActiveQuest(Player player, Quest quest) {
        this(
                UUID.randomUUID(),
                player.getUniqueId(),
                quest.getQuestId(),
                quest.getQuestGroup().getGroupId(),
                -1, // TODO
                quest.getTasks().stream().map((task) -> new ActiveTask(quest, task)).collect(Collectors.toCollection(HashSet::new)),
                !quest.getOptionValue(Optionnable.Option.NEED_STARTING)
        );
    }

    public ActiveQuest(UUID activeQuestUniqueId, UUID owner, String questId, String groupId, long nextReset, HashSet<ActiveTask> tasks, boolean started) {
        this.activeQuestUniqueId = activeQuestUniqueId;
        this.owner = owner;
        this.questId = questId;
        this.groupId = groupId;
        this.nextReset = nextReset;
        this.tasks = tasks;
        this.started = started;
    }

    public UUID getUniqueId() {
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

    public Set<ActiveTask> getTasks() {
        return tasks;
    }

    public ActiveTask getActiveTask(String taskId) {
        return tasks.stream().filter(activeTask -> activeTask.getTaskId().equals(taskId)).findAny().orElse(null);
    }

    public boolean isDone() {
        return tasks.stream().allMatch(ActiveTask::isDone);
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
}
