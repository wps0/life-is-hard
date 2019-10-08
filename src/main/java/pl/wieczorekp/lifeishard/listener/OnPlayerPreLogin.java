package pl.wieczorekp.lifeishard.listener;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import pl.wieczorekp.lifeishard.BanManager;
import pl.wieczorekp.lifeishard.LifeIsHard;
import pl.wieczorekp.lifeishard.network.database.User;

public class OnPlayerPreLogin implements Listener {

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
        User u = LifeIsHard.getDatabaseManager().getUserBy(e.getUniqueId());

        if (u.appliesForBan()) {
            if (!u.isBanned())
                BanManager.ban(e.getName());
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, ((String) LifeIsHard.getInst().getConfigInstance().getValue("ban.message"))
                    .replaceAll("%DATE%", Bukkit.getBanList(BanList.Type.NAME).getBanEntry(e.getName()).getExpiration().toString()));
        } else if (u.getHP() <= 0) {
            BanManager.unban(e.getName());
            u.setHP(1);
        }
    }
}
