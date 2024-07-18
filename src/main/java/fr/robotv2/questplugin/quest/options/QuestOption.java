package fr.robotv2.questplugin.quest.options;

import org.bukkit.configuration.ConfigurationSection;

import java.util.EnumMap;
import java.util.Map;

public class QuestOption implements Optionnable {

    private final Map<Option, Boolean> options = new EnumMap<>(Option.class);

    public QuestOption(final ConfigurationSection section) {

        if(section == null) {
            return;
        }

        if(section.isBoolean("dependant-tasks")) {
            options.put(Option.DEPENDANT_TASK, section.getBoolean("dependant-tasks"));
        }

//        if(section.isBoolean("global")) {
//            options.put(Option.GLOBAL, section.getBoolean("global"));
//        }

        if(section.isBoolean("automatically-given")) {
            options.put(Option.AUTOMATICALLY_GIVEN, section.getBoolean("automatically-given"));
        }

        if(section.isBoolean("need-starting")) {
            options.put(Option.NEED_STARTING, section.getBoolean("need-starting"));
        }
    }

    @Override
    public boolean isOptionSet(Option option) {
        return options.containsKey(option);
    }

    @Override
    public boolean getOptionValue(Option option) {
        return isOptionSet(option) ? options.get(option) : DEFAULT.get(option);
    }
}
