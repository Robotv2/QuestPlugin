package fr.robotv2.questplugin.event;

import fr.robotv2.questplugin.quest.Quest;

import fr.robotv2.questplugin.quest.task.Task;
import fr.robotv2.questplugin.storage.model.ActiveQuest;
import fr.robotv2.questplugin.storage.model.ActiveTask;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import java.math.BigDecimal;

public class QuestIncrementEvent extends QuestEvent implements Cancellable {

    private final static HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;

    private final Task task;

    private final BigDecimal progress;

    private boolean cancel = false;

    public QuestIncrementEvent(Quest quest, Task task, ActiveQuest activeQuest, ActiveTask activeTask, Player player) {
        super(quest);
        this.player = player;
        this.task = task;
    }

    public Player getPlayer() {
        return player;
    }

    public Task getTask() {
        return task;
    }

    public BigDecimal getProgress() {
        return progress;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
