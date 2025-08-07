package fr.robotv2.questplugin.configurations.cosmetics;

import java.util.EnumSet;
import java.util.Set;

public enum CosmeticType {

    QUEST_DONE,
    TASK_INCREMENT,
    TASK_DONE,
    ;

    public static final Set<CosmeticType> VALUES = EnumSet.allOf(CosmeticType.class);
}
