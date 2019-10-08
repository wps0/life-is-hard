package pl.wieczorekp.lifeishard.command.available;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.wieczorekp.lifeishard.LifeIsHard;
import pl.wieczorekp.lifeishard.command.Cmd;
import pl.wieczorekp.lifeishard.network.database.User;

import java.text.SimpleDateFormat;

public class HpCmd extends Cmd {
    @Override
    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player))
            return false;

        Player p = (Player) sender;
        User u = LifeIsHard.getDatabaseManager().getUserBy(p.getUniqueId());

        p.getPlayer().sendMessage(
                ((String) LifeIsHard.getInst().getValue("hpMessage"))
                        .replaceAll("%HP%", String.valueOf(u.getHP()))
                        .replaceAll("%MAX_HP%", String.valueOf((int) LifeIsHard.getInst().getValue("maxHP")))
        );

        return true;
    }
}
