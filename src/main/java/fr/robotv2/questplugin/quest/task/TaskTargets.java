package fr.robotv2.questplugin.quest.task;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class TaskTargets {

    private final static Map<Class<?>, Function<Task, TaskTarget<?>>> resolvers = new HashMap<>();

    public static TaskTarget<?> resolve(@NotNull Task task) {
        return Objects.requireNonNull(resolvers.get(task.getType().getRequiredClass())).apply(task);
    }

    public static void registerResolver(Class<?> tClass, Function<Task, TaskTarget<?>> resolver) {
        resolvers.put(tClass, resolver);
    }
}
