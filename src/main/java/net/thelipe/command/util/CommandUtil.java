/*
 * This source file is part of command-lib.
 *
 * Copyright (c) 2026 thelipe7
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for license information.
 */

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

/**
 * Shared utility methods used by the command framework.
 */
public class CommandUtil {

    /**
     * Pattern used to split command input by spaces.
     */
    public final static Pattern SPACE = Pattern.compile(" ");
    /**
     * Pattern used to parse textual durations such as {@code 1d2h30m}.
     */
    public final static Pattern DURATION = Pattern.compile("(?:(\\d+)d)?(?:(\\d+)h)?(?:(\\d+)m)?(?:(\\d+)s)?");

    private static final Map<Class<?>, Map<String, Enum<?>>> enumCache = new HashMap<>();

    /**
     * Sends one or more MiniMessage-formatted lines to a command sender.
     *
     * @param sender the recipient
     * @param message the lines to send
     */
    public static void sendMessage(CommandSender sender, String... message) {
        sender.sendMessage(MiniMessage.miniMessage().deserialize(String.join("\n", message)));
    }

    /**
     * Joins an object array using the provided separator.
     *
     * @param array the array to join
     * @param separator the separator string
     * @return the joined string
     */
    public static String join(Object[] array, String separator) {
        return ApacheCommonsLangUtil.join(array, separator);
    }

    /**
     * Joins an iterable using the provided separator.
     *
     * @param iterable the values to join
     * @param separator the separator string
     * @return the joined string
     */
    public static String join(Iterable<?> iterable, String separator) {
        return ApacheCommonsLangUtil.join(iterable, separator);
    }

    /**
     * Resolves an enum constant by its exact name using an internal cache.
     *
     * @param clazz the enum type
     * @param name the enum constant name
     * @return the matching enum constant, or {@code null} when not found
     */
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

    /**
     * Returns the available constant names for an enum type.
     *
     * @param clazz the enum type
     * @return the enum constant names
     */
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

    /**
     * Parses a textual duration in the format {@code 1d2h3m4s}.
     *
     * @param text the text to parse
     * @return the parsed duration, or {@code null} when the format is invalid
     */
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
