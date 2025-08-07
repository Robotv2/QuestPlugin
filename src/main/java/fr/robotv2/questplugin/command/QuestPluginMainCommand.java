package fr.robotv2.questplugin.command;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.event.QuestDoneEvent;
import fr.robotv2.questplugin.event.TaskDoneEvent;
import fr.robotv2.questplugin.group.QuestGroup;
import fr.robotv2.questplugin.quest.Quest;
import fr.robotv2.questplugin.storage.model.ActiveQuest;
import fr.robotv2.questplugin.storage.model.QuestPlayer;
import org.bukkit.Bukkit;
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
    public void onGive(BukkitCommandActor actor, @Named("group") QuestGroup group, @Named("quest") Quest quest, @Named("target") @Default("me") Player target) {
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

    @Subcommand("clear")
    @CommandPermission("betterdailyquest.command.clear")
    @AutoComplete("@players @target_quests")
    public void onClear(BukkitCommandActor actor, @Named("target") Player player, @Named("questID") String questID) {
        final QuestPlayer questPlayer = plugin.getDatabaseManager().getCachedQuestPlayer(player);

        if(questPlayer == null) {
            actor.getSender().sendMessage(ChatColor.RED + "The player is not connected or is not loaded.");
            return;
        }

        final ActiveQuest activeQuest = questPlayer.getActiveQuest(questID);
        if(activeQuest == null) {
            actor.getSender().sendMessage(ChatColor.RED + "The target does not have this quest.");
            return;
        }

        plugin.getDatabaseManager().removeQuestsAndTasks(activeQuest);
        questPlayer.removeActiveQuest(activeQuest);

        actor.getSender().sendMessage(ChatColor.GREEN + "The quest '" + activeQuest.getQuestId() + "' has successfully been cleared for the player '" + player.getName() + "'.");
    }

    @Subcommand("reset")
    @CommandPermission("betterdailyquest.command.reset")
    @AutoComplete("@players @target_quests")
    public void onReset(BukkitCommandActor actor, @Named("target") Player target, String questID) {
        final QuestPlayer questPlayer = plugin.getDatabaseManager().getCachedQuestPlayer(target);

        if(questPlayer == null) {
            actor.getSender().sendMessage(ChatColor.RED + "The player is not connected or is not loaded.");
            return;
        }

        final ActiveQuest activeQuest = questPlayer.getActiveQuest(questID);
        if(activeQuest == null) {
            actor.getSender().sendMessage(ChatColor.RED + "The target does not have this quest.");
            return;
        }

        if(activeQuest.getQuest() == null) {
            actor.getSender().sendMessage(ChatColor.RED + "The quest is not available. (" + activeQuest.getQuestId() + ")");
            return;
        }

        plugin.getDatabaseManager().removeQuestsAndTasks(activeQuest);
        questPlayer.removeActiveQuest(activeQuest);

        final Quest quest = activeQuest.getQuest();
        if(quest != null) {
            questPlayer.addActiveQuest(new ActiveQuest(target, quest));
        } else {
            actor.getSender().sendMessage(ChatColor.RED + "The quest is not available. (" + activeQuest.getQuestId() + ")");
            return;
        }

        actor.getSender().sendMessage(ChatColor.GREEN + "The quest '" + quest.getQuestId() + "' has successfully been reset for the player '" + target.getName() + "'.");
    }

    @Subcommand("reroll")
    @CommandPermission("betterdailyquest.command.reroll")
    @AutoComplete("@target_quests")
    public void onRerollSelf(BukkitCommandActor actor, String questID) {
        final Player target = actor.requirePlayer();
        handleReroll(actor, questID, target, false);
    }

    @Subcommand("reroll-others")
    @CommandPermission("betterdailyquest.command.reroll.others")
    @AutoComplete("@players @target_quests")
    public void onRerollOthers(BukkitCommandActor actor, Player player, String questID) {
        if (player == null) {
            actor.getSender().sendMessage(ChatColor.RED + "You must specify a player for reroll-others.");
            return;
        }

        handleReroll(actor, questID, player, true);
    }

    private void handleReroll(BukkitCommandActor actor, String questID, Player target, boolean isOthers) {
        final QuestPlayer questPlayer = plugin.getDatabaseManager().getCachedQuestPlayer(target);

        if (questPlayer == null) {
            actor.getSender().sendMessage(ChatColor.RED + "The player is not connected or is not loaded.");
            return;
        }

        final ActiveQuest activeQuest = questPlayer.getActiveQuest(questID);
        if (activeQuest == null) {
            actor.getSender().sendMessage(ChatColor.RED + (isOthers ? "The target doesn't" : "You don't") + " have this quest.");
            return;
        }

        int currentRerollCount = activeQuest.getRerollCount();

        if (activeQuest.getQuest() == null) {
            actor.getSender().sendMessage(ChatColor.RED + "The quest is not available. (" + activeQuest.getQuestId() + ")");
            return;
        }

        final QuestGroup group = activeQuest.getQuest().getQuestGroup();
        if(group.getMaxRerolls() > 0 && currentRerollCount >= group.getMaxRerolls()) {
            actor.getSender().sendMessage(ChatColor.RED + "You have reached the maximum rerolls for this quest group.");
            return;
        }

        final Quest newQuest = plugin.getQuestManager().getRandomQuest(questPlayer, activeQuest.getQuest().getQuestGroup());
        if (newQuest == null) {
            actor.getSender().sendMessage(ChatColor.RED + "No quest available for reroll.");
            return;
        }

        questPlayer.removeActiveQuest(activeQuest);

        final ActiveQuest newActiveQuest = new ActiveQuest(target, newQuest);
        newActiveQuest.setRerollCount(currentRerollCount + 1);
        questPlayer.addActiveQuest(newActiveQuest);

        if (isOthers) {
            actor.getSender().sendMessage(ChatColor.GREEN + "The quest '" + activeQuest.getQuestId() + "' has successfully been rerolled for the player '" + target.getName() + "'.");
        } else {
            actor.getSender().sendMessage(ChatColor.GREEN + "Your quest '" + activeQuest.getQuestId() + "' has been successfully rerolled.");
        }
    }

    @Subcommand("complete")
    @CommandPermission("betterdailyquest.command.complete")
    @AutoComplete("@players @target_quests")
    public void onComplete(BukkitCommandActor actor, @Named("target") Player target, String questID) {
        final QuestPlayer questPlayer = plugin.getDatabaseManager().getCachedQuestPlayer(target);

        if(questPlayer == null) {
            actor.getSender().sendMessage(ChatColor.RED + "The player is not connected or is not loaded.");
            return;
        }

        final ActiveQuest activeQuest = questPlayer.getActiveQuest(questID);
        if(activeQuest == null) {
            actor.getSender().sendMessage(ChatColor.RED + "The target does not have this quest.");
            return;
        }

        if(activeQuest.isDone()) {
            actor.getSender().sendMessage(ChatColor.RED + "The quest is already completed.");
            return;
        }

        final Quest quest = activeQuest.getQuest();
        if(quest == null) {
            actor.getSender().sendMessage(ChatColor.RED + "The quest is not available. (" + activeQuest.getQuestId() + ")");
            return;
        }

        activeQuest.getTasks().forEach((task) -> {
            if(task.isDone()) {
                return;
            }

            task.setDone(true);
            Bukkit.getPluginManager().callEvent(new TaskDoneEvent(target, quest, quest.getTask(task.getTaskId()), task));

            if(activeQuest.isDone()) {
                Bukkit.getPluginManager().callEvent(new QuestDoneEvent(quest, activeQuest, target));
            }
        });
        actor.getSender().sendMessage(ChatColor.GREEN + "The quest '" + activeQuest.getQuestId() + "' has successfully been completed for the player '" + target.getName() + "'.");
    }
}
