package fr.robotv2.questplugin.util;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class GroupUtil {

    private static Permission PERMISSION;
    private static boolean initialized = false;

    public static boolean initialize(JavaPlugin plugin) {
        if(initialized) {
            return true;
        }

        final RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);

        if(rsp == null) {
            return false;
        }

        PERMISSION = rsp.getProvider();
        return (initialized = true);
    }

    public static String getPlayerPrimaryGroup(OfflinePlayer offlinePlayer) {
        if(!initialized) {
            return "default";
        }

        try {
            return PERMISSION.getPrimaryGroup(null, offlinePlayer);
        } catch (UnsupportedOperationException exception) {

            // this is a fallback for plugins that don't support the getPrimaryGroup method

            return "default";
        }
    }
}
