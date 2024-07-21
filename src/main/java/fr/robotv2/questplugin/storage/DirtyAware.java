package fr.robotv2.questplugin.storage;

public interface DirtyAware {

    boolean isDirty();

    void setDirty(boolean dirty);

}
