package fr.robotv2.questplugin.util;

import java.io.File;

public class FileUtil {
    public static String getFileNameWithoutExtension(File file) {
        return file.getName().substring(0, file.getName().lastIndexOf('.'));
    }
}
