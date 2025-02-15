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

    private Set<ActiveQuest> activeQuests;

    private Map<String, Integer> questDone;

    private transient boolean dirty;

    public QuestPlayer(Player player) {
        this(player.getUniqueId(), player.getName(), new HashSet<>(), new HashMap<>());
    }

    @ApiStatus.Internal
    public QuestPlayer(QuestPlayerDto dto, Set<ActiveQuest> quests) {
        this(dto.getId(), dto.getPlayerName(), quests, dto.getQuestDone());
    }

    public QuestPlayer(UUID uniqueId, String playerName, Set<ActiveQuest> quests, Map<String, Integer> questDone) {
        this.playerUniqueId = uniqueId;
        this.playerName = playerName;
        this.activeQuests = quests;
        this.questDone = questDone;
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

    public void removeActiveQuest(Quest quest) {
        this.activeQuests.removeIf((active) -> {
            return active.getQuestId().equals(quest.getQuestId());
        });
    }

    public void removeActiveQuest(QuestGroup group) {
        boolean result = this.activeQuests.removeIf(quest -> quest.getGroupId().equalsIgnoreCase(group.getGroupId()));
        if(result) setDirty(true);
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

    public Map<String, Integer> getQuestDone() {
        return questDone;
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
