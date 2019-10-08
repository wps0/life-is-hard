package pl.wieczorekp.lifeishard.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.*;
import pl.wieczorekp.lifeishard.LifeIsHard;
import pl.wieczorekp.lifeishard.network.database.User;

public class OnPlayerJoin implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        User u = LifeIsHard.getDatabaseManager().getUserBy(e.getPlayer().getUniqueId());

        if (!u.isPresentInDb()) {
            LifeIsHard.getDatabaseManager().add(u);
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1200 * ((int) LifeIsHard.getInst().getConfigInstance().getValue("invisibilityDuration")), 1, false, false), true);
        }

    }
}
