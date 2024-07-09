package fr.robotv2.questplugin.quest.task;

public abstract class TaskTarget<T> {

    private final Task task;
    private final Class<T> tClass;

    public TaskTarget(Task task, Class<T> tClass) {
        this.task = task;
        this.tClass = tClass;
    }

    public Class<T> getTargetClass() {
        return tClass;
    }

    public boolean isTarget(Object value) {

        if(!getTargetClass().isInstance(value)) {
            return false;
        }

        try {
            return matchesValue(getTargetClass().cast(value));
        } catch (ClassCastException exception) {
            return false;
        }
    }

    abstract protected boolean matchesValue(T value);
}
