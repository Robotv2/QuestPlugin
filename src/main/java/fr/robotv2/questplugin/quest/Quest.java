package fr.robotv2.questplugin.quest;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.group.QuestGroup;
import fr.robotv2.questplugin.quest.options.Optionnable;
import fr.robotv2.questplugin.quest.options.QuestOption;
import fr.robotv2.questplugin.quest.task.Task;
import fr.robotv2.questplugin.quest.type.QuestType;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Quest implements Optionnable {

    private final String questId;

    private final ConfigurationSection questSection;

    private final String questName;

    private final QuestGroup questGroup;

    private final QuestOption options;

    private final Map<Integer, Task> tasks = new TreeMap<>();

    public Quest(QuestPlugin plugin, ConfigurationSection section) {
        this(plugin, section.getName(), section);
    }

    public Quest(QuestPlugin plugin, String questId, ConfigurationSection section) {

        this.questId = questId;
        this.questSection = section;
        this.questName = section.getString("name");
        this.questGroup = Objects.requireNonNull(plugin.getQuestGroupManager().getGroup(section.getString("group")));
        this.options = new QuestOption(section.getConfigurationSection("options"));

        registerTasks(section.getConfigurationSection("tasks"));
    }

    public String getQuestId() {
        return questId;
    }

    public ConfigurationSection getQuestSection() {
        return questSection;
    }

    public String getQuestName() {
        return questName;
    }

    @UnmodifiableView
    public Collection<Task> getTasks() {
        return Collections.unmodifiableCollection(tasks.values());
    }

    @UnmodifiableView
    public Collection<Task> getTasks(QuestType<?> type) {
        return getTasks().stream().filter(task -> Objects.equals(type, task.getType())).collect(Collectors.toSet());
    }

    @Nullable
    public Task getTask(int taskId) {
        return tasks.get(taskId);
    }

    @NotNull
    public QuestGroup getQuestGroup() {
        return questGroup;
    }

    @Override
    public boolean isOptionSet(Option option) {
        return options.isOptionSet(option);
    }

    @Override
    public boolean getOptionValue(Option option) {
        if(isOptionSet(option)) {
            return options.getOptionValue(option);
        } else if (getQuestGroup().getOption().isOptionSet(option)) {
            return getQuestGroup().getOption().getOptionValue(option);
        } else {
            return Optionnable.DEFAULT.get(option);
        }
    }

    private void registerTasks(ConfigurationSection tasksSection) {

        if(tasksSection == null) {
            return;
        }

        for(String taskId : tasksSection.getKeys(false)) {
            final ConfigurationSection taskSection = Objects.requireNonNull(tasksSection.getConfigurationSection(taskId));
            final Task task = new Task(this, taskSection);
            tasks.put(Integer.parseInt(taskId), task);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quest)) return false;
        Quest quest = (Quest) o;
        return Objects.equals(questId, quest.questId) && Objects.equals(questGroup, quest.questGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questId, questGroup);
    }
}
