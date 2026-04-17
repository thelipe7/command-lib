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
import lombok.Setter;
import net.thelipe.command.util.CommandUtil;
import org.bukkit.command.CommandSender;

/**
 * Provides customizable user-facing messages emitted by the command framework.
 *
 * <p>Replace the default implementation through {@link #setInstance(MessageProvider)}
 * to customize validation and permission messages.</p>
 */
public abstract class MessageProvider {

    @Getter
    @Setter
    private static MessageProvider instance = new MessageProvider() {};

    /**
     * Called when a sender does not have permission to execute a command.
     *
     * @param sender the sender that failed the permission check
     */
    public void noPermission(CommandSender sender) {
        CommandUtil.sendMessage(sender, "<red>You do not have permission to use this command.");
    }

    /**
     * Called when a sender does not have permission to execute a subcommand.
     *
     * @param sender the sender that failed the permission check
     */
    public void noPermissionSubcommand(CommandSender sender) {
        CommandUtil.sendMessage(sender, "<red>You do not have permission to use this command.");
    }

    /**
     * Called when the sender uses an invalid command syntax.
     *
     * @param sender the sender that used the command
     * @param usage the correct usage string
     */
    public void invalidUsage(CommandSender sender, String usage) {
        CommandUtil.sendMessage(sender, "<red>Usage: {usage}"
                .replace("{usage}", "/" + usage));
    }

    /**
     * Called when a player argument cannot be resolved.
     *
     * @param sender the sender that provided the argument
     */
    public void playerNotFound(CommandSender sender) {
        CommandUtil.sendMessage(sender, "<red>Player not found.");
    }

    /**
     * Called when a player-only command is executed by a non-player sender.
     *
     * @param sender the invalid sender
     */
    public void onlyPlayer(CommandSender sender) {
        CommandUtil.sendMessage(sender, "<red>This command can only be used by players.");
    }

    /**
     * Called when a console-only command is executed by a player.
     *
     * @param sender the invalid sender
     */
    public void onlyConsole(CommandSender sender) {
        CommandUtil.sendMessage(sender, "<red>This command can only be used from the console.");
    }

    /**
     * Called when an argument value cannot be parsed.
     *
     * @param sender the sender that provided the argument
     */
    public void invalidValue(CommandSender sender) {
        CommandUtil.sendMessage(sender, "<red>Invalid value.");
    }

    /**
     * Called when a duration argument does not match the accepted format.
     *
     * @param sender the sender that provided the argument
     */
    public void invalidDuration(CommandSender sender) {
        CommandUtil.sendMessage(sender, "<red>Invalid duration. Use the format: 1d13h15m23s.");
    }

}
