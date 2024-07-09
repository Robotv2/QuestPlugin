package fr.robotv2.questplugin.quest.task.impl;

import fr.robotv2.questplugin.quest.task.Task;
import fr.robotv2.questplugin.quest.task.TaskTarget;
import fr.robotv2.questplugin.quest.task.TaskTargets;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationTaskTarget extends TaskTarget<Location> {

    static {
        TaskTargets.registerResolver(Location.class, LocationTaskTarget::new);
    }

    private final Location location;
    private final double distanceFromLocation;

    public LocationTaskTarget(Task task) {
        super(task, Location.class);

        final String world = task.getTaskSection().getString("required_location.world");
        final double x = task.getTaskSection().getDouble("required_location.x");
        final double y = task.getTaskSection().getDouble("required_location.y");
        final double z = task.getTaskSection().getDouble("required_location.z");

        if(world == null || Bukkit.getWorld(world) == null) {
            throw new NullPointerException("world");
        }

        this.location = new Location(Bukkit.getWorld(world), x, y, z);
        this.distanceFromLocation = Math.pow(task.getTaskSection().getDouble("required_location.distance_from_location", 5D), 2); // pow 2 so we can use Location#distanceSquared instead of Location#distance (more efficient)
    }

    @Override
    public boolean matchesValue(Location value) {

        final World world = location.getWorld();
        final World targetWorld = value.getWorld();

        if(world != targetWorld) {
            return false;
        }

        return value.distanceSquared(location) < distanceFromLocation;
    }
}
