/*
 * This source file is part of command-lib.
 *
 * Copyright (c) 2026 thelipe7
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for license information.
 */

package net.thelipe.command.argument;

import org.bukkit.command.CommandSender;

/**
 * Resolves raw string input into a typed command argument.
 *
 * @param <T> the resolved argument type
 */
@FunctionalInterface
public interface ArgumentResolver<T> {

    /**
     * Resolves a raw input token into a typed value.
     *
     * @param sender the command sender
     * @param input the raw input token
     * @param argument the argument metadata
     * @return the resolution result
     */
    ArgumentResult<T> resolve(CommandSender sender, String input, Argument argument);

}
