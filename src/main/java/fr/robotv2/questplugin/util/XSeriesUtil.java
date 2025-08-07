package fr.robotv2.questplugin.util;

import com.cryptomorin.xseries.XEntityType;
import com.cryptomorin.xseries.XMaterial;
import org.jetbrains.annotations.NotNull;

public class XSeriesUtil {
    @NotNull
    public static XMaterial matchMaterialOrThrow(String value) {
        return XMaterial.matchXMaterial(value).orElseThrow(() -> new NullPointerException("Value " + value + " is not a valid material type"));
    }

    @NotNull
    public static XEntityType matchEntityTypeOrThrow(String value) {
        return XEntityType.of(value).orElseThrow(() -> new NullPointerException("Value " + value + " is not a valid entity type"));
    }
}
