package fr.robotv2.questplugin.quest.options;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public interface Optionnable {

    Map<Option, Boolean> DEFAULT = createDefaultMap();

    static Map<Option, Boolean> createDefaultMap() {
        Map<Option, Boolean> defaultMap = new EnumMap<>(Option.class);
        defaultMap.put(Option.DEPENDANT_TASK, Boolean.FALSE);
        defaultMap.put(Option.GLOBAL, Boolean.FALSE);
        defaultMap.put(Option.NEED_STARTING, Boolean.FALSE);
        defaultMap.put(Option.AUTOMATICALLY_GIVEN, Boolean.TRUE);
        return Collections.unmodifiableMap(defaultMap);
    }

    /**
     * Gets the default value for the specified option.
     *
     * @param option the option to retrieve the default value for
     * @return the default value of the option
     */
    static boolean getDefault(Option option) {
        return DEFAULT.get(option);
    }

    enum Option {
        DEPENDANT_TASK, // group & individual
        NEED_STARTING, // group & individual
        GLOBAL, // group only
        AUTOMATICALLY_GIVEN, // group only
        ;
    }

    /**
     * Checks if the specified option is set.
     *
     * @param option the option to check
     * @return true if the option is set, false otherwise
     */
    boolean isOptionSet(Option option);

    /**
     * Gets the value of the specified option.
     *
     * @param option the option to retrieve the value for
     * @return the value of the option
     */
    boolean getOptionValue(Option option);
}
