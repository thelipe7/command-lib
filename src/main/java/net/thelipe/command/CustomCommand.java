package net.thelipe.command;

import net.thelipe.command.util.CommandUtil;
import org.bukkit.command.CommandSender;

public abstract class CustomCommand {

    protected void sendUseMessage(CommandSender sender, String usage) {
        CommandUtil.sendMessage(sender, "<red>Utilize: {usage}"
                .replace("{usage}", usage));
    }

}
