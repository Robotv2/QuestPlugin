package fr.robotv2.questplugin.quest.task;

import com.cryptomorin.xseries.XEntityType;
import com.cryptomorin.xseries.XMaterial;
import fr.robotv2.questplugin.quest.task.impl.CollectionTaskTarget;
import fr.robotv2.questplugin.quest.task.impl.EnumCollectionTaskTarget;
import fr.robotv2.questplugin.quest.task.impl.LocationTaskTarget;
import fr.robotv2.questplugin.quest.task.impl.VoidTaskTarget;
import fr.robotv2.questplugin.util.XSeriesUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TaskTargets {

    private final static Map<Class<?>, Function<Task, TaskTarget<?>>> resolvers = new HashMap<>();

    static {
        registerDefaultResolvers();
    }

    public static TaskTarget<?> resolve(@NotNull Task task) {
        final Class<?> requiredClass = task.getType().getRequiredClass();
        final Function<Task, TaskTarget<?>> resolver = resolvers.get(requiredClass);

        if (resolver == null) {
            throw new NullPointerException("No resolver found for class: " + requiredClass.getName());
        }

        return resolver.apply(task);
    }

    public static void registerResolver(Class<?> tClass, Function<Task, TaskTarget<?>> resolver) {
        resolvers.put(tClass, resolver);
    }

    public static void registerDefaultResolvers() {
        TaskTargets.registerResolver(String.class, (task) -> new CollectionTaskTarget<>(task, String.class, (string) -> string, Collectors.toCollection(HashSet::new)));
        TaskTargets.registerResolver(XMaterial.class, (task) -> new EnumCollectionTaskTarget<>(task, XMaterial.class, XSeriesUtil::matchMaterialOrThrow));
        TaskTargets.registerResolver(Material.class, (task) -> new EnumCollectionTaskTarget<>(task, Material.class));
        TaskTargets.registerResolver(EntityType.class, (task) -> new EnumCollectionTaskTarget<>(task, EntityType.class));
        TaskTargets.registerResolver(XEntityType.class, (task) -> new EnumCollectionTaskTarget<>(task, XEntityType.class, XSeriesUtil::matchEntityTypeOrThrow));
        TaskTargets.registerResolver(EntityDamageEvent.DamageCause.class, (task) -> new EnumCollectionTaskTarget<>(task, EntityDamageEvent.DamageCause.class));
        TaskTargets.registerResolver(Location.class, LocationTaskTarget::new);
        TaskTargets.registerResolver(Void.class, VoidTaskTarget::new);
    }
}
