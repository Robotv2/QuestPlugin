package fr.robotv2.questplugin.conditions;

import fr.robotv2.questplugin.quest.context.RunningQuestContext;

public interface Condition {
    String getKey();
    boolean isMet(RunningQuestContext<?, ?> context);
}
