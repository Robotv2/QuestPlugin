package fr.robotv2.questplugin.storage.model;

import fr.robotv2.questplugin.quest.Quest;
import fr.robotv2.questplugin.quest.task.Task;
import fr.robotv2.questplugin.storage.DirtyAware;
import fr.robotv2.questplugin.storage.Identifiable;
import fr.robotv2.questplugin.storage.dto.ActiveTaskDto;
import org.jetbrains.annotations.ApiStatus;

import java.math.BigDecimal;
import java.util.UUID;

public class ActiveTask implements java.io.Serializable, Identifiable<UUID>, DirtyAware {

    /**
     * Representing the unique identifier for this specific task
     */
    private UUID activeTaskUniqueId;

    /**
     * Representing the id of the parent quest of this task
     */
    private String parentQuestId;

    /**
     * Representing the id the group of the parent quest of this task
     */
    private String parentQuestGroupId;

    /**
     * Representing the id of the task
     */
    private int taskId;

    /**
     * Representing the progress of the task
     */
    private BigDecimal progress;

    /**
     * Representing the required progress to finish this task
     */
    private BigDecimal required;

    /**
     * Representing whether the quest has been done or not
     */
    private boolean done;

    /**
     * Representing whether the task need saving
     */
    private transient boolean dirty;

    public ActiveTask(Quest quest, Task task) {
        this(
                UUID.randomUUID(),
                quest.getQuestId(),
                quest.getQuestGroup().getGroupId(),
                task.getTaskId(),
                BigDecimal.valueOf(0),
                task.getRequiredAmount(),
                false
        );
    }

    @ApiStatus.Internal
    public ActiveTask(ActiveTaskDto dto) {
        this(
                dto.getId(),
                dto.getParentQuestId(),
                dto.getParentQuestGroupId(),
                dto.getTaskId(),
                dto.getProgress(),
                dto.getRequired(),
                dto.isDone()
        );
    }

    public ActiveTask(UUID activeTaskUniqueId, String parentQuestId, String parentQuestGroupId, int taskId, BigDecimal progress, BigDecimal required, boolean done) {
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

    public void addProgress(Number amount) {
        this.progress = progress.add(BigDecimal.valueOf(amount.doubleValue()));
        setDirty(true);
    }

    public void removeProgress(Number amount) {
        this.progress = progress.subtract(BigDecimal.valueOf(amount.doubleValue()));
        setDirty(true);
    }

    public BigDecimal getRequired() {
        return required;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
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
