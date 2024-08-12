package fr.robotv2.questplugin;

import fr.robotv2.questplugin.database.InternalDatabaseManager;
import fr.robotv2.questplugin.group.QuestGroup;
import fr.robotv2.questplugin.quest.Quest;
import fr.robotv2.questplugin.quest.options.Optionnable;
import fr.robotv2.questplugin.storage.dto.GlobalQuestDto;
import fr.robotv2.questplugin.storage.model.ActiveQuest;
import fr.robotv2.questplugin.storage.model.GlobalQuest;
import fr.robotv2.questplugin.storage.model.QuestPlayer;
import fr.robotv2.questplugin.util.GroupUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class QuestResetHandler {

    private final QuestPlugin plugin;

    public QuestResetHandler(QuestPlugin plugin) {
        this.plugin = plugin;
    }

    public void reset(QuestGroup group) {

        for (QuestPlayer questPlayer : plugin.getDatabaseManager().getCachedQuestPlayers()) {
            questPlayer.removeActiveQuest(group);
        }

        plugin.getDatabaseManager().getActiveQuestRepository().remove(group).thenRun(() -> {
            plugin.getLogger().info("All stored quest in group " + group.getGroupId() + " have been successfully removed.");
        }).exceptionally((throwable) -> {
            plugin.getLogger().log(Level.SEVERE, "An error occurred while deleting stored quests from storage.", throwable);
            return null;
        });

        if(group.getOption().getOptionValue(Optionnable.Option.GLOBAL)) {
            final Set<Quest> random = plugin.getQuestManager().getNRandomQuests(group, group.getGlobalAssignation());
            // todo
        }

        if(!group.getOption().getOptionValue(Optionnable.Option.AUTOMATICALLY_GIVEN)) {
            return;
        }

        plugin.getDatabaseManager().getCachedQuestPlayers().forEach((questPlayer) -> fillPlayer(questPlayer, group));
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

        if(group.getOption().getOptionValue(Optionnable.Option.GLOBAL)) {
            final Set<Quest> quests = plugin.getDatabaseManager().getGlobalQuestRepository().selectAll(group)
                    .join() // they will be cached so this is fine.
                    .stream()
                    .map((dto) -> plugin.getQuestManager().fromId(dto.getQuestId(), dto.getGroupId()))
                    .filter(Objects::nonNull)
                    .filter((quest) -> !questPlayer.hasQuest(quest))
                    .collect(Collectors.toSet());
            quests.forEach((quest) -> questPlayer.addActiveQuest(new ActiveQuest(questPlayer.getPlayer(), quest)));
            return quests.size();
        }

        final int required = getPlayerLimit(questPlayer, group);
        final int current = questPlayer.getActiveQuests(group).size();

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

            final Quest random = plugin.getQuestManager().getRandomQuest(group);

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
