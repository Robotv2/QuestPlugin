package fr.robotv2.questplugin.group;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.api.cron.ICronJob;
import fr.robotv2.questplugin.api.group.IQuestGroup;
import fr.robotv2.questplugin.cron.CronJob;
import fr.robotv2.questplugin.quest.options.QuestOption;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public class QuestGroup implements IQuestGroup {

    private final String groupId;

    private final QuestOption option;

    private final String cronSyntax;

    private final ICronJob cronJob;

    private final int globalAssignation;

    private final Map<String, Integer> assignations;

    public QuestGroup(final ConfigurationSection section) {
        this(section.getName(), section);
    }

    public QuestGroup(final String groupId, final ConfigurationSection section) {
        this.groupId = groupId;

        this.option = new QuestOption(section.getConfigurationSection("options"));
        this.globalAssignation = section.getInt("global-assignation");

        this.cronSyntax = section.getString("automatic-reset");
        if(this.cronSyntax != null) {
            this.cronJob = new CronJob(this.cronSyntax, () -> QuestPlugin.instance().getResetHandler().reset(this));
            this.cronJob.prepare();
            this.cronJob.start();
        } else {
            this.cronJob = null;
        }

        if(section.isConfigurationSection("assignations")) {
            this.assignations = new HashMap<>();
            final ConfigurationSection assignationSection = section.getConfigurationSection("assignations");
            assignationSection.getKeys(false).forEach((key) -> this.assignations.put(key.toLowerCase(), assignationSection.getInt(key)));
        } else {
            this.assignations = Collections.emptyMap();
        }
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    @Override
    public QuestOption getOption() {
        return option;
    }

    @Override
    public String getCronSyntax() {
        return cronSyntax;
    }

    @Override
    public @Nullable ICronJob getCronJob() {
        return cronJob;
    }

    @Override
    public int getGlobalAssignation() {
        return globalAssignation;
    }

    @Override
    public OptionalInt getRoleAssignation(String role) {
        return assignations.containsKey(role.toLowerCase()) ? OptionalInt.of(assignations.get(role.toLowerCase())) : OptionalInt.empty();
    }

    @Override
    public @UnmodifiableView Map<String, Integer> getRoleAssignations() {
        return Collections.unmodifiableMap(assignations);
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
