package fr.robotv2.questplugin.configurations;

import com.cryptomorin.xseries.messages.ActionBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class ActionBarsSection {

    public static final ActionBarsSection EMPTY = new ActionBarsSection(null);

    private boolean enabled;
    private final String message;

    public ActionBarsSection(@Nullable ConfigurationSection section) {
        if(section == null) {
            this.enabled = false;
            this.message = null;
        } else {
            this.enabled = section.getBoolean("enabled");
            this.message = section.getString("message");
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getMessage() {
        return message;
    }

    public void send(Player player) {

        if(!isEnabled()) {
            return;
        }

        ActionBar.sendActionBar(player, getMessage());
    }
}
