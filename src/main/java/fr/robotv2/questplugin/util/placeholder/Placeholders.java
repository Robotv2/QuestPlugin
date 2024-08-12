package fr.robotv2.questplugin.util.placeholder;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.quest.Quest;
import fr.robotv2.questplugin.quest.task.Task;
import fr.robotv2.questplugin.storage.model.ActiveQuest;
import fr.robotv2.questplugin.storage.model.ActiveTask;
import fr.robotv2.questplugin.util.placeholder.impl.ValuePlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;

public class Placeholders {

    public static ValuePlaceholder<Quest> QUEST_PLACEHOLDER = (text, value) -> {
        if(text == null || text.isEmpty() || value == null) return text;
        return text
                .replace("%quest_id%", value.getQuestId())
                .replace("%quest_name%", value.getQuestName())
                .replace("%quest_group%", value.getQuestGroup().getGroupId())
                .replace("%quest_tasks%", String.valueOf(value.getTasks().size()))
        ;
    };

    public static ValuePlaceholder<Task> TASK_PLACEHOLDER = (text, value) -> {
        if(text == null || text.isEmpty() || value == null) return text;
        return QUEST_PLACEHOLDER.apply(text, value.getParent())
                .replace("%task_id%", String.valueOf(value.getTaskId()))
                .replace("%task_type%", value.getType().getLiteral())
                .replace("%task_required%", value.getRequiredAmount().toString())
        ;
    };

    public static ValuePlaceholder<ActiveQuest> ACTIVE_QUEST_PLACEHOLDER = (text, value) -> {
        if(text == null || text.isEmpty() || value == null) return text;
        final Quest quest = QuestPlugin.instance().getQuestManager().fromId(value.getQuestId(), value.getGroupId());
        return QUEST_PLACEHOLDER.apply(text, quest)
                .replace("%quest_player%", Optional.ofNullable(Bukkit.getPlayer(value.getOwner())).map(Player::getName).orElse("Unknown."))
                .replace("%quest_started%", String.valueOf(value.isStarted()))
                .replace("%quest_done%", String.valueOf(value.isDone()))
                .replace("%quest_tasks_completed%", String.valueOf(value.getTasks().stream().filter(ActiveTask::isDone).count()))
                .replace("%quest_reset_timestamp%", String.valueOf(value.getNextReset()))
                ;
    };

    public static ValuePlaceholder<ActiveTask> ACTIVE_TASK_PLACEHOLDER = (text, value) -> {
        if(text == null || text.isEmpty() || value == null) return text;
        final Task task = Optional.ofNullable(QuestPlugin.instance().getQuestManager().fromId(value.getParentQuestId(), value.getParentQuestGroupId()))
                .map((quest) -> quest.getTask(value.getTaskId()))
                .orElse(null);
        return TASK_PLACEHOLDER.apply(text, task)
                .replace("%task_progress%", value.getProgress().toString())
                .replace("%task_done%", String.valueOf(value.isDone()))
                ;
    };
}
