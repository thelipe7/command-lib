/*
 * This source file is part of command-lib.
 *
 * Copyright (c) 2026 thelipe7
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for license information.
 */

package net.thelipe.command;

import net.thelipe.command.util.CommandUtil;
import org.bukkit.command.CommandSender;

/**
 * Base type for annotation-driven command classes.
 *
 * <p>Extend this class and annotate methods with command annotations such as
 * {@code @Default}, {@code @SubCommand}, and {@code @Unknown}.</p>
 */
public abstract class CustomCommand {

    /**
     * Sends a default usage message to the command sender.
     *
     * @param sender the sender receiving the message
     * @param usage the usage string to display
     */
    protected void sendUseMessage(CommandSender sender, String usage) {
        CommandUtil.sendMessage(sender, "<red>Utilize: {usage}"
                .replace("{usage}", usage));
    }

}
