package fr.robotv2.questplugin.group;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.configurations.cosmetics.CosmeticMap;
import fr.robotv2.questplugin.configurations.cosmetics.Cosmeticable;
import fr.robotv2.questplugin.cron.CronJob;
import fr.robotv2.questplugin.quest.options.QuestOption;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public class QuestGroup implements Cosmeticable {

    private final String groupId;

    private final QuestOption option;

    private final String cronSyntax;

    private final CronJob cronJob;

    private final int globalAssignation;

    private final Map<String, Integer> assignations;

    private final CosmeticMap cosmetics;

    private final int maxRerolls;

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

        this.cosmetics = new CosmeticMap(section.getConfigurationSection("cosmetics"));
        this.maxRerolls = section.getInt("max-rerolls", 0);
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

    public @Nullable CronJob getCronJob() {
        return cronJob;
    }

    public void stopCronJob() {
        if(cronJob != null) {
            cronJob.stop();
        }
    }

    public long getNextReset() {
        return cronJob != null ? cronJob.getNextExecution() : -1;
    }

    public int getGlobalAssignation() {
        return globalAssignation;
    }

    public OptionalInt getRoleAssignation(String role) {
        return assignations.containsKey(role.toLowerCase()) ? OptionalInt.of(assignations.get(role.toLowerCase())) : OptionalInt.empty();
    }

    public @UnmodifiableView Map<String, Integer> getRoleAssignations() {
        return Collections.unmodifiableMap(assignations);
    }

    public int getMaxRerolls() {
        return maxRerolls;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuestGroup group)) return false;
        return Objects.equals(groupId, group.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(groupId);
    }

    @Override
    public CosmeticMap getCosmeticMap() {
        return cosmetics;
    }
}
