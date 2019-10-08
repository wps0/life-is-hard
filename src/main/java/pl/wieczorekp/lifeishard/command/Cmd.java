package pl.wieczorekp.lifeishard.command;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import pl.wieczorekp.lifeishard.LifeIsHard;

public abstract class Cmd {
    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    protected boolean canExecute(CommandSender sender) {
        return canExecute(sender, Sender.PLAYER);
    }

    protected boolean canExecute(CommandSender sender, Sender senderType) {
//        boolean status;
//        switch (senderType) {
//            case PLAYER:   if (status = !(sender instanceof Player)) LifeIsHard.getInst().getConfigInstance().sendMessage(sender, "onlyConsoleCommand"); break;
//            case BLOCK:    if (status = !(sender instanceof BlockCommandSender)) LifeIsHard.getInst().getConfigInstance().sendMessage(sender, "commandNotAvailableForConsole"); break;
//            case CONSOLE:  if (status = !(sender instanceof ConsoleCommandSender)) LifeIsHard.getInst().getConfigInstance().sendMessage(sender, "commandNotAvailableForConsole"); break;
//            case PROXIED:  if (status = !(sender instanceof ProxiedCommandSender)) LifeIsHard.getInst().getConfigInstance().sendMessage(sender, "commandNotAvailableForConsole"); break;
//            default:       status = false;
//        }

//        return !status;
        return true;
    }

    public enum Sender {
        CONSOLE,
        BLOCK,
        PLAYER,
        PROXIED
    }
}
