package pl.wieczorekp.lifeishard.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import pl.wieczorekp.lifeishard.LifeIsHard;
import pl.wieczorekp.lifeishard.network.database.User;

import java.util.Calendar;

public class OnPlayerDeath implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        User u = LifeIsHard.getDatabaseManager().getUserBy(e.getEntity().getUniqueId());
        Player victim = e.getEntity();

        if (!u.isPresentInDb())
            LifeIsHard.getDatabaseManager().add(u);

        victim.sendMessage(
                ((String) LifeIsHard.getInst().getValue("warningMessage"))
                        .replaceAll("%HP%", String.valueOf(u.getHP() - 1))
                        .replaceAll("%MAX_HP%", String.valueOf((int) LifeIsHard.getInst().getValue("maxHP")))
        );

        int currentTime = (int) (Calendar.getInstance().getTimeInMillis() / 1000);

        if (u.getLastDeathDate() > 0 && currentTime - u.getLastDeathDate() > u.getLongestLife())
            u.setLongestLife(currentTime - u.getLastDeathDate());

        u.decreaseHP();
        u.increaseDeaths();
        u.setLastDeathDate(currentTime);

        if (victim.getKiller() != null) {
            if (Calendar.getInstance().getTime().getHours() >= LifeIsHard.getInst().getConfigInstance().getValue("hpSteal.begin") &&
                    Calendar.getInstance().getTime().getHours() < LifeIsHard.getInst().getConfigInstance().getValue("hpSteal.end")) {

                User killer = LifeIsHard.getDatabaseManager().getUserBy(victim.getKiller().getUniqueId());
                if (killer.increaseHP()) {
                    victim.getKiller().sendMessage(
                            ((String) LifeIsHard.getInst().getValue("newHP"))
                                    .replaceAll("%HP%", String.valueOf(killer.getHP()))
                                    .replaceAll("%MAX_HP%", String.valueOf((int) LifeIsHard.getInst().getValue("maxHP")))
                    );
                }
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                victim.spigot().respawn();
            }
        }.runTaskLater(LifeIsHard.getInst(), 1);
    }
}
