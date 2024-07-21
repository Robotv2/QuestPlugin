package fr.robotv2.questplugin.util;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class Futures {

    public static CompletableFuture<Void> ofAll(Collection<CompletableFuture<Void>> futures) {
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

}
