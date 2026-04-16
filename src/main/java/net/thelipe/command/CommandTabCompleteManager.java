/*
 * This source file is part of command-lib.
 *
 * Copyright (c) 2026 thelipe7
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for license information.
 */

package net.thelipe.command;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import net.thelipe.command.argument.Argument;
import net.thelipe.command.util.CommandUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;
import java.util.function.BiFunction;

/**
 * Async tab completion listener for commands registered through {@link CommandManager}.
 */
public class CommandTabCompleteManager implements Listener {

    /**
     * Creates and registers the listener for the given manager.
     *
     * @param commandManager the owning command manager
     */
    public CommandTabCompleteManager(CommandManager commandManager) {
        Bukkit.getPluginManager().registerEvents(this, commandManager.getPlugin());
    }

    /**
     * Handles Paper async tab completion events for registered commands.
     *
     * @param event the async tab completion event
     */
    @EventHandler
    public void onAsyncTabComplete(AsyncTabCompleteEvent event) {
        CommandSender sender = event.getSender();
        String buffer = event.getBuffer();

        if (event.isCommand() || buffer.startsWith("/")) {
            if (buffer.indexOf(' ') != -1) {
                List<String> completions = getAllCompletions(sender, buffer);
                if (completions != null) {
                    event.setCompletions(completions);
                    event.setHandled(true);
                }
            }
        }
    }

    /**
     * Resolves all completions for a raw command buffer.
     *
     * @param sender the sender requesting completions
     * @param buffer the raw command buffer
     * @return the computed completions, or {@code null} when the command is not managed by this library
     */
    public List<String> getAllCompletions(CommandSender sender, String buffer) {
        String[] args = CommandUtil.SPACE.split(buffer, -1);

        String label = args[0].startsWith("/") ? args[0].substring(1) : args[0];
        args = args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[]{""};

        RegisteredCommand command = CommandManager.getInstance().getCommands().get(label);
        if (command == null) return null;

        List<String> completer = new ArrayList<>();
        if (command.getPermission() != null && !sender.hasPermission(command.getPermission())) return completer;

        completer.addAll(getExecutorCompletion(sender, command, args));
        completer.addAll(getCommandsForCompletion(sender, command, args));

        return completer;
    }

    /**
     * Resolves completions produced by argument-level tab completers.
     *
     * @param sender the sender requesting completions
     * @param command the command being completed
     * @param args the split command arguments
     * @return the matching argument completions
     */
    public Collection<String> getExecutorCompletion(CommandSender sender, RegisteredCommand command, String[] args) {
        Collection<String> completion = new ArrayList<>();

        String joinedLower = CommandUtil.join(args, " ").toLowerCase();
        for (String name : command.getExecutorsName()) {
            if (name.equals(joinedLower)) break;

            if (joinedLower.startsWith(name + " ")) {
                CommandExecutor executor = command.getExecutorsMap().get(name);
                if (executor.getPermission() != null && !sender.hasPermission(executor.getPermission())) break;

                int argPosition = (args.length - CommandUtil.SPACE.split(name).length) - 1;
                if (argPosition == -1) break;

                BiFunction<CommandSender, Argument, Collection<String>> function = executor.getTabCompleter().get(argPosition);
                if (function != null) {
                    Argument argument = executor.getArgumentsMap().get(argPosition);
                    Collection<String> applied = function.apply(sender, argument);
                    if (applied != null) completion.addAll(applied);
                }
                break;
            }
        }

        CommandExecutor defaultExecutor = command.getDefaultExecutor();
        if (defaultExecutor != null) {
            int argPosition = args.length - 1;
            if (argPosition != -1) {
                BiFunction<CommandSender, Argument, Collection<String>> function = defaultExecutor.getTabCompleter().get(argPosition);
                if (function != null) {
                    Argument argument = defaultExecutor.getArgumentsMap().get(argPosition);
                    Collection<String> applied = function.apply(sender, argument);
                    if (applied != null) completion.addAll(applied);
                }
            }
        }

        return completion;
    }

    /**
     * Resolves subcommand name completions for the current argument position.
     *
     * @param sender the sender requesting completions
     * @param command the command being completed
     * @param args the split command arguments
     * @return the matching subcommand fragments
     */
    public Collection<String> getCommandsForCompletion(CommandSender sender, RegisteredCommand command, String[] args) {
        Collection<String> commands = new ArrayList<>();

        String joinedLower = CommandUtil.join(args, " ").toLowerCase();
        int cmdIndex = Math.max(0, args.length - 1);
        for (CommandExecutor executor : command.getExecutors()) {
            String name = executor.getFirstName();
            if (name.equals(joinedLower)) break;

            if (name.startsWith(joinedLower)) {
                if (executor.getPermission() != null && !sender.hasPermission(executor.getPermission())) continue;

                String[] split = CommandUtil.SPACE.split(name);
                if (split[0].equals(joinedLower)) continue;

                String index = split[cmdIndex];
                if (index.equals(joinedLower)) index = split[cmdIndex + 1];

                if (!commands.contains(index)) commands.add(index);
            }
        }

        return commands;
    }

}
