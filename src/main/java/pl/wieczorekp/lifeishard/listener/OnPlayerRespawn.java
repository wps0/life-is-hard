package pl.wieczorekp.lifeishard.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import pl.wieczorekp.lifeishard.BanManager;
import pl.wieczorekp.lifeishard.LifeIsHard;
import pl.wieczorekp.lifeishard.network.database.User;

public class OnPlayerRespawn implements Listener {
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        User u = LifeIsHard.getDatabaseManager().getUserBy(e.getPlayer().getUniqueId());
        if (u.getHP() <= 0) {
            BanManager.ban(e.getPlayer());
            u.setHP(0);
            u.increaseHP(true);
        } else if (u.getHP() < ((int) LifeIsHard.getInst().getValue("maxHP"))) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1200 * ((int) LifeIsHard.getInst().getConfigInstance().getValue("invisibilityDurationAfterDeath")) - 20, 1, false, false), true);
                }
            }.runTaskLater(LifeIsHard.getInst(), 20);
        }
    }
}
