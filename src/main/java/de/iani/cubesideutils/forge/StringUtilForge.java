package de.iani.cubesideutils.forge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

public class StringUtilForge {
    public static final char COLOR_CHAR = '§';
    public static final Pattern COLOR_CHAR_PATTERN = Pattern.compile("\\" + COLOR_CHAR);
    public static final Pattern COLOR_CODES_PATTERN = Pattern.compile("\\" + COLOR_CHAR + "([0-9a-fk-or]|(x(" + COLOR_CHAR + "[0-9a-f]){6}))", Pattern.CASE_INSENSITIVE);
    private static final Pattern URL_PATTERN = Pattern.compile("^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,4})(/\\S*)?$");

    public static ArrayList<String> copyPartialMatches(String token, Collection<String> unfiltered) {
        return unfiltered.stream().filter(s -> startsWithIgnoreCase(s, token)).collect(Collectors.toCollection(() -> new ArrayList<>()));
    }

    public static boolean startsWithIgnoreCase(final String string, final String prefix) {
        return string.length() >= prefix.length() && string.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    public static Component parseLegacyColoredString(String text) {
        return parseLegacyColoredString(text, false);
    }

    public static Component parseLegacyColoredString(String text, boolean parseUrls) {
        ArrayList<MutableComponent> components = new ArrayList<>();
        Style style = Style.EMPTY;
        Style newStyle = style;
        StringBuilder builder = new StringBuilder();
        Matcher urlMatcher = parseUrls ? URL_PATTERN.matcher(text) : null;

        int len = text.length();
        for (int i = 0; i < len; i++) {
            char current = text.charAt(i);
            if (current == COLOR_CHAR) {
                if (i < len) {
                    char formatting = text.charAt(i + 1);
                    ChatFormatting chatFormatting = ChatFormatting.getByCode(formatting);
                    formatingSection: if (formatting == 'x') {
                        // rgb colors
                        if (len > i + 13) {
                            StringBuilder hexString = new StringBuilder();
                            for (int j = 0; j < 6; j++) {
                                char expectedColorChar = text.charAt(i + 2 + j * 2);
                                char expectedHexColorPart = text.charAt(i + 2 + j * 2 + 1);
                                if (expectedColorChar != COLOR_CHAR) {
                                    break formatingSection;
                                }
                                expectedHexColorPart = Character.toLowerCase(expectedHexColorPart);
                                if ((expectedHexColorPart >= '0' && expectedHexColorPart <= '9') || (expectedHexColorPart >= 'a' && expectedHexColorPart <= 'f')) {
                                    hexString.append(expectedHexColorPart);
                                } else {
                                    break formatingSection;
                                }
                            }
                            int color = Integer.parseInt(hexString.toString(), 16);
                            newStyle = Style.EMPTY.withColor(color);
                            i += 13;
                        }
                    } else if (chatFormatting != null) {
                        newStyle = style.applyLegacyFormat(chatFormatting);
                        i++;
                    }
                }
                if (!newStyle.equals(style)) {
                    if (!builder.isEmpty()) {
                        components.add(Component.literal(builder.toString()).setStyle(style));
                        builder.delete(0, builder.length());
                    }
                    style = newStyle;
                }
            } else {
                if (parseUrls) {
                    // look for urls
                    int nextSpace = text.indexOf(' ', i);
                    if (nextSpace == -1) {
                        nextSpace = text.length();
                    }
                    if (urlMatcher.region(i, nextSpace).find()) {
                        if (!builder.isEmpty()) {
                            components.add(Component.literal(builder.toString()).setStyle(style));
                            builder.delete(0, builder.length());
                        }
                        String url = text.substring(i, nextSpace);
                        ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, url.startsWith("http") ? url : "http://" + url);
                        components.add(Component.literal(url).setStyle(style.withClickEvent(clickEvent)));
                        i = nextSpace - 1;
                        continue;
                    }
                }
                // normal char
                builder.append(current);
            }
        }
        if (!builder.isEmpty()) {
            components.add(Component.literal(builder.toString()).setStyle(style));
        }
        if (components.isEmpty()) {
            return Component.literal("");
        } else if (components.size() == 1) {
            return components.get(0);
        } else if (components.get(0).getStyle().equals(Style.EMPTY)) {
            MutableComponent parent = components.get(0);
            for (int i = 1; i < components.size(); i++) {
                parent.append(components.get(i));
            }
            return parent;
        } else {
            MutableComponent parent = Component.literal("");
            for (int i = 0; i < components.size(); i++) {
                parent.append(components.get(i));
            }
            return parent;
        }
    }

    public static String convertColors(String text) {
        return parseColors(text, false);
    }

    public static String stripColors(String text) {
        return parseColors(text, true);
    }

    private static String parseColors(String text, boolean remove) {
        if (text == null) {
            return null;
        }
        StringBuilder builder = null;
        int len = text.length();
        for (int i = 0; i < len; i++) {
            char current = text.charAt(i);
            if (current == '&' && i + 1 < len) {
                char next = text.charAt(i + 1);
                // if next is a "&" skip next char
                // if its a color char replace the "&"
                if (ChatFormatting.getByCode(next) != null || next == '&' || next == 'x') {
                    if (builder == null) {
                        builder = new StringBuilder();
                        builder.append(text, 0, i);
                    }
                    i++;
                    if (next != '&') {
                        if (next == 'x') {
                            TextColor hex = parseHexColor(text, i + 1);
                            if (hex == null) {
                                builder.append(current).append(next);
                            } else {
                                if (!remove) {
                                    String hexString = Integer.toString(hex.getValue(), 16);
                                    builder.append(COLOR_CHAR).append('x');
                                    int offset = hexString.length() - 6;
                                    for (int j = 0; j < 6; j++) {
                                        int charPos = j + offset;
                                        char c = charPos >= 0 ? hexString.charAt(charPos) : '0';
                                        builder.append(COLOR_CHAR).append(c);
                                    }
                                }
                                i += 6;
                            }
                        } else {
                            if (!remove) {
                                builder.append(COLOR_CHAR).append(next);
                            }
                        }
                        continue;
                    }
                }
            }
            if (builder != null) {
                builder.append(current);
            }
        }
        return builder == null ? text : builder.toString();
    }

    public static TextColor parseHexColor(String text, int startIndex) {
        if (text.length() - startIndex < 6) {
            return null;
        }
        StringBuilder hexString = new StringBuilder("");
        for (int i = 0; i < 6; i++) {
            char c = Character.toLowerCase(text.charAt(i + startIndex));
            if ((c < '0' || c > '9') && (c < 'a' || c > 'f')) {
                return null;
            }
            hexString.append(c);
        }
        return TextColor.fromRgb(Integer.parseInt(hexString.toString(), 16));
    }

    public static String revertColors(String converted) {
        if (converted == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < converted.length(); i++) {
            char c = converted.charAt(i);
            if (c == COLOR_CHAR) {
                if (converted.length() > i + 1 && converted.charAt(i + 1) == 'x') {
                    if (i + 14 > converted.length()) {
                        builder.append("&");
                        continue;
                    }
                    String hexString = converted.substring(i, i + 14);
                    if (!COLOR_CODES_PATTERN.matcher(hexString).matches()) {
                        builder.append("&");
                        continue;
                    }
                    builder.append("&").append(COLOR_CHAR_PATTERN.matcher(hexString).replaceAll(""));
                    i += 13;
                    continue;
                }
                builder.append("&");
            } else if (c == '&') {
                builder.append("&&");
            } else {
                builder.append(c);
            }
        }

        return builder.toString();
    }
}
