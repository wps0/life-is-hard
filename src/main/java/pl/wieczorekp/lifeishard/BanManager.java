package pl.wieczorekp.lifeishard;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.Date;

public class BanManager {

    public static void ban(Player player) {
        ban(player.getName());
        player.kickPlayer(
                ((String) LifeIsHard.getInst().getConfigInstance().getValue("ban.message"))
                        .replaceAll("%DATE%", Bukkit.getBanList(BanList.Type.NAME).getBanEntry(player.getName()).getExpiration().toString())
        );
    }

    public static void ban(String name) {
        LifeIsHard.getInst().getLogger().info("Banning player " + name + "...");
        Date banExpireDate = new Date(Calendar.getInstance().getTimeInMillis() + (((int) LifeIsHard.getInst().getConfigInstance().getValue("ban.duration")) * 3600000));
        String banMsg = ((String) LifeIsHard.getInst().getConfigInstance().getValue("ban.message"))
                .replaceAll("%DATE%", banExpireDate.toString());

        Bukkit.getBanList(BanList.Type.NAME).addBan(name, banMsg, banExpireDate, "LifeIsHard");

    }

    public static void unban(String name) {
        Bukkit.getBanList(BanList.Type.NAME).pardon(name);
    }
}
