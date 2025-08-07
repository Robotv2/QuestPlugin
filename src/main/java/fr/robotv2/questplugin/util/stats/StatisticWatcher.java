package fr.robotv2.questplugin.util.stats;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import fr.robotv2.questplugin.util.TriFunction;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.UUID;

public class StatisticWatcher {

    private final Statistic statistic;

    private final TriFunction<Player, Integer, Integer, Event> callEvent;

    private final Table<UUID, Statistic, Integer> playerStats;

    public StatisticWatcher(Statistic statistic, TriFunction<Player, Integer, Integer, Event> callEvent) {
        this.statistic = statistic;
        this.callEvent = callEvent;
        this.playerStats = HashBasedTable.create();
    }

    public Statistic getStatistic() {
        return statistic;
    }

    public void init(Player player) {
        playerStats.put(player.getUniqueId(), statistic, player.getStatistic(statistic));
    }

    public void clear(Player player) {
        playerStats.remove(player.getUniqueId(), statistic);
    }

    public void check(Player player) {
        int value = player.getStatistic(statistic);
        int lastValue = playerStats.get(player.getUniqueId(), statistic);
        if(value != lastValue) {
            playerStats.put(player.getUniqueId(), statistic, value);
            final Event event = callEvent.apply(player, lastValue, value);
            if(event != null) {
                Bukkit.getPluginManager().callEvent(event);
            }
        }
    }
}
