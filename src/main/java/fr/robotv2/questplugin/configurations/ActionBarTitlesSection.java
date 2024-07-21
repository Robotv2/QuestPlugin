package fr.robotv2.questplugin.configurations;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

public class ActionBarTitlesSection {

    private final ActionBarsSection actionBarsSection;
    private final TitlesSection titlesSection;

    public ActionBarTitlesSection(@Nullable ConfigurationSection section) {
        if(section == null) {
            this.actionBarsSection = ActionBarsSection.EMPTY;
            this.titlesSection = TitlesSection.EMPTY;
        } else {
            this.actionBarsSection = new ActionBarsSection(section.getConfigurationSection("action_bars"));
            this.titlesSection = new TitlesSection(section.getConfigurationSection("titles"));
        }
    }

    public ActionBarsSection getActionBarsSection() {
        return actionBarsSection;
    }

    public TitlesSection getTitlesSection() {
        return titlesSection;
    }
}
