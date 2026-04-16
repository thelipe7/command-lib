package net.thelipe.command;

import lombok.Getter;
import lombok.Setter;
import net.thelipe.command.util.CommandUtil;
import org.bukkit.command.CommandSender;

public abstract class MessageProvider {
    
    @Getter
    @Setter
    private static MessageProvider instance = new MessageProvider() {};

    public void noPermission(CommandSender sender) {
        CommandUtil.sendMessage(sender, "<red>Comando não encontrado.");
    }

    public void invalidUsage(CommandSender sender, String usage) {
        CommandUtil.sendMessage(sender, "<red>Utilize: {usage}"
                .replace("{usage}", "/" + usage));
    }

    public void playerNotFound(CommandSender sender) {
        CommandUtil.sendMessage(sender, "<red>Jogador não encontrado.");
    }

    public void onlyPlayer(CommandSender sender) {
        CommandUtil.sendMessage(sender, "<red>Este comando só pode ser executado por jogadores.");
    }

    public void onlyConsole(CommandSender sender) {
        CommandUtil.sendMessage(sender, "<red>Este comando só pode ser executado pelo console.");
    }

    public void invalidValue(CommandSender sender) {
        CommandUtil.sendMessage(sender, "<red>O valor inserido é inválido.");
    }

    public void invalidDuration(CommandSender sender) {
        CommandUtil.sendMessage(sender, "<red>A duração inserida é invalida, utilize o formato: 1d13h15m23s.");
    }
    
}
