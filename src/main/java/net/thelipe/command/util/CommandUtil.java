package net.thelipe.command.util;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandUtil {

    public final static Pattern SPACE = Pattern.compile(" ");
    public final static Pattern DURATION = Pattern.compile("(?:(\\d+)d)?(?:(\\d+)h)?(?:(\\d+)m)?(?:(\\d+)s)?");

    private static final Map<Class<?>, Map<String, Enum<?>>> enumCache = new HashMap<>();

    public static void sendMessage(CommandSender sender, String... message) {
        sender.sendMessage(MiniMessage.miniMessage().deserialize(String.join("\n", message)));
    }

    public static String join(Object[] array, String separator) {
        return ApacheCommonsLangUtil.join(array, separator);
    }

    public static String join(Iterable<?> iterable, String separator) {
        return ApacheCommonsLangUtil.join(iterable, separator);
    }

    public static Enum<?> getEnumByName(Class<? extends Enum<?>> clazz, String name) {
        Map<String, Enum<?>> enumMap = enumCache.get(clazz);
        if (enumMap == null) {
            enumMap = new HashMap<>();
            for (Enum<?> enumObject : clazz.getEnumConstants()) {
                enumMap.put(enumObject.name(), enumObject);
            }

            enumCache.put(clazz, enumMap);
        }

        return enumMap.get(name);
    }

    public static List<String> getEnumNames(Class<? extends Enum<?>> clazz) {
        Map<String, Enum<?>> enumMap = enumCache.get(clazz);
        if (enumMap == null) {
            enumMap = new HashMap<>();
            for (Enum<?> enumObject : clazz.getEnumConstants()) {
                enumMap.put(enumObject.name(), enumObject);
            }

            enumCache.put(clazz, enumMap);
        }

        return new ArrayList<>(enumMap.keySet());
    }

    public static Duration parseDuration(String text) {
        if (text.isBlank()) return null;

        Matcher matcher = DURATION.matcher(text);
        if (matcher.matches()) {
            long days = parseLong(matcher.group(1));
            long hours = parseLong(matcher.group(2));
            long minutes = parseLong(matcher.group(3));
            long seconds = parseLong(matcher.group(4));

            return Duration.ofDays(days).plusHours(hours).plusMinutes(minutes).plusSeconds(seconds);
        } else {
            return null;
        }
    }

    private static long parseLong(String value) {
        return value != null ? Long.parseLong(value) : (long) 0;
    }

}
