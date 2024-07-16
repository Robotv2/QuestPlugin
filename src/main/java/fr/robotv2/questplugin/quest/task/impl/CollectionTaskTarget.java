package fr.robotv2.questplugin.quest.task.impl;

import fr.robotv2.questplugin.quest.task.Task;
import fr.robotv2.questplugin.quest.task.TaskTarget;
import fr.robotv2.questplugin.quest.task.TaskTargets;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class CollectionTaskTarget<T> extends TaskTarget<T> {

    static {
        TaskTargets.registerResolver(String.class, (task) -> new CollectionTaskTarget<>(task, String.class, (string) -> string, Collectors.toCollection(HashSet::new)));
    }

    private final Collection<T> target;
    private final boolean matchAll;

    public CollectionTaskTarget(Task task, Class<T> tClass, Function<String, T> stringTFunction, Collector<T, ?, Collection<T>> collector) {
        super(task, tClass);
        final List<String> targets = task.getTaskSection().getStringList("required_targets");
        this.matchAll = targets.contains("*");
        this.target = targets.stream().filter((string) -> !string.equals("*")).map(stringTFunction).collect(collector);
    }

    @Override
    protected boolean matchesValue(T value) {
        return matchAll || target.contains(value);
    }
}
