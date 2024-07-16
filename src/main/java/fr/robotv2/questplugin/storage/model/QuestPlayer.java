package fr.robotv2.questplugin.storage.model;

import fr.robotv2.questplugin.group.QuestGroup;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class QuestPlayer implements java.io.Serializable {

    private UUID playerUniqueId;

    private String playerName;

    private HashSet<ActiveQuest> activeQuests;

    public QuestPlayer(UUID uniqueId, String playerName, HashSet<ActiveQuest> sets) {
        this.playerUniqueId = uniqueId;
        this.playerName = playerName;
        this.activeQuests = sets;
    }

    public UUID getPlayerUniqueId() {
        return playerUniqueId;
    }

    public String getPlayerName() {
        return playerName;
    }

    @UnmodifiableView
    public Set<ActiveQuest> getActiveQuests() {
        return Collections.unmodifiableSet(activeQuests);
    }

    @UnmodifiableView
    public List<ActiveQuest> getActiveQuests(String groupId) {
        return getActiveQuests().stream()
                .filter(quest -> quest.getGroupId().equalsIgnoreCase(groupId))
                .collect(Collectors.toList());
    }

    @UnmodifiableView
    public List<ActiveQuest> getActiveQuests(QuestGroup group) {
        return getActiveQuests(group.getGroupId());
    }

    public void addActiveQuests(Collection<ActiveQuest> activeQuests) {
        this.activeQuests.addAll(activeQuests);
    }

    public void addActiveQuest(ActiveQuest activeQuest) {
        this.activeQuests.add(activeQuest);
    }

    public void removeActiveQuest(String resetId) {
        this.activeQuests.removeIf(quest -> quest.getGroupId().equalsIgnoreCase(resetId));
    }

    public void removeActiveQuest(QuestGroup group) {
        removeActiveQuest(group.getGroupId());
    }

    public void clearActiveQuests() {
        activeQuests.clear();
    }

    public boolean hasQuest(String questId) {
        return getActiveQuests().stream().anyMatch(quest -> quest.getQuestId().equalsIgnoreCase(questId));
    }
}
