package fr.robotv2.questplugin.configurations.cosmetics.impl;

import com.cryptomorin.xseries.messages.Titles;
import fr.robotv2.questplugin.configurations.cosmetics.PlayerSendable;
import fr.robotv2.questplugin.util.placeholder.PlaceholderSupport;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class TitlesSection extends PlaceholderSupport<TitlesSection> implements PlayerSendable {

    public static final TitlesSection EMPTY = new TitlesSection(null);

    private final boolean enabled;

    private final String title;
    public final String subtitle;
    private final int fadeIn;
    private final int stay;
    private final int fadeOut;

    private final boolean empty;

    public TitlesSection(@Nullable ConfigurationSection section) {
        if(section == null) {
            this.enabled = false;
            this.title = null;
            this.subtitle = null;
            this.fadeIn = 0;
            this.stay = 0;
            this.fadeOut = 0;
            this.empty = true;
        } else {
            this.enabled = section.getBoolean("enabled");
            this.title = section.getString("title");
            this.subtitle = section.getString("subtitle");
            this.fadeIn = section.getInt("fade-in", 10);
            this.stay = section.getInt("stay", 20);
            this.fadeOut = section.getInt("fade-out", 10);
            this.empty = false;
        }
    }

    @ApiStatus.Internal
    protected TitlesSection(boolean enabled, @Nullable String title, @Nullable String subtitle, int fadeIn, int stay, int fadeOut, boolean empty) {
        this.enabled = enabled;
        this.title = title;
        this.subtitle = subtitle;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
        this.empty = empty;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isEmpty() {
        return empty;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public int getFadeIn() {
        return fadeIn;
    }

    public int getStay() {
        return stay;
    }

    public int getFadeOut() {
        return fadeOut;
    }

    @Override
    public void send(Player player) {

        if(!isEnabled()) {
            return;
        }

        Titles.sendTitle(player, getFadeIn(), getStay(), getFadeOut(), getTitle(), getSubtitle());
    }

    @Override
    @Contract("_ -> new")
    public TitlesSection apply(Function<String, String> replaceFunction) {
        return new TitlesSection(
                enabled,
                title != null ? replaceFunction.apply(title) : null,
                subtitle != null ? replaceFunction.apply(subtitle) : null,
                fadeIn,
                stay,
                fadeOut,
                empty
        );
    }
}
