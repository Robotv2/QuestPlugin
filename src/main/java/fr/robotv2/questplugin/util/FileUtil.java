package fr.robotv2.questplugin.util;

import java.io.File;
import java.util.function.Consumer;

public class FileUtil {

    public static String getFileNameWithoutExtension(File file) {
        return file.getName().substring(0, file.getName().lastIndexOf('.'));
    }

    public static void iterateFiles(File initial, Consumer<File> consumer) {
        if(!initial.exists()) {
            return;
        }

        final File[] files = initial.listFiles();

        if(files == null) {
            return;
        }

        for (File file : files) {
            if(file.isDirectory()) {
                iterateFiles(file, consumer);
            } else {
                consumer.accept(file);
            }
        }
    }
}
