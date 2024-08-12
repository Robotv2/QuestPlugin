package fr.robotv2.questplugin.quest.task.impl;

import fr.robotv2.questplugin.quest.task.Task;

import java.util.EnumSet;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EnumCollectionTaskTarget<T extends Enum<T>> extends CollectionTaskTarget<T> {

    public EnumCollectionTaskTarget(Task task, Class<T> tClass) {
        this(task, tClass, (target) -> Enum.valueOf(tClass, target));
    }

    public EnumCollectionTaskTarget(Task task, Class<T> tClass, Function<String, T> stringTFunction) {
        super(task, tClass, stringTFunction, Collectors.toCollection(() -> EnumSet.noneOf(tClass)));
    }
}
