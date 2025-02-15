package fr.robotv2.questplugin;

import fr.robotv2.questplugin.group.QuestGroup;
import fr.robotv2.questplugin.quest.Quest;
import fr.robotv2.questplugin.quest.options.Optionnable;
import fr.robotv2.questplugin.storage.model.ActiveQuest;
import fr.robotv2.questplugin.storage.model.QuestPlayer;
import fr.robotv2.questplugin.util.GroupUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class QuestResetHandler {

    private final QuestPlugin plugin;

    public QuestResetHandler(QuestPlugin plugin) {
        this.plugin = plugin;
    }

    public void reset(QuestGroup group) {

        for (QuestPlayer questPlayer : plugin.getDatabaseManager().getCachedPlayers()) {
            questPlayer.removeActiveQuest(group);
        }

        plugin.getDatabaseManager().removeQuests(group).thenRun(() -> {
            plugin.getLogger().info("All stored quest in group " + group.getGroupId() + " have been successfully removed.");
        }).exceptionally((throwable) -> {
            plugin.getLogger().log(Level.SEVERE, "An error occurred while deleting stored quests from storage.", throwable);
            return null;
        });

        if(!group.getOption().getOptionValue(Optionnable.Option.AUTOMATICALLY_GIVEN)) {
            return;
        }

        plugin.getDatabaseManager().getCachedPlayers().forEach((questPlayer) -> fillPlayer(questPlayer, group));
    }

    private int getRoleLimit(String role, QuestGroup group, int defaultValue) {
        return group.getRoleAssignation(role).orElse(defaultValue);
    }

    public int getPlayerLimit(QuestPlayer questPlayer, QuestGroup group) {
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(questPlayer.getId());
        return getRoleLimit(GroupUtil.getPlayerPrimaryGroup(offlinePlayer), group, 0);
    }

    public int fillPlayer(QuestPlayer questPlayer) {
        int amount = 0;

        for(QuestGroup group : plugin.getQuestGroupManager().getGroups()) {
            amount += fillPlayer(questPlayer, group);
        }

        return amount;
    }

    /**
     * Fill the player quest with all the missing quest in the reset service
     * @return the number of quest added
     */
    public int fillPlayer(QuestPlayer questPlayer, QuestGroup group) {
        final int required = getPlayerLimit(questPlayer, group);
        final int current = questPlayer.getActiveQuests(group).size();

        QuestPlugin.debug("Player " + questPlayer.getPlayer().getName() + " has " + current + " quest(s) in group " + group.getGroupId() + " and need " + required + " quest(s).");

        if(required != 0 && required > current) {
            final int diff = required - current;
            return fillPlayer(questPlayer, group, diff);
        }

        return 0;
    }

    /**
     * Fill the player with quests.
     * @param questPlayer to fill quest with
     * @param group to select quests from
     * @param amount of quest to give
     * @return the number of quests given
     */
    public int fillPlayer(QuestPlayer questPlayer, QuestGroup group, int amount) {
        final List<Quest> quests = new ArrayList<>();
        final int max = plugin.getQuestManager().getQuests(group).size();

        while(quests.size() < amount) {

            if(quests.size() >= max) {
                break;
            }

            final Quest random = plugin.getQuestManager().getRandomQuest(questPlayer, group);

            if(random == null) {
                break; //There is no quest available for this delay.
            }

            if(!quests.contains(random)) {
                quests.add(random);
            }
        }

        for(Quest quest : quests) {
            final ActiveQuest activeQuest = new ActiveQuest(questPlayer.getPlayer(), quest);
            questPlayer.addActiveQuest(activeQuest);
        }

        return quests.size();
    }
}
