package fr.robotv2.questplugin.util;

import com.cryptomorin.xseries.XMaterial;
import org.jetbrains.annotations.NotNull;

public class XMaterialUtil {

    @NotNull
    public static XMaterial matchMaterialOrThrow(String value) {
        return XMaterial.matchXMaterial(value).orElseThrow(() -> new NullPointerException("Value " + value + " is not a valid material type"));
    }

}
