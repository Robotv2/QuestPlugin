package fr.robotv2.questplugin.quest.task;

import fr.robotv2.questplugin.quest.Quest;
import fr.robotv2.questplugin.quest.type.QuestType;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class Task {

    private final Quest parent;
    private final String id;
    private final ConfigurationSection section;

    private final int requiredAmount;
    private final QuestType<?> type;
    private final List<String> rewards;

    private final transient TaskTarget<?> target;

    public Task(Quest quest, ConfigurationSection section) {
        this.parent = quest;
        this.id = section.getName();
        this.section = section;
        this.requiredAmount = section.getInt("required_amount");
        this.type = QuestType.getByLiteral(section.getString("task_type"));
        this.rewards = section.getStringList("task_rewards");
        this.target = TaskTargets.resolve(this);
    }

    public Quest getParent() {
        return parent;
    }

    public String getTaskId() {
        return id;
    }

    public ConfigurationSection getTaskSection() {
        return section;
    }

    public int getRequiredAmount() {
        return requiredAmount;
    }

    public QuestType<?> getType() {
        return type;
    }

    public List<String> getRewards() {
        return rewards;
    }

    public TaskTarget<?> getTaskTarget() {
        return target;
    }
}
