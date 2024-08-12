package fr.robotv2.questplugin.configurations;

import com.cryptomorin.xseries.messages.ActionBar;
import fr.robotv2.questplugin.api.configurations.PlayerSendable;
import fr.robotv2.questplugin.util.placeholder.PlaceholderSupport;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class ActionBarsSection extends PlaceholderSupport<ActionBarsSection> implements PlayerSendable {

    public static final ActionBarsSection EMPTY = new ActionBarsSection(null);

    private final boolean enabled;
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

    @ApiStatus.Internal
    protected ActionBarsSection(boolean enabled, @Nullable String message) {
        this.enabled = enabled;
        this.message = message;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void send(Player player) {

        if(!isEnabled()) {
            return;
        }

        ActionBar.sendActionBar(player, getMessage());
    }

    @Override
    @Contract("_ -> new")
    public ActionBarsSection apply(Function<String, String> replaceFunction) {
        return new ActionBarsSection(enabled, message != null ? replaceFunction.apply(message) : null);
    }
}
