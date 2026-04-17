/*
 * This source file is part of command-lib.
 *
 * Copyright (c) 2026 thelipe7
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for license information.
 */

package net.thelipe.command;

import lombok.Getter;
import net.thelipe.command.annotation.*;
import net.thelipe.command.annotation.Optional;
import net.thelipe.command.argument.Argument;
import net.thelipe.command.argument.ArgumentResolver;
import net.thelipe.command.argument.ArgumentResult;
import net.thelipe.command.util.CommandUtil;
import net.thelipe.command.util.ResultStatus;
import net.thelipe.command.util.SenderType;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Runtime representation of a single command handler method.
 *
 * <p>A command executor wraps the reflected method, its resolved argument metadata,
 * permission requirements, sender restrictions, and tab completion rules.</p>
 */
@Getter
public class CommandExecutor {

    private final String firstName;
    private final List<String> names;
    private SenderType senderType;
    private final String permission;

    private final Method method;
    private final RegisteredCommand command;

    private final Map<Integer, Argument> argumentsMap = new HashMap<>();
    private final List<Argument> arguments = new ArrayList<>();
    private String invalidUsageFormat;

    private final Map<Integer, BiFunction<CommandSender, Argument, Collection<String>>> tabCompleter = new HashMap<>();

    /**
     * Creates an executor with an explicit permission override.
     *
     * @param name the handler name
     * @param method the reflected command method
     * @param command the owning registered command
     * @param permission the permission required to use this executor
     */
    public CommandExecutor(String name, Method method, RegisteredCommand command, String permission) {
        firstName = name;
        this.names = Collections.singletonList(name);
        this.method = method;
        this.command = command;
        this.permission = permission.isEmpty() ? null : permission;

        loadArguments();
        loadInvalidFormat();
        loadTabCompleter();
    }

    /**
     * Creates an executor for a single handler name.
     *
     * @param name the handler name
     * @param method the reflected command method
     * @param command the owning registered command
     */
    public CommandExecutor(String name, Method method, RegisteredCommand command) {
        this(new String[] {name}, method, command);
    }

    /**
     * Creates an executor for one or more handler names.
     *
     * @param names the handler names and aliases
     * @param method the reflected command method
     * @param command the owning registered command
     */
    public CommandExecutor(String[] names, Method method, RegisteredCommand command) {
        firstName = names[0].toLowerCase();
        this.names = Stream.of(names).map(String::toLowerCase).collect(Collectors.toList());
        this.method = method;
        this.command = command;

        Permission permissionAnnotation = method.getAnnotation(Permission.class);
        permission = permissionAnnotation == null ? null : permissionAnnotation.value();

        loadArguments();
        loadInvalidFormat();
        loadTabCompleter();
    }

    /**
     * Executes the handler against the provided sender and raw arguments.
     *
     * @param sender the command sender
     * @param label the command label used for usage feedback
     * @param args the raw arguments to resolve
     */
    public void execute(CommandSender sender, String label, String[] args) {
        if (!senderType.equals(SenderType.ALL)) {
            if (sender instanceof ConsoleCommandSender && senderType.equals(SenderType.PLAYER)) {
                MessageProvider.getInstance().onlyPlayer(sender);
                return;
            }

            if (sender instanceof Player && senderType.equals(SenderType.CONSOLE)) {
                MessageProvider.getInstance().onlyConsole(sender);
                return;
            }
        }

        if (!checkArguments(sender, label, args)) return;

        List<String> joinArgs = new ArrayList<>(Arrays.asList(args));

        List<Object> finalArgs = new ArrayList<>();
        finalArgs.add(sender);

        for (Argument argument : arguments) {
            if (argument.isJoin()) {
                finalArgs.add(CommandUtil.join(joinArgs, " "));
            } else {
                String arg;
                try  {
                    arg = args[argument.getPosition()];
                } catch (Exception e) {
                    if (argument.isOptional()) finalArgs.add(null);
                    continue;
                }

                ArgumentResolver<?> argumentResolver = command.getCommandManager().getArgumentResolvers().get(argument.getClazz());
                if (argumentResolver == null && argument.getClazz().isEnum()) argumentResolver = command.getCommandManager().getArgumentResolvers().get(Enum.class);

                if (argumentResolver == null) {
                    if (argument.getClazz().equals(String.class)) {
                        finalArgs.add(arg);
                    } else {
                        throw new RuntimeException("No argument resolver found for class " + argument.getClazz());
                    }
                } else {
                    ArgumentResult<?> result = argumentResolver.resolve(sender, arg, argument);
                    if (result.getStatus().equals(ResultStatus.FAIL)) return;

                    finalArgs.add(result.getResult());
                }

                joinArgs.removeFirst();
            }
        }

        try {
            method.invoke(command.getCustomCommand(), finalArgs.toArray());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Validates whether the provided raw arguments match the handler signature.
     *
     * @param sender the command sender
     * @param label the command label used for usage feedback
     * @param args the raw arguments
     * @return {@code true} when the input can be executed by this handler
     */
    public boolean checkArguments(CommandSender sender, String label, String[] args) {
        if (args.length > 0 && arguments.isEmpty()) {
            MessageProvider.getInstance().invalidUsage(sender, label + invalidUsageFormat);
            return false;
        }

        if (args.length != arguments.size()) {
            Argument lastArgument = getLastArgument();
            if (args.length < arguments.size()) {
                if (args.length == arguments.size() - 1 && lastArgument.isOptional()) return true;

                MessageProvider.getInstance().invalidUsage(sender, label + invalidUsageFormat);
                return false;
            }

            if (args.length > arguments.size()) {
                if (lastArgument.isJoin()) return true;

                MessageProvider.getInstance().invalidUsage(sender, label + invalidUsageFormat);
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the last declared command argument.
     *
     * @return the last argument metadata
     */
    public Argument getLastArgument() {
        return arguments.getLast();
    }

    private void loadTabCompleter() {
        TabComplete tabComplete = method.getAnnotation(TabComplete.class);
        if (tabComplete != null) {
            int i = 0;
            for (String completer : tabComplete.value().split(" ")) {
                if (argumentsMap.get(i) == null) {
                    Argument lastArgument = getLastArgument();
                    if (lastArgument == null || !lastArgument.isJoin()) break;
                }

                if (completer.equals("*")) {
                    i++;
                    continue;
                }

                if (completer.startsWith("@")) {
                    Function<CommandSender, Collection<String>> function = command.getCommandManager().getByIdTabCompleter().get(completer);
                    if (function != null) tabCompleter.put(i, ((sender, argument) -> function.apply(sender)));
                } else {
                    List<String> result = Arrays.asList(completer.split("\\|"));
                    tabCompleter.put(i, ((sender, argument) -> result));
                }

                i++;
            }
        }

        for (Argument argument : arguments) {
            if (tabCompleter.containsKey(argument.getPosition())) continue;

            BiFunction<CommandSender, Argument, Collection<String>> function = command.getCommandManager().getByClassTabCompleter().get(argument.getClazz());
            if (function == null && argument.getClazz().isEnum()) function = command.getCommandManager().getByClassTabCompleter().get(Enum.class);

            if (function != null) tabCompleter.put(argument.getPosition(), function);
        }
    }

    private void loadInvalidFormat() {
        if (arguments.isEmpty()) {
            invalidUsageFormat = "";
            return;
        }

        StringBuilder builder = new StringBuilder();

        for (Argument argument : arguments) {
            if (argument.isOptional()) {
                builder.append(" [").append(argument.getName()).append("]");
            } else {
                builder.append(" <").append(argument.getName()).append(">");
            }
        }

        invalidUsageFormat = builder.toString();
    }

    private void loadArguments() {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (i == 0) {
                if (parameter.getType().equals(CommandSender.class)) {
                    senderType = SenderType.ALL;
                } else if (parameter.getType().equals(Player.class)) {
                    senderType = SenderType.PLAYER;
                } else if (parameter.getType().equals(ConsoleCommandSender.class)) {
                    senderType = SenderType.CONSOLE;
                } else {
                    throw new RuntimeException("Invalid sender");
                }
                continue;
            }

            Argument argument = getArgument(parameters, parameter, i);
            argumentsMap.put(argument.getPosition(), argument);
            arguments.add(argument);
        }
    }

    private Argument getArgument(Parameter[] parameters, Parameter parameter, int i) {
        Name nameAnnotation = parameter.getAnnotation(Name.class);

        String name = nameAnnotation != null ? nameAnnotation.value() : parameter.getName();
        Class<?> clazz = parameter.getType();
        int position = i - 1;
        boolean isOptional = parameter.getAnnotation(Optional.class) != null;
        boolean isJoin = parameter.getAnnotation(Join.class) != null;

        int lastArgumentPosition = parameters.length - 2;
        if (position != lastArgumentPosition && (isOptional || isJoin))
            throw new RuntimeException("@Optional and @Join annotation can only be used at the end of arguments");

        return new Argument(name, clazz, position, isOptional, isJoin);
    }

}
