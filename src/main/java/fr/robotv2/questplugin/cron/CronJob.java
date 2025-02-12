package fr.robotv2.questplugin.cron;

import fr.robotv2.questplugin.QuestPlugin;
import it.sauronsoftware.cron4j.Predictor;
import it.sauronsoftware.cron4j.Scheduler;
import org.bukkit.Bukkit;

import java.util.TimeZone;

public class CronJob {

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

    public void start() {
        if(!scheduler.isStarted()) {
            scheduler.start();
        }
    }

    public void stop() {
        if(scheduler.isStarted()) {
            scheduler.stop();
        }
    }

    public long getNextExecution() {
        return nextExecution;
    }

    private void calculateNextExecution() {
        this.nextExecution = new Predictor(this.cronSyntax).nextMatchingTime();
    }
}
