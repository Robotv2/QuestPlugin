package fr.robotv2.questplugin.quest.context;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.quest.Quest;
import fr.robotv2.questplugin.quest.options.Optionnable;
import fr.robotv2.questplugin.quest.task.Task;
import fr.robotv2.questplugin.storage.model.ActiveQuest;
import fr.robotv2.questplugin.storage.model.ActiveTask;
import fr.robotv2.questplugin.storage.model.QuestPlayer;
import org.bukkit.event.Listener;

import java.util.Objects;

public abstract class QuestProgressionEnhancer implements Listener {

    private final QuestPlugin plugin;

    protected QuestProgressionEnhancer(QuestPlugin plugin) {
        this.plugin = plugin;
    }

    public void updateQuestProgress(RunningQuestContext<?, ?> context) {

        final QuestPlayer questPlayer = null; // TODO

        if (questPlayer == null) {
            return;
        }

        for (ActiveQuest activeQuest : questPlayer.getActiveQuests()) {
            updateQuestProgressFor(activeQuest, context);
        }
    }

    public void updateQuestProgressFor(ActiveQuest activeQuest, RunningQuestContext<?, ?> context) {

        final Quest quest = plugin.getQuestManager().fromId(activeQuest.getQuestId());

        if(quest == null) {
            return; // quest is no longer in the config.
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

            if(context.getTarget() != null && !task.isTarget(context.getTarget())) {
                continue; // only skip if the target is not null
            }

            activeTask.addProgress(context.getAmount());
        }
    }
}
