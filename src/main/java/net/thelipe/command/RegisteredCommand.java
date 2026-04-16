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
import net.thelipe.command.annotation.Default;
import net.thelipe.command.annotation.SubCommand;
import net.thelipe.command.annotation.Unknown;
import net.thelipe.command.argument.Argument;
import net.thelipe.command.util.CommandUtil;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Bukkit command wrapper generated for a {@link CustomCommand} instance.
 *
 * <p>This type manages default, unknown, and subcommand executors and delegates
 * runtime execution to the appropriate handler method.</p>
 */
@Getter
public class RegisteredCommand extends org.bukkit.command.Command {

    private final String firstName;
    private final List<String> names = new ArrayList<>();
    private final String permission;

    private final CustomCommand customCommand;
    private final Class<?> clazz;

    private CommandExecutor defaultExecutor;
    private CommandExecutor unknownExecutor;

    private final Map<String, CommandExecutor> executorsMap = new HashMap<>();
    private final List<CommandExecutor> executors = new ArrayList<>();
    private final List<String> executorsName = new ArrayList<>();

    private final CommandManager commandManager;

    /**
     * Creates a new registered command wrapper.
     *
     * @param name the primary command name
     * @param aliases the command aliases
     * @param permission the base permission, or {@code null} for no permission check
     * @param customCommand the command implementation instance
     * @param commandManager the owning command manager
     */
    public RegisteredCommand(String name, List<String> aliases, String permission, CustomCommand customCommand, CommandManager commandManager) {
        super(name, "", "/" + name, aliases);

        firstName = name;
        names.add(name);
        names.addAll(aliases);
        this.permission = permission;

        if (this.permission != null) super.setPermission(this.permission);

        this.customCommand = customCommand;
        clazz = this.customCommand.getClass();

        this.commandManager = commandManager;

        loadExecutors();
    }

    /**
     * Dispatches command execution to the matching handler method.
     *
     * @param sender the command sender
     * @param label the label used to invoke the command
     * @param literalArgs the raw command arguments
     * @return always {@code false} so Bukkit does not emit its own usage message
     */
    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String @NotNull [] literalArgs) {
        if (permission != null && !sender.hasPermission(permission)) {
            MessageProvider.getInstance().noPermission(sender);
            return false;
        }

        String joinedLower = CommandUtil.join(literalArgs, " ").toLowerCase();
        for (String name : executorsName) {
            if (joinedLower.equals(name) || joinedLower.startsWith(name + " ")) {
                CommandExecutor executor = executorsMap.get(name);
                if (executor.getPermission() != null && !sender.hasPermission(executor.getPermission())) break;

                String[] split = CommandUtil.SPACE.split(name);
                String[] finalArgs = Arrays.copyOfRange(literalArgs, split.length, literalArgs.length);

                executor.execute(sender, label + " " + name, finalArgs);
                return false;
            }
        }

        if (defaultExecutor != null) {
            boolean execute = false;
            if (!defaultExecutor.getArguments().isEmpty()) {
                if (literalArgs.length == defaultExecutor.getArguments().size()) execute = true;

                Argument argument = defaultExecutor.getLastArgument();
                if (literalArgs.length == defaultExecutor.getArguments().size() - 1 && argument.isOptional()) execute = true;
                if (literalArgs.length > defaultExecutor.getArguments().size() && argument.isJoin()) execute = true;
            } else {
                if (literalArgs.length == 0) execute = true;
            }

            if (execute) {
                defaultExecutor.execute(sender, label, literalArgs);
                return false;
            }
        }

        if (unknownExecutor != null) {
            unknownExecutor.execute(sender, "", new String[] {});
            return false;
        }

        throw new RuntimeException("No executor was found");
    }

    private void loadExecutors() {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getAnnotation(Default.class) != null) {
                defaultExecutor = new CommandExecutor("default", method, this);
                continue;
            }

            Unknown unknown = method.getAnnotation(Unknown.class);
            if (unknown != null) {
                unknownExecutor = new CommandExecutor("unknown", method, this);
                if (unknown.value()) {
                    CommandExecutor executor = new CommandExecutor("help", method, this, unknown.helpPermission());

                    executorsMap.put("help", executor);
                    executors.add(executor);
                    executorsName.add("help");
                }
                continue;
            }

            SubCommand subCommand = method.getAnnotation(SubCommand.class);
            if (subCommand != null) {
                CommandExecutor executor = new CommandExecutor(subCommand.value(), method, this);

                executors.add(executor);
                for (String name : executor.getNames()) {
                    executorsMap.put(name, executor);
                    executorsName.add(name);
                }
            }
        }

        executorsName.sort(Comparator.comparing(name -> ((String) name).split(" ").length).reversed());
    }

}
