package fr.robotv2.questplugin.util.placeholder;


import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.util.placeholder.impl.RelationalValuePlaceholder;
import fr.robotv2.questplugin.util.placeholder.impl.SimplePlaceholder;
import fr.robotv2.questplugin.util.placeholder.impl.SuppliedPlaceholder;
import fr.robotv2.questplugin.util.placeholder.impl.ValuePlaceholder;

import java.util.function.Function;
import java.util.function.Predicate;

public abstract class PlaceholderSupport<E extends PlaceholderSupport<E>> {

    public abstract E apply(Function<String, String> replaceFunction);

    public E apply(String from, String to) {
        return apply(SimplePlaceholder.of(from, to));
    }

    public E apply(SimplePlaceholder placeholder) {
        return apply(placeholder::apply);
    }

    public E apply(SuppliedPlaceholder placeholder) {
        return apply(placeholder::apply);
    }

    @SuppressWarnings("unchecked")
    public E apply(SimplePlaceholder... placeholders) {

        for (SimplePlaceholder placeholder : placeholders) {
            apply(placeholder);
        }

        return (E) this;
    }

    public <T> E apply(ValuePlaceholder<T> placeholder, T value) {
        return apply(s -> placeholder.apply(s, value));
    }

    public <A, B> E apply(RelationalValuePlaceholder<A, B> placeholder, A fst, B snd) {
        return apply(s -> placeholder.apply(s, fst, snd));
    }

    public E applyIf(Predicate<String> predicate, String from, String to) {
        return applyIf(predicate, SimplePlaceholder.of(from, to));
    }

    public E applyIf(Predicate<String> predicate, SimplePlaceholder placeholder) {
        return apply(s -> predicate.test(s) ? placeholder.apply(s) : s);
    }

    @SuppressWarnings("unchecked")
    public <T> E applyIf(ValuePlaceholder<T> placeholder, Class<T> tClass, Object value) {

        if (tClass.isAssignableFrom(value.getClass())) {
            apply(placeholder, (T) value);
        }

        return (E) this;
    }

    public E color() {
        return apply((text) -> QuestPlugin.instance().getColorProvider().colorize(text));
    }

//    public E papi(OfflinePlayer offlinePlayer) {
//        return apply(s -> SafePlaceholderAPI.parsePAPI(s, offlinePlayer));
//    }
}

