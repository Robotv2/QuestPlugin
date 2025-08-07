package fr.robotv2.questplugin.storage.dto;

import fr.robotv2.questplugin.storage.Identifiable;
import fr.robotv2.questplugin.storage.model.ActiveTask;

import java.math.BigDecimal;
import java.util.UUID;

public class ActiveTaskDto implements Identifiable<UUID> {

    private final UUID activeTaskUniqueId;

    private final UUID parentActiveQuestId;

    private final String parentQuestId;

    private final String parentQuestGroupId;

    private final int taskId;

    private final BigDecimal progress;

    private final BigDecimal required;

    private final boolean done;

    public ActiveTaskDto(ActiveTask activeTask) {
        this.activeTaskUniqueId = activeTask.getUID();
        this.parentActiveQuestId = activeTask.getParentActiveQuestId();
        this.parentQuestId = activeTask.getParentQuestId();
        this.parentQuestGroupId = activeTask.getParentQuestGroupId();
        this.taskId = activeTask.getTaskId();
        this.progress = activeTask.getProgress();
        this.required = activeTask.getRequired();
        this.done = activeTask.isDone();
    }

    @Override
    public UUID getUID() {
        return activeTaskUniqueId;
    }

    public UUID getParentActiveQuestId() {
        return parentActiveQuestId;
    }

    public String getParentQuestId() {
        return parentQuestId;
    }

    public String getParentQuestGroupId() {
        return parentQuestGroupId;
    }

    public int getTaskId() {
        return taskId;
    }

    public BigDecimal getProgress() {
        return progress;
    }

    public BigDecimal getRequired() {
        return required;
    }

    public boolean isDone() {
        return done;
    }
}
