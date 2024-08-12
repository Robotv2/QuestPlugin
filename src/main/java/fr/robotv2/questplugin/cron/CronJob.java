package fr.robotv2.questplugin.cron;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.api.cron.ICronJob;
import it.sauronsoftware.cron4j.Predictor;
import it.sauronsoftware.cron4j.Scheduler;
import org.bukkit.Bukkit;

import java.util.TimeZone;

public class CronJob implements ICronJob {

    private final Scheduler scheduler;

    private final String cronSyntax;

    private final Runnable runnable;

    private long nextExecution;

    public CronJob(String cronSyntax, Runnable runnable) {
        this.scheduler = new Scheduler();
        this.cronSyntax = cronSyntax;
        this.runnable = runnable;

        calculateNextExecution();
    }

    @Override
    public void prepare() {

        if(scheduler.isStarted()) {
            throw new IllegalStateException("scheduler is running");
        }
        scheduler.setTimeZone(TimeZone.getDefault());
        scheduler.schedule(this.cronSyntax, () -> {
            Bukkit.getScheduler().runTask(QuestPlugin.instance(), runnable);
            calculateNextExecution();
        });
    }

    @Override
    public void start() {
        if(!scheduler.isStarted()) {
            scheduler.start();
        }
    }

    @Override
    public void stop() {
        if(scheduler.isStarted()) {
            scheduler.stop();
        }
    }

    @Override
    public long getNextExecution() {
        return nextExecution;
    }

    private void calculateNextExecution() {
        this.nextExecution = new Predictor(this.cronSyntax).nextMatchingTime();
    }
}
