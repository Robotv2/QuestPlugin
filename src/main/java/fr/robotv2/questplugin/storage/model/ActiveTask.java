package fr.robotv2.questplugin.storage.model;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.quest.Quest;
import fr.robotv2.questplugin.quest.task.Task;
import fr.robotv2.questplugin.storage.DirtyAware;
import fr.robotv2.questplugin.storage.Identifiable;
import fr.robotv2.questplugin.storage.dto.ActiveTaskDto;

import java.math.BigDecimal;
import java.util.UUID;

public class ActiveTask implements java.io.Serializable, Identifiable<UUID>, DirtyAware {

    // CONSTANTS
    private final UUID activeTaskUniqueId;

    private final UUID parentActiveQuestId;

    private final String parentQuestId;

    private final String parentQuestGroupId;

    private final int taskId;

    private final BigDecimal required;

    // VARIABLES
    private BigDecimal progress;

    private boolean done;

    private transient boolean dirty;

    public ActiveTask(Quest quest, UUID parentActiveQuestId, Task task) {
        this.activeTaskUniqueId = UUID.randomUUID();
        this.parentActiveQuestId = parentActiveQuestId;
        this.parentQuestId = quest.getQuestId();
        this.parentQuestGroupId = quest.getQuestGroup().getGroupId();
        this.taskId = task.getTaskId();
        this.required = BigDecimal.valueOf(task.getRequiredAmount().randomInt());
        this.progress = BigDecimal.valueOf(0);
        this.done = false;
    }

    public ActiveTask(ActiveTaskDto dto) {
        this.activeTaskUniqueId = dto.getUID();
        this.parentActiveQuestId = dto.getParentActiveQuestId();
        this.parentQuestId = dto.getParentQuestId();
        this.parentQuestGroupId = dto.getParentQuestGroupId();
        this.taskId = dto.getTaskId();
        this.progress = dto.getProgress();
        this.required = dto.getRequired();
        this.done = dto.isDone();
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

    public Task getTask() {
        return QuestPlugin.instance().getQuestManager().fromId(parentQuestId, parentQuestGroupId).getTask(taskId);
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
        setDirty(true);
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
