package fr.robotv2.questplugin.quest.context;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.conditions.Condition;
import fr.robotv2.questplugin.event.QuestDoneEvent;
import fr.robotv2.questplugin.event.QuestIncrementEvent;
import fr.robotv2.questplugin.quest.Quest;
import fr.robotv2.questplugin.quest.options.Optionnable;
import fr.robotv2.questplugin.quest.task.Task;
import fr.robotv2.questplugin.storage.model.ActiveQuest;
import fr.robotv2.questplugin.storage.model.ActiveTask;
import fr.robotv2.questplugin.storage.model.QuestPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.Collection;
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

        final Quest quest = plugin.getQuestManager().fromId(activeQuest.getQuestId(), activeQuest.getGroupId());

        if(quest == null) {
            return; // quest is no longer in the config.
        }

        if(!areAllConditionsMet(quest.getConditions(), context)) {
            return;
        }

        boolean canProceed = true;

        for (Task task : quest.getTasks()) {
            final ActiveTask activeTask = activeQuest.getActiveTask(task.getTaskId());

            if (activeTask == null) {
                continue;
            }

            if (quest.getOptionValue(Optionnable.Option.DEPENDANT_TASK) && !canProceed) {
                break; // break the loop if the previous task was not completed
            }

            canProceed = activeTask.isDone();

            if(!Objects.equals(task.getType(), context.getType())) {
                continue;
            }

            if(activeTask.isDone()) {
                continue;
            }

            if(!areAllConditionsMet(task.getConditions(), context)) {
                return;
            }

            if(context.getTarget() != null && !task.isTarget(context.getTarget())) {
                continue; // only skip if the target is not null
            }

            activeTask.addProgress(context.getAmount());

            final QuestIncrementEvent event = new QuestIncrementEvent(quest, task, activeQuest, activeTask, context.getInitiator());
            Bukkit.getPluginManager().callEvent(event);

            if(event.isCancelled()) {
                activeTask.removeProgress(context.getAmount());
                return;
            }

            if(activeTask.getProgress().compareTo(task.getRequiredAmount()) >= 0) {

                activeTask.setDone(true);

                if(activeQuest.isDone()) {
                    Bukkit.getPluginManager().callEvent(new QuestDoneEvent(quest, activeQuest, context.getInitiator()));
                }
            }
        }
    }

    private boolean areAllConditionsMet(Collection<Condition> conditions, RunningQuestContext<?, ?> context) {
        return conditions.stream().allMatch((condition) -> condition.isMet(context));
    }
}
