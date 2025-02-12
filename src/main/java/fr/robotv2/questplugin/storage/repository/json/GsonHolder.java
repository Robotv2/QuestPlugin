package fr.robotv2.questplugin.storage.repository.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;

public class GsonHolder {

    public static final Gson GSON;

    static {
        final GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC);
        builder.setPrettyPrinting();
        builder.serializeNulls();
        builder.enableComplexMapKeySerialization();
        GSON = builder.create();
    }
}
