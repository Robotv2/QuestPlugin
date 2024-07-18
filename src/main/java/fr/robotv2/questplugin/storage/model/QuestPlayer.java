package fr.robotv2.questplugin.storage.model;

import fr.robotv2.questplugin.group.QuestGroup;
import fr.robotv2.questplugin.quest.Quest;
import fr.robotv2.questplugin.storage.Identifiable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.stream.Collectors;

public class QuestPlayer implements java.io.Serializable, Identifiable<UUID> {

    private UUID playerUniqueId;

    private String playerName;

    // HashSet is used directly for serialization reason.
    private HashSet<ActiveQuest> activeQuests;

    public QuestPlayer(Player player) {
        this(player.getUniqueId(), player.getName(), new HashSet<>());
    }

    public QuestPlayer(UUID uniqueId, String playerName, HashSet<ActiveQuest> sets) {
        this.playerUniqueId = uniqueId;
        this.playerName = playerName;
        this.activeQuests = sets;
    }

    @Override
    public UUID getId() {
        return playerUniqueId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(getId());
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

    public void checkEndedQuests() {
        this.activeQuests.removeIf(ActiveQuest::hasEnded);
    }

    public void removeActiveQuest(String groupId) {
        this.activeQuests.removeIf(quest -> quest.getGroupId().equalsIgnoreCase(groupId));
    }

    public void removeActiveQuest(QuestGroup group) {
        removeActiveQuest(group.getGroupId());
    }

    public void clearActiveQuests() {
        activeQuests.clear();
    }

    public boolean hasQuest(Quest quest) {
        return getActiveQuests().stream()
                .anyMatch(other -> Objects.equals(quest.getQuestId(), other.getQuestId())
                        && Objects.equals(quest.getQuestGroup().getGroupId(), other.getGroupId()));
    }
}
