package fr.robotv2.questplugin.group;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.cron.CronJob;
import fr.robotv2.questplugin.quest.options.QuestOption;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class QuestGroup {

    private final String groupId;

    private final QuestOption option;

    private final String cronSyntax;

    @Nullable private CronJob cronJob;

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
        }

        if(section.isConfigurationSection("assignations")) {
            this.assignations = new HashMap<>();
            final ConfigurationSection assignationSection = section.getConfigurationSection("assignations");
            assignationSection.getKeys(false).forEach((key) -> this.assignations.put(key.toLowerCase(), assignationSection.getInt(key)));
        } else {
            this.assignations = Collections.emptyMap();
        }
    }

    public String getGroupId() {
        return groupId;
    }

    public QuestOption getOption() {
        return option;
    }

    public String getCronSyntax() {
        return cronSyntax;
    }

    @Nullable
    public CronJob getCronJob() {
        return cronJob;
    }

    public void stopCronJob() {
        if(getCronJob() != null) {
            getCronJob().stop();
        }
    }

    public long getNextReset() {
        return getCronJob() != null ? getCronJob().getNextExecution() : -1;
    }

    public int getGlobalAssignation() {
        return globalAssignation;
    }

    public OptionalInt getRoleAssignation(String role) {
        return assignations.containsKey(role.toLowerCase()) ? OptionalInt.of(assignations.get(role.toLowerCase())) : OptionalInt.empty();
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
