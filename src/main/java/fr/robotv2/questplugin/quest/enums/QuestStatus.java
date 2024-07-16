package fr.robotv2.questplugin.quest.enums;

import java.util.EnumSet;
import java.util.Set;

public enum QuestStatus {

    NOT_STARTED,
    STARTED,
    DONE,
    ;

    public static final Set<QuestStatus> VALUES = EnumSet.allOf(QuestStatus.class);
}
