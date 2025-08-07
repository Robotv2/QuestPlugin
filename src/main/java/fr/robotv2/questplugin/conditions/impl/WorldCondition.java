package fr.robotv2.questplugin.conditions.impl;

import fr.robotv2.questplugin.conditions.Condition;
import fr.robotv2.questplugin.quest.context.RunningQuestContext;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.Set;

public class WorldCondition implements Condition {

    private final Set<String> worlds;

    public WorldCondition(String key, ConfigurationSection parent) {
        this.worlds = new HashSet<>(parent.getStringList(key));
    }

    @Override
    public boolean isMet(RunningQuestContext<?, ?> context) {
        final String worldName = context.getInitiator().getWorld().getName();
        return worlds.contains(worldName);
    }
}
