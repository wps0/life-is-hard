package pl.wieczorekp.lifeishard.network.database;

import org.bukkit.Bukkit;
import pl.wieczorekp.configmodule.IConfigurableJavaPlugin;
import pl.wieczorekp.lifeishard.ExceptionManager;
import pl.wieczorekp.lifeishard.LifeIsHard;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DatabaseManager extends DatabaseConnection {
    private HashSet<User> users;
    private List<String> updateQueries;

    public DatabaseManager(HashMap<String, Object> databaseCredentials) {
        super(databaseCredentials, (int) databaseCredentials.get("type") != 1);

        users = new HashSet<>(4);
        updateQueries = new ArrayList<>(4);
    }

    public synchronized void startup() {
        connect();

        StringBuilder table_players = new StringBuilder();
        table_players.append("CREATE TABLE IF NOT EXISTS `LifeIsHard_players` (");
        table_players.append("uuid VARCHAR(100) NOT NULL ").append(isSQLite ? "PRIMARY KEY," : ",");
        table_players.append("current_hp INT NOT NULL DEFAULT ").append((int) LifeIsHard.getInst().getConfigInstance().getValue("maxHP")).append(",");
        table_players.append("total_natural_hp INT NOT NULL DEFAULT 0,");
        table_players.append("last_natural_hp_date INT NOT NULL DEFAULT 0,");
        table_players.append("total_artificial_hp INT NOT NULL DEFAULT 0,");
        table_players.append("last_artificial_hp_date INT NOT NULL DEFAULT 0,");
        table_players.append("deaths INT NOT NULL DEFAULT 0,");
        table_players.append("last_death INT NOT NULL DEFAULT 0,");
        table_players.append("longest_life INT NOT NULL DEFAULT 0");
        if (!isSQLite)
            table_players.append(",PRIMARY KEY (uuid)");
        table_players.append(");");

        try {
            getConnection().createStatement().executeUpdate(table_players.toString());
        } catch (SQLException e) {
            ExceptionManager.handle(e);
        }

        load();
    }

    private synchronized void load() {
        connect();

        try {
            ResultSet rs = getConnection().createStatement().executeQuery("SELECT * FROM `LifeIsHard_players`");

            while (rs.next()) {
                User u = new User(UUID.fromString(rs.getString("uuid")));
                u.setHP(rs.getInt("current_hp"));
                u.setLongestLife(rs.getInt("longest_life"));

                u.setTotalNaturalHP(rs.getInt("total_natural_hp"));
                u.setLastNaturalRegenerationDate(rs.getInt("last_natural_hp_date"));

                u.setTotalArtificialHP(rs.getInt("total_artificial_hp"));
                u.setLastArtificialRegenerationDate(rs.getInt("last_artificial_hp_date"));

                u.setDeaths(rs.getInt("deaths"));
                u.setLastDeathDate(rs.getInt("last_death"));

                u.setPresentInDb(true);

                users.add(u);
            }
        } catch (SQLException e) {
            ExceptionManager.handle(e);
        }

        disconnect();
    }

    public synchronized void save() {
        IConfigurableJavaPlugin plugin = LifeIsHard.getInst();
        connect();

        plugin.getLogger().info("Saving player data to database...");

        for (User u : users) {
            StringBuilder insertQuery = new StringBuilder();
            StringBuilder updateQ = new StringBuilder();
            if (!u.isPresentInDb()) {
                insertQuery.append("INSERT OR IGNORE INTO `LifeIsHard_players` (uuid, current_hp, total_natural_hp, last_natural_hp_date, total_artificial_hp, last_artificial_hp_date, deaths, last_death, longest_life) VALUES (");
                insertQuery.append("'").append(u.getUuid().toString()).append("',");
                insertQuery.append(u.getHP()).append(",");

                insertQuery.append(u.getTotalNaturalHP()).append(",");
                insertQuery.append(u.getLastNaturalRegenerationDate()).append(",");

                insertQuery.append(u.getTotalArtificialHP()).append(",");
                insertQuery.append(u.getLastArtificialRegenerationDate()).append(",");

                insertQuery.append(u.getDeaths()).append(",");
                insertQuery.append(u.getLastDeathDate()).append(",");

                insertQuery.append(u.getLongestLife()).append(");");
            } else {
                updateQ.append("UPDATE LifeIsHard_players SET ");
                updateQ.append("current_hp=").append(u.getHP()).append(",");
                updateQ.append("longest_life=").append(u.getLongestLife()).append(",");

                updateQ.append("total_natural_hp=").append(u.getTotalNaturalHP()).append(",");
                updateQ.append("last_natural_hp_date=").append(u.getLastNaturalRegenerationDate()).append(",");

                updateQ.append("total_artificial_hp=").append(u.getLastNaturalRegenerationDate()).append(",");
                updateQ.append("last_artificial_hp_date=").append(u.getLastNaturalRegenerationDate()).append(",");

                updateQ.append("deaths=").append(u.getDeaths()).append(",");
                updateQ.append("last_death=").append(u.getLastDeathDate());
                updateQ.append(" WHERE uuid LIKE '").append(u.getUuid().toString()).append("';");
            }

            try {
                if (!u.isPresentInDb()) {
                    getConnection().createStatement().executeUpdate(insertQuery.toString());
                    u.setPresentInDb(true);
                } else
                    getConnection().createStatement().executeUpdate(updateQ.toString());
            } catch (SQLException e) {
                ExceptionManager.handle(e);
                LifeIsHard.getInst().getLogger().severe("Database save error!");
            }
        }

        Iterator<String> iterator = updateQueries.iterator();
        while (iterator.hasNext()) {
            String s = iterator.next();
            try {
                getConnection().createStatement().executeUpdate(s);
                iterator.remove();
            } catch (SQLException e) {
                ExceptionManager.handle(e);
                LifeIsHard.getInst().getLogger().severe("Database save error!");
            }
        }

        LifeIsHard.getInst().getLogger().info("Database save completed.");
        disconnect();
    }

    public synchronized void add(UUID uuid) {
        users.add(getUserBy(uuid));
    }

    public synchronized void add(User u) {
        users.add(u);
    }

    public User getUserBy(UUID uuid) {
        if (uuid == null)
            return null;

        for (User u : users) // ToDo: zopymalizowac!!!
            if (u.getUuid().equals(uuid))
                return u;

        return new User(uuid);
    }

    public synchronized ResultSet executeQueryImmediately(String query) throws SQLException {
        connect();
        return getConnection().createStatement().executeQuery(query);
    }

    public Set<User> getUsers() {
        return users;
    } // ToDo: zwracac kopie
}
