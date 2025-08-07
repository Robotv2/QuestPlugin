package fr.robotv2.questplugin.quest;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.conditions.Condition;
import fr.robotv2.questplugin.configurations.cosmetics.CosmeticMap;
import fr.robotv2.questplugin.configurations.cosmetics.Cosmeticable;
import fr.robotv2.questplugin.group.QuestGroup;
import fr.robotv2.questplugin.quest.options.Optionnable;
import fr.robotv2.questplugin.quest.options.QuestOption;
import fr.robotv2.questplugin.quest.task.Task;
import fr.robotv2.questplugin.quest.type.QuestType;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.stream.Collectors;

public class Quest implements Optionnable, Cosmeticable {

    private final QuestPlugin plugin;

    private final String questId;

    private final ConfigurationSection questSection;

    private final String questName;

    private final QuestGroup questGroup;

    private final QuestOption options;

    private final Map<Integer, Task> tasks = new TreeMap<>();

    private final Set<Condition> conditions = new HashSet<>();

    private final CosmeticMap cosmetics;

    private final List<String> rewards;

    public Quest(QuestPlugin plugin, ConfigurationSection section) {
        this(plugin, section.getName(), section);
    }

    public Quest(QuestPlugin plugin, String questId, ConfigurationSection section) {
        this.plugin = plugin;
        this.questId = questId;
        this.questSection = section;
        this.questName = section.getString("name");
        this.questGroup = Objects.requireNonNull(plugin.getQuestGroupManager().getGroup(section.getString("group")));
        this.options = new QuestOption(section.getConfigurationSection("options"));
        this.cosmetics = new CosmeticMap(section.getConfigurationSection("cosmetics"));
        this.rewards = section.getStringList("rewards");

        registerTasks(section.getConfigurationSection("tasks"));
        registerConditions(section.getConfigurationSection("conditions"));
    }

    public QuestPlugin getPlugin() {
        return plugin;
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

    public Set<Condition> getConditions() {
        return Collections.unmodifiableSet(conditions);
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
            return option.getDefaultValue();
        }
    }

    @Override
    public CosmeticMap getCosmeticMap() {
        return cosmetics;
    }

    public List<String> getRewards() {
        return rewards;
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

    private void registerConditions(ConfigurationSection conditionsSection) {

        conditions.clear();

        if(conditionsSection == null) {
            return;
        }

        for(String key : conditionsSection.getKeys(false)) {
            plugin.getConditionManager().toInstance(conditionsSection, key).ifPresent(conditions::add);
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
