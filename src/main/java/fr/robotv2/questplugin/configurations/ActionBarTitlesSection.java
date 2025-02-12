package fr.robotv2.questplugin.configurations;

import fr.robotv2.questplugin.util.placeholder.PlaceholderSupport;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class ActionBarTitlesSection extends PlaceholderSupport<ActionBarTitlesSection> implements PlayerSendable {

    private final ActionBarsSection actionBarsSection;
    private final TitlesSection titlesSection;

    public ActionBarTitlesSection(@Nullable ConfigurationSection section) {
        if(section == null) {
            this.actionBarsSection = ActionBarsSection.EMPTY;
            this.titlesSection = TitlesSection.EMPTY;
        } else {
            this.actionBarsSection = new ActionBarsSection(section.getConfigurationSection("action_bar"));
            this.titlesSection = new TitlesSection(section.getConfigurationSection("titles"));
        }
    }

    @ApiStatus.Internal
    protected ActionBarTitlesSection(ActionBarsSection actionBarsSection, TitlesSection titlesSection) {
        this.actionBarsSection = actionBarsSection;
        this.titlesSection = titlesSection;
    }

    public ActionBarsSection getActionBarsSection() {
        return actionBarsSection;
    }

    public TitlesSection getTitlesSection() {
        return titlesSection;
    }

    @Override
    public void send(Player player) {
        getActionBarsSection().send(player);
        getTitlesSection().send(player);
    }

    @Override
    @Contract("_ -> new")
    public ActionBarTitlesSection apply(Function<String, String> replaceFunction) {
        return new ActionBarTitlesSection(actionBarsSection.apply(replaceFunction), titlesSection.apply(replaceFunction));
    }
}
