package fr.robotv2.questplugin;

import fr.robotv2.questplugin.database.DatabaseManager;
import fr.robotv2.questplugin.group.QuestGroup;
import fr.robotv2.questplugin.quest.Quest;
import fr.robotv2.questplugin.quest.QuestManager;
import fr.robotv2.questplugin.quest.options.Optionnable;
import fr.robotv2.questplugin.storage.model.ActiveQuest;
import fr.robotv2.questplugin.storage.model.QuestPlayer;
import fr.robotv2.questplugin.util.GroupUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;

public class QuestResetHandler {

    private final QuestPlugin plugin;
    private final DatabaseManager databaseManager;

    public QuestResetHandler(QuestPlugin plugin) {
        this.plugin = plugin;
        this.databaseManager = plugin.getDatabaseManager();
    }

    public void reset(QuestGroup group) {

        for (QuestPlayer questPlayer : databaseManager.getCachedQuestPlayers()) {
            questPlayer.removeActiveQuest(group);
        }

        if(!group.getOption().getOptionValue(Optionnable.Option.AUTOMATICALLY_GIVEN)) {
            return;
        }

        databaseManager.getCachedQuestPlayers().forEach((questPlayer) -> fillPlayer(questPlayer, group));
    }

    private int getDefaultLimit(QuestGroup group, int defaultValue) {
        return group.getRoleAssignation("default").orElse(defaultValue);
    }

    private int getLimitFor(String role, QuestGroup group, int defaultValue) {
        return group.getRoleAssignation(role).orElse(defaultValue);
    }

    public int getPlayerLimit(QuestPlayer questPlayer, QuestGroup group) {
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(questPlayer.getId());
        return getLimitFor(GroupUtil.getPlayerPrimaryGroup(offlinePlayer), group, 0);
    }

    /**
     * Fill the player quest with all the missing quest in the reset service
     * @return the number of quest added
     */
    public int fillPlayer(QuestPlayer questPlayer, QuestGroup group) {

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
