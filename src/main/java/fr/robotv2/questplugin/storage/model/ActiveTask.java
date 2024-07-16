package fr.robotv2.questplugin.storage.model;

import fr.robotv2.questplugin.quest.Quest;
import fr.robotv2.questplugin.quest.task.Task;

import java.math.BigDecimal;

public class ActiveTask implements java.io.Serializable {

    /**
     * Representing the id of the parent quest of this task
     */
    private String parentQuestId;

    /**
     * Representing the id of the task
     */
    private String taskId;

    /**
     * Representing the progress of the task
     */
    private BigDecimal progress;

    /**
     * Representing whether the quest has been done or not
     */
    private boolean done;

    public ActiveTask(Quest quest, Task task) {
        this(
                quest.getQuestId(),
                task.getTaskId(),
                BigDecimal.valueOf(0),
                false
        );
    }

    public ActiveTask(String parentQuestId, String taskId, BigDecimal progress, boolean done) {
        this.parentQuestId = parentQuestId;
        this.taskId = taskId;
        this.progress = progress;
        this.done = done;
    }

    public String getParentQuestId() {
        return parentQuestId;
    }

    public String getTaskId() {
        return taskId;
    }

    public BigDecimal getProgress() {
        return progress;
    }

    /**
     * Method to add progress to this model
     * @param amount the progress to add
     */
    public void addProgress(Number amount) {
        this.progress = progress.add(BigDecimal.valueOf(amount.doubleValue()));
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
