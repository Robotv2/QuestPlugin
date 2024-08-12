package fr.robotv2.questplugin.storage.model;

import fr.robotv2.questplugin.group.QuestGroup;
import fr.robotv2.questplugin.quest.Quest;
import fr.robotv2.questplugin.storage.DirtyAware;
import fr.robotv2.questplugin.storage.Identifiable;
import fr.robotv2.questplugin.storage.dto.QuestPlayerDto;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.stream.Collectors;

public class QuestPlayer implements java.io.Serializable, Identifiable<UUID>, DirtyAware {

    private UUID playerUniqueId;

    private String playerName;

    private transient Set<ActiveQuest> activeQuests;

    private transient boolean dirty;

    public QuestPlayer(Player player) {
        this(player.getUniqueId(), player.getName(), new HashSet<>());
    }

    @ApiStatus.Internal
    public QuestPlayer(QuestPlayerDto dto, Set<ActiveQuest> quests) {
        this(dto.getId(), dto.getPlayerName(), quests);
    }

    public QuestPlayer(UUID uniqueId, String playerName, Set<ActiveQuest> quests) {
        this.playerUniqueId = uniqueId;
        this.playerName = playerName;
        this.activeQuests = quests;
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

    public Set<ActiveQuest> getActiveQuests() {
        return activeQuests;
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
        setDirty(true);
    }

    public void addActiveQuest(ActiveQuest activeQuest) {
        this.activeQuests.add(activeQuest);
        setDirty(true);
    }

    public void removeActiveQuest(String groupId) {
        boolean result = this.activeQuests.removeIf(quest -> quest.getGroupId().equalsIgnoreCase(groupId));
        if(result) setDirty(true);
    }

    public void removeActiveQuest(QuestGroup group) {
        removeActiveQuest(group.getGroupId());
    }

    public void clearActiveQuests() {
        activeQuests.clear();
        setDirty(true);
    }

    public boolean hasQuest(Quest quest) {
        return getActiveQuests().stream()
                .anyMatch(other -> Objects.equals(quest.getQuestId(), other.getQuestId())
                        && Objects.equals(quest.getQuestGroup().getGroupId(), other.getGroupId()));
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
