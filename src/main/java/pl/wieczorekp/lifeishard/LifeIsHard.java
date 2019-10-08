package pl.wieczorekp.lifeishard;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pl.wieczorekp.configmodule.IConfigurableJavaPlugin;
import pl.wieczorekp.configmodule.config.*;
import pl.wieczorekp.configmodule.factories.HashMapFactory;
import pl.wieczorekp.lifeishard.command.CmdHandler;
import pl.wieczorekp.lifeishard.listener.*;
import pl.wieczorekp.lifeishard.network.database.DatabaseManager;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.logging.Level;

public class LifeIsHard extends JavaPlugin implements IConfigurableJavaPlugin {
    private static IConfigurableJavaPlugin _instance;
    private static DatabaseManager _dbm;
    private Config _config;
    private CmdHandler _cmd;
    private boolean configLoaded;

    public LifeIsHard() {
        _instance = this;
        this.configLoaded = false;
    }

    @Override
    public void onEnable() {
        getLogger().setLevel(Level.FINE);
        try {
            _config = new Config(true, this, new ConfigFile(getDataFolder(), "config.yml", new ConfigEntryHashMap(
                    HashMapFactory.makeHashMap(
                            new ConfigEntry<>("maxHP", () -> 1),
                            new ConfigEntry<>("invisibilityDuration", () -> 1),
                            new ConfigEntry<>("invisibilityDurationAfterDeath", () -> 1),
                            new ConfigEntry<>("saveDelay", () -> 1),
                            new ConfigEntry<>("hpSteal.begin", () -> 1),
                            new ConfigEntry<>("hpSteal.end", () -> 1),
                            new ConfigEntry<>("autoRegen.perHP", () -> 1),
                            new ConfigEntry<>("autoRegen.threshold", () -> 1),
                            new ConfigEntry<>("ban.duration", () -> 1),
                            new ConfigEntry<>("database.type", () -> 1),
                            new ConfigEntry<>("database.port", () -> 1)
                    ),
                    HashMapFactory.makeHashMap(),
                    HashMapFactory.makeHashMap(
                            new ConfigEntry<>("ban.message", String::new),
                            new ConfigEntry<>("warningMessage", String::new),
                            new ConfigEntry<>("hpMessage", String::new),
                            new ConfigEntry<>("maxHPMessage", String::new),
                            new ConfigEntry<>("newHP", String::new),
                            new ConfigEntry<>("healingItem.name", String::new),
                            new ConfigEntry<>("database.address", String::new),
                            new ConfigEntry<>("database.login", String::new),
                            new ConfigEntry<>("database.password", String::new),
                            new ConfigEntry<>("database.name", String::new)
                    ),
                    HashMapFactory.makeHashMap(
            ))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!_config.load()) {
            getLogger().severe("Config file could not be loaded!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        _dbm = new DatabaseManager(HashMapFactory.getHashMap(
                new SimpleEntry<>("type", _config.getValue("database.type")),
                new SimpleEntry<>("address", _config.getValue("database.address")),
                new SimpleEntry<>("port", _config.getValue("database.port")),
                new SimpleEntry<>("login", _config.getValue("database.login")),
                new SimpleEntry<>("password", _config.getValue("database.password")),
                new SimpleEntry<>("name", _config.getValue("database.name"))
        ));
        _dbm.startup();
        configLoaded = true;

        // DB autosave
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Bukkit.getOnlinePlayers().size() > 0)
                    getDatabaseManager().save();
            }
        }.runTaskTimer(this, 600, 20 * (int) LifeIsHard.getInst().getConfigInstance().getValue("saveDelay"));

        // Golden apples

        ItemStack healingGApple = new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1);
        ItemMeta hGAppleMeta = healingGApple.getItemMeta();
        hGAppleMeta.addEnchant(Enchantment.THORNS, 1, true);
        hGAppleMeta.setDisplayName(_config.getValue("healingItem.name"));
        hGAppleMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        healingGApple.setItemMeta(hGAppleMeta);

        ShapedRecipe healingGoldenApple = new ShapedRecipe(new NamespacedKey(this, "healing_gapple"), healingGApple);
        healingGoldenApple.shape("dgd", "gag", "dgd");
        healingGoldenApple.setIngredient('d', Material.DIAMOND_BLOCK);
        healingGoldenApple.setIngredient('g', Material.GOLD_BLOCK);
        healingGoldenApple.setIngredient('a', Material.APPLE);

        getServer().addRecipe(healingGoldenApple);

        getServer().getPluginManager().registerEvents(new OnPlayerConsumeItem(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerRespawn(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerDeath(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerJoin(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerPreLogin(), this);

        _cmd = new CmdHandler();

        getLogger().info("Successfully enabled!");
    }

    @Override
    public void onDisable() {
        if (configLoaded)
            _dbm.save();
    }

    public static LifeIsHard getInst() {
        return (LifeIsHard) _instance;
    }

    @Override
    public IConfigurableJavaPlugin getInstance() {
        return _instance;
    }

    @Override
    public Config getConfigInstance() {
        return _config;
    }

    @Nullable
    public <T> T getValue(@NotNull String key) {
        return _config.getValue(key);
    }

    public static DatabaseManager getDatabaseManager() {
        return _dbm;
    }
}
