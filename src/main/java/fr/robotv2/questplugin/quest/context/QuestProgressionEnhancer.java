package fr.robotv2.questplugin.quest.context;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.event.QuestIncrementEvent;
import fr.robotv2.questplugin.quest.Quest;
import fr.robotv2.questplugin.quest.options.Optionnable;
import fr.robotv2.questplugin.quest.task.Task;
import fr.robotv2.questplugin.storage.model.ActiveQuest;
import fr.robotv2.questplugin.storage.model.ActiveTask;
import fr.robotv2.questplugin.storage.model.QuestPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.math.BigDecimal;
import java.util.Objects;

public abstract class QuestProgressionEnhancer implements Listener {

    private final QuestPlugin plugin;

    protected QuestProgressionEnhancer(QuestPlugin plugin) {
        this.plugin = plugin;
    }

    public void updateQuestProgress(RunningQuestContext<?, ?> context) {

        final QuestPlayer questPlayer = plugin.getDatabaseManager().getCachedQuestPlayer(context.getInitiator());

        if (questPlayer == null) {
            return;
        }

        for (ActiveQuest activeQuest : questPlayer.getActiveQuests()) {
            updateQuestProgressFor(activeQuest, context);
        }
    }

    public void updateQuestProgressFor(ActiveQuest activeQuest, RunningQuestContext<?, ?> context) {

        QuestPlugin.debug("Updating quest progress for active quest " + activeQuest.getQuestId() + " and player " + context.getInitiator().getName() + ".");

        final Quest quest = plugin.getQuestManager().fromId(activeQuest.getQuestId(), activeQuest.getGroupId());

        if(quest == null) {
            QuestPlugin.debug("Quest is no longer in the config.");
            return; // quest is no longer in the config.
        }

        boolean canProceed = true;

        for (Task task : quest.getTasks()) {
            final ActiveTask activeTask = activeQuest.getActiveTask(task.getTaskId());
            QuestPlugin.debug("Processing task " + task.getTaskId());

            if (activeTask == null) {
                QuestPlugin.debug("Couldn't find tasK.");
                continue;
            }

            if (quest.getOptionValue(Optionnable.Option.DEPENDANT_TASK) && !canProceed) {
                QuestPlugin.debug("Quest is dependant task and the player cannot proceed.");
                break; // break the loop if the previous task was not completed
            }

            canProceed = activeTask.isDone();

            if(!Objects.equals(task.getType(), context.getType())) {
                QuestPlugin.debug("Quest types are not similar. Task Type " + task.getType().getLiteral() + ", Required " + context.getType().getLiteral());
                continue;
            }

            if(activeTask.isDone()) {
                QuestPlugin.debug("Already done.");
                continue;
            }

            if(context.getTarget() != null && !task.isTarget(context.getTarget())) {
                QuestPlugin.debug("Target is not the required target. Found " + context.getTarget());
                continue; // only skip if the target is not null
            }

            final QuestIncrementEvent event = new QuestIncrementEvent(quest, task, activeQuest, activeTask, context.getInitiator());
            Bukkit.getPluginManager().callEvent(event);

            if(event.isCancelled()) {
                return;
            }

            activeTask.addProgress(context.getAmount());

            if(activeTask.getProgress().compareTo(task.getRequiredAmount()) >= 0) {
                activeTask.setDone(true);
            }
        }
    }
}
