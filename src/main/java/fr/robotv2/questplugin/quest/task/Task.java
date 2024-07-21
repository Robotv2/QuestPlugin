package fr.robotv2.questplugin.quest.task;

import fr.robotv2.questplugin.quest.Quest;
import fr.robotv2.questplugin.quest.type.QuestType;
import org.bukkit.configuration.ConfigurationSection;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class Task {

    private final Quest parent;
    private final int id;
    private final ConfigurationSection section;

    private final QuestType<?> type;
    private final BigDecimal requiredAmount;
    private final List<String> rewards;

    private final transient TaskTarget<?> target;

    public Task(Quest quest, ConfigurationSection section) {
        this.parent = quest;
        this.id = Integer.parseInt(section.getName());
        this.section = section;
        this.type = Objects.requireNonNull(QuestType.getByLiteral(section.getString("task_type")), "Invalid task type.");
        this.requiredAmount = this.type.isNumerical() ? BigDecimal.valueOf(section.getDouble("required_amount")) : BigDecimal.ONE;
        this.rewards = section.getStringList("task_rewards");
        this.target = TaskTargets.resolve(this);
    }

    public Quest getParent() {
        return parent;
    }

    public int getTaskId() {
        return id;
    }

    public ConfigurationSection getTaskSection() {
        return section;
    }

    public BigDecimal getRequiredAmount() {
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

    public boolean isTarget(Object value) {
        return getTaskTarget() == null || getTaskTarget().isTarget(value);
    }

    @Override
    public String toString() {
        return "Task{" +
                "parent=" + parent +
                ", id='" + id + '\'' +
                ", type=" + type +
                ", rewards=" + rewards +
                ", requiredAmount=" + requiredAmount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
