package fr.robotv2.questplugin.command;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.group.QuestGroup;
import fr.robotv2.questplugin.quest.Quest;
import fr.robotv2.questplugin.storage.model.ActiveQuest;
import fr.robotv2.questplugin.storage.model.QuestPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"betterdailyquest", "bdq"})
public class QuestPluginMainCommand {

    private final QuestPlugin plugin;

    public QuestPluginMainCommand(QuestPlugin plugin) {
        this.plugin = plugin;
    }

    @DefaultFor({"betterdailyquest", "bdq"})
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
        actor.getSender().sendMessage(ChatColor.GREEN + "The quest '" + quest.getQuestId() + "' has successfully been given to the player '" + target.getName() + "'.");
    }

    @Subcommand("reroll")
    @CommandPermission("betterdailyquest.command.reroll")
    public void onReroll(BukkitCommandActor actor, QuestGroup group, Quest quest, @Default("me") Player target) {
        final QuestPlayer questPlayer = plugin.getDatabaseManager().getCachedQuestPlayer(target);

        if(questPlayer == null) {
            actor.getSender().sendMessage(ChatColor.RED + "The player is not connected or is not loaded.");
            return;
        }

        if(!questPlayer.hasQuest(quest)) {
            actor.getSender().sendMessage(ChatColor.RED + "The target does not have this quest.");
            return;
        }

        if (actor.isPlayer() && !actor.requirePlayer().hasPermission("betterdailyquest.command.reroll.others")) {
            actor.getSender().sendMessage(ChatColor.RED + "You do not have permission to reroll quests for other players.");
            return;
        }

        final Quest newQuest = plugin.getQuestManager().getRandomQuest(questPlayer, group);
        if(newQuest == null) {
            actor.getSender().sendMessage(ChatColor.RED + "No quest available for reroll.");
            return;
        }

        questPlayer.removeActiveQuest(quest);
        questPlayer.addActiveQuest(new ActiveQuest(target, newQuest));
        actor.getSender().sendMessage(ChatColor.GREEN + "The quest '" + quest.getQuestId() + "' has successfully been rerolled for the player '" + target.getName() + "'.");
    }
}
