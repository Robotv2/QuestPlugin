package fr.robotv2.questplugin.storage.dto;

import fr.robotv2.questplugin.storage.Identifiable;
import fr.robotv2.questplugin.storage.model.ActiveTask;

import java.math.BigDecimal;
import java.util.UUID;

public class ActiveTaskDto implements Identifiable<UUID> {

    private final UUID activeTaskUniqueId;

    private final String parentQuestId;

    private final String parentQuestGroupId;

    private final int taskId;

    private final BigDecimal progress;

    private final BigDecimal required;

    private final boolean done;

    public ActiveTaskDto(ActiveTask activeTask) {
        this(
                activeTask.getId(),
                activeTask.getParentQuestId(),
                activeTask.getParentQuestGroupId(),
                activeTask.getTaskId(),
                activeTask.getProgress(),
                activeTask.getRequired(),
                activeTask.isDone()
        );
    }

    public ActiveTaskDto(UUID activeTaskUniqueId, String parentQuestId, String parentQuestGroupId, int taskId, BigDecimal progress, BigDecimal required, boolean done) {
        this.activeTaskUniqueId = activeTaskUniqueId;
        this.parentQuestId = parentQuestId;
        this.parentQuestGroupId = parentQuestGroupId;
        this.taskId = taskId;
        this.progress = progress;
        this.required = required;
        this.done = done;
    }

    @Override
    public UUID getId() {
        return activeTaskUniqueId;
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
