package fr.robotv2.questplugin.api.group;

import fr.robotv2.questplugin.api.cron.ICronJob;
import fr.robotv2.questplugin.quest.options.QuestOption;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Map;
import java.util.OptionalInt;

public interface IQuestGroup {

    String getGroupId();

    QuestOption getOption();

    String getCronSyntax();

    @Nullable
    ICronJob getCronJob();

    default void stopCronJob() {
        if(getCronJob() != null) getCronJob().stop();
    }

    default long getNextReset() {
        return getCronJob() != null ? getCronJob().getNextExecution() : -1;
    }

    int getGlobalAssignation();

    OptionalInt getRoleAssignation(String role);

    @UnmodifiableView
    Map<String, Integer> getRoleAssignations();
}
