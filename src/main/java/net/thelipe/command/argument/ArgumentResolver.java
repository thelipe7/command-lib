package net.thelipe.command.argument;

import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface ArgumentResolver<T> {

    ArgumentResult<T> resolve(CommandSender sender, String input, Argument argument);

}
