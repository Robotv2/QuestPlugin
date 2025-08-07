package fr.robotv2.questplugin.quest.options;

public interface Optionnable {

    enum Option {
        DEPENDANT_TASK(false), // group & individual
        NEED_STARTING(false), // group & individual
        AUTOMATICALLY_GIVEN(true), // group only
        REPEATABLE(false), // group & individual
        ;

        private final boolean defaultValue;

        Option(boolean defaultValue) {
            this.defaultValue = defaultValue;
        }

        public boolean getDefaultValue() {
            return defaultValue;
        }
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
