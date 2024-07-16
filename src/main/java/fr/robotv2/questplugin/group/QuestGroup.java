package fr.robotv2.questplugin.group;

import fr.robotv2.questplugin.quest.options.QuestOption;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Objects;

public class QuestGroup {

    private final String groupId;

    private final QuestOption option;

    public QuestGroup(final ConfigurationSection section) {
        this(section.getName(), section);
    }

    public QuestGroup(final String groupId, final ConfigurationSection section) {
        this.groupId = groupId;
        this.option = new QuestOption(section);
    }

    public String getGroupId() {
        return groupId;
    }

    public QuestOption getOption() {
        return option;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuestGroup)) return false;
        QuestGroup group = (QuestGroup) o;
        return Objects.equals(groupId, group.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(groupId);
    }
}
