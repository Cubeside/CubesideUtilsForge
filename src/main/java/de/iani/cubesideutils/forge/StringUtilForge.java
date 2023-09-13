package de.iani.cubesideutils.forge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class StringUtilForge {
    public static ArrayList<String> copyPartialMatches(String token, Collection<String> unfiltered) {
        return unfiltered.stream().filter(s -> startsWithIgnoreCase(s, token)).collect(Collectors.toCollection(() -> new ArrayList<>()));
    }

    public static boolean startsWithIgnoreCase(final String string, final String prefix) {
        return string.length() >= prefix.length() && string.regionMatches(true, 0, prefix, 0, prefix.length());
    }
}
