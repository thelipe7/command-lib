/*
 * This source file is part of command-lib.
 *
 * Copyright (c) 2026 thelipe7
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for license information.
 */

package net.thelipe.command.util;

/**
 * Supported sender restrictions for command handler methods.
 */
public enum SenderType {

    /**
     * The handler accepts any {@link org.bukkit.command.CommandSender}.
     */
    ALL,
    /**
     * The handler only accepts {@link org.bukkit.entity.Player} senders.
     */
    PLAYER,
    /**
     * The handler only accepts {@link org.bukkit.command.ConsoleCommandSender}.
     */
    CONSOLE

}
