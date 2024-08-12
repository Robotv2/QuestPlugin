package fr.robotv2.questplugin.api.cron;

public interface ICronJob {

    void prepare();

    void start();

    void stop();

    long getNextExecution();
}
