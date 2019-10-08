package pl.wieczorekp.lifeishard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

import java.util.Arrays;

public class ExceptionManager {

    public static void handle(Exception e) {
        ConsoleCommandSender ccs = Bukkit.getConsoleSender();
        ccs.sendMessage(ChatColor.YELLOW + "---" + LifeIsHard.getInst().getName() + " ERROR ---");
        ccs.sendMessage(ChatColor.YELLOW + "Message: " + ChatColor.GRAY + e.getMessage());
        ccs.sendMessage(ChatColor.YELLOW + "Stack trace: " + ChatColor.GRAY + Arrays.toString(e.getStackTrace()));
        e.printStackTrace();
        ccs.sendMessage(ChatColor.YELLOW + "---------");
    }
}
