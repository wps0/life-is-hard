package pl.wieczorekp.lifeishard.listener;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import pl.wieczorekp.lifeishard.LifeIsHard;
import pl.wieczorekp.lifeishard.network.database.User;

public class OnPlayerConsumeItem implements Listener {

    @EventHandler
    public void onPlayerConsumeItem(PlayerItemConsumeEvent e) {

        if (e.getItem().getItemMeta() != null
                && !e.getItem().getItemMeta().getEnchants().isEmpty()
                && e.getItem().getType() == Material.ENCHANTED_GOLDEN_APPLE
                && e.getItem().getEnchantments().get(Enchantment.THORNS) != null) {

            User u = LifeIsHard.getDatabaseManager().getUserBy(e.getPlayer().getUniqueId());

            if (!u.increaseHP(false)) {
                e.setCancelled(true);
                e.getPlayer().sendMessage((String) LifeIsHard.getInst().getValue("maxHPMessage"));
            } else {
                e.getPlayer().sendMessage(
                        ((String) LifeIsHard.getInst().getValue("newHP"))
                        .replaceAll("%HP%", String.valueOf(u.getHP()))
                        .replaceAll("%MAX_HP%", String.valueOf((int) LifeIsHard.getInst().getValue("maxHP")))
                );
            }
        }
    }
}
