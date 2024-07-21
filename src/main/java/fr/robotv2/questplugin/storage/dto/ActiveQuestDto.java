package fr.robotv2.questplugin.storage.dto;

import fr.robotv2.questplugin.storage.Identifiable;
import fr.robotv2.questplugin.storage.model.ActiveQuest;
import fr.robotv2.questplugin.storage.model.ActiveTask;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ActiveQuestDto implements Identifiable<UUID> {

    private final UUID activeQuestUniqueId;

    private final UUID owner;

    private final String questId;

    private final String groupId;

    private final long nextReset;

    private final List<UUID> activeTasks;

    private final boolean started;

    public ActiveQuestDto(ActiveQuest activeQuest) {
        this(
                activeQuest.getId(),
                activeQuest.getOwner(),
                activeQuest.getQuestId(),
                activeQuest.getGroupId(),
                activeQuest.getNextReset(),
                activeQuest.getTasks().stream().map(ActiveTask::getId).collect(Collectors.toList()),
                activeQuest.isStarted()
        );
    }

    public ActiveQuestDto(UUID activeQuestUniqueId, UUID owner, String questId, String groupId, long nextReset, List<UUID> activeTasks, boolean started) {
        this.activeQuestUniqueId = activeQuestUniqueId;
        this.owner = owner;
        this.questId = questId;
        this.groupId = groupId;
        this.nextReset = nextReset;
        this.activeTasks = activeTasks;
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

    public List<UUID> getActiveTasks() {
        return activeTasks;
    }

    public boolean isStarted() {
        return started;
    }
}
