package fr.robotv2.questplugin.storage.dto;

import fr.maxlego08.sarah.Column;
import fr.robotv2.questplugin.storage.Identifiable;
import fr.robotv2.questplugin.storage.model.ActiveTask;

import java.math.BigDecimal;
import java.util.UUID;

public class ActiveTaskDto implements Identifiable<UUID> {

    @Column("id")
    private final UUID activeTaskUniqueId;

    @Column("parent_quest_id")
    private final String parentQuestId;

    @Column("parent_quest_group_id")
    private final String parentQuestGroupId;

    @Column("task_id")
    private final int taskId;

    @Column("progress")
    private final BigDecimal progress;

    @Column("required")
    private final BigDecimal required;

    @Column("done")
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
