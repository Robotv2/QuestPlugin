package fr.robotv2.questplugin.configurations;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

public class TitlesSection {

    public static final TitlesSection EMPTY = new TitlesSection(null);

    private final boolean enabled;

    private final String title;
    public final String subtitle;
    private final int fadeIn;
    private final int stay;
    private final int fadeOut;

    public TitlesSection(@Nullable ConfigurationSection section) {
        if(section == null) {
            this.enabled = false;
            this.title = null;
            this.subtitle = null;
            this.fadeIn = 0;
            this.stay = 0;
            this.fadeOut = 0;
        } else {
            this.enabled = section.getBoolean("enabled");
            this.title = section.getString("title");
            this.subtitle = section.getString("subtitle");
            this.fadeIn = section.getInt("fade-in", 10);
            this.stay = section.getInt("stay", 20);
            this.fadeOut = section.getInt("fade-out", 10);
        }
    }

    public boolean isEnabled() {
        return enabled;
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
}
