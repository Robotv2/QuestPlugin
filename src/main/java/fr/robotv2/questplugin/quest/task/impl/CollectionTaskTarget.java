package fr.robotv2.questplugin.quest.task.impl;

import com.cryptomorin.xseries.XTag;
import fr.robotv2.questplugin.quest.task.Task;
import fr.robotv2.questplugin.quest.task.TaskTarget;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;

public class CollectionTaskTarget<T> extends TaskTarget<T> {

    private final Collection<T> allowed;
    private final Collection<T> disallowed;

    private final boolean matchAll;

    public CollectionTaskTarget(Task task, Class<T> tClass, Function<String, T> stringTFunction, Collector<T, ?, Collection<T>> collector) {
        super(tClass);

        final List<String> targets;
        if(task.getTaskSection().isString("required_target")) {
            targets = List.of(task.getTaskSection().getString("required_target"));
        } else if(task.getTaskSection().contains("required_targets")) {
            targets = task.getTaskSection().getStringList("required_targets");
        } else {
            targets = List.of();
        }

        this.matchAll = targets.contains("*");

        final Set<T> tempAllowed = new HashSet<>();
        final Set<T> tempDisallowed = new HashSet<>();

        for(String raw : targets) {

            if(raw == null) continue;
            final String value = raw.trim();
            if (value.isEmpty() || value.equals("*")) continue;

            if(raw.startsWith("!")) {
                populate(value.substring(1).trim(), stringTFunction, tempDisallowed);
            } else {
                populate(value, stringTFunction, tempAllowed);
            }
        }

        this.allowed = tempAllowed.stream().collect(collector);
        this.disallowed = tempDisallowed.stream().collect(collector);
    }

    private void populate(String value, Function<String, T> function, Collection<T> collection) {
        final Collection<T> fetched = fetch(value, function);
        if(!fetched.isEmpty()) {
            collection.addAll(fetched);
        }
    }

    private Collection<T> fetch(String value, Function<String, T> stringTFunction) {
        final Collection<T> result = new HashSet<>();
        if(value.startsWith("TAG:")) {
            final String tagName = value.substring(4);
            final Optional<XTag<?>> tag = XTag.getTag(tagName);
            tag.ifPresent((t) -> {
                for(Object tagValue : t.getValues()) {
                    if(getTargetClass().isInstance(tagValue)) {
                        result.add(getTargetClass().cast(tagValue));
                    }
                }
            });
        } else {
            final T target = stringTFunction.apply(value);
            if(target != null) result.add(target);
        }

        return result;
    }

    @Override
    protected boolean matchesValue(T value) {
        return (matchAll && !disallowed.contains(value)) || allowed.contains(value);
    }
}
