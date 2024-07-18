package fr.robotv2.questplugin.command;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.group.QuestGroup;
import fr.robotv2.questplugin.quest.Quest;
import fr.robotv2.questplugin.storage.model.ActiveQuest;
import fr.robotv2.questplugin.storage.model.QuestPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"betterdailyquest", "bdq"})
public class QuestPluginMainCommand {

    private final QuestPlugin plugin;

    public QuestPluginMainCommand(QuestPlugin plugin) {
        this.plugin = plugin;
    }

    @DefaultFor("rtq|robottimedquest")
    public void onDefault(BukkitCommandActor actor) {
        actor.getSender().sendMessage(ChatColor.GREEN + "This server is using BetterDailyQuest with version " + plugin.getDescription().getVersion() + ".");
    }

    @Subcommand("reload")
    @CommandPermission("betterdailyquest.command.reload")
    public void onReload(BukkitCommandActor actor) {
        plugin.onReload();
        actor.getSender().sendMessage(ChatColor.GREEN + "The plugin has been reloaded successfully.");
    }

    @Subcommand("give")
    @CommandPermission("betterdailyquest.command.give")
    public void onGive(BukkitCommandActor actor, QuestGroup group, Quest quest, @Default("me") Player target) {
        final QuestPlayer questPlayer = plugin.getDatabaseManager().getCachedQuestPlayer(target);

        if(questPlayer == null) {
            actor.getSender().sendMessage(ChatColor.RED + "The player is not connected or is not loaded.");
            return;
        }

        if(questPlayer.hasQuest(quest)) {
            actor.getSender().sendMessage(ChatColor.RED + "The target already has this quest.");
            return;
        }

        questPlayer.addActiveQuest(new ActiveQuest(target, quest));
        actor.getSender().sendMessage(ChatColor.GREEN + "The quest '" + quest.getQuestId() + "' has been successfully been given to the player '" + target.getName() + "'.");
    }
}
