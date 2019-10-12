package pl.wieczorekp.lifeishard.network.database;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import pl.wieczorekp.lifeishard.LifeIsHard;

import java.util.UUID;

public class User implements Comparable<User> {
    private UUID uuid;
    // Current HP
    private int currentHP;
    // HP regenerated Naturally
    private int totalNaturalHP;
    private int lastNaturalRegenerationDate; // ToDo: nie zapisywac z ms
    // HP regenerated Artificially
    private int totalArtificialHP;
    private int lastArtificialRegenerationDate;
    // Deaths
    private int deaths;
    private int lastDeathDate;
    // 'Records'
    private int longestLife;

    private boolean presentInDb;

    public User(UUID uuid, boolean presentInDb) {
        this.uuid = uuid;
        this.currentHP = 3;
        this.totalNaturalHP = 0;
        this.lastNaturalRegenerationDate = 0;
        this.totalArtificialHP = 0;
        this.lastArtificialRegenerationDate = 0;
        this.deaths = 0;
        this.lastDeathDate = 0;
        this.longestLife = 0;
        this.presentInDb = presentInDb;
    }

    public User(UUID uuid) {
        this(uuid, false);
    }

    public int getHP() {
        return currentHP;
    }

    public void setHP(int currentHP) {
        this.currentHP = currentHP;
    }

    public void decreaseHP() {
        this.currentHP--;
    }

    public boolean increaseHP(boolean isNatural) {
        if (this.currentHP >= ((int) LifeIsHard.getInst().getValue("maxHP")))
            return false;

        this.currentHP++;
        if (isNatural) {
            this.totalNaturalHP++;
            this.setLastNaturalRegenerationDate(Utils.getCurrentTime());
        } else {
            this.totalArtificialHP++;
            this.setLastArtificialRegenerationDate(Utils.getCurrentTime());
        }
        return true;
    }

    public int getLongestLife() {
        return longestLife;
    }

    public void setLongestLife(int longestLife) {
        this.longestLife = longestLife;
    }

    public void setTotalNaturalHP(int totalNaturalHP) {
        this.totalNaturalHP = totalNaturalHP;
    }

    public void setLastNaturalRegenerationDate(int lastNaturalRegenerationDate) {
        this.lastNaturalRegenerationDate = lastNaturalRegenerationDate;
    }

    public void setTotalArtificialHP(int totalArtificialHP) {
        this.totalArtificialHP = totalArtificialHP;
    }

    public void setLastArtificialRegenerationDate(int lastArtificialRegenerationDate) {
        this.lastArtificialRegenerationDate = lastArtificialRegenerationDate;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void increaseDeaths() {
        this.deaths++;
    }

    public void setLastDeathDate(int lastDeathDate) {
        this.lastDeathDate = lastDeathDate;
    }

    public void setPresentInDb(boolean presentInDb) {
        this.presentInDb = presentInDb;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getTotalNaturalHP() {
        return totalNaturalHP;
    }

    public int getLastNaturalRegenerationDate() {
        return lastNaturalRegenerationDate;
    }

    public int getTotalArtificialHP() {
        return totalArtificialHP;
    }

    public int getLastArtificialRegenerationDate() {
        return lastArtificialRegenerationDate;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getLastDeathDate() {
        return lastDeathDate;
    }

    public boolean isPresentInDb() {
        return presentInDb;
    }


    public boolean isBanned() {
        String name = Bukkit.getOfflinePlayer(uuid).getName();
        if (name == null)
            return false;

        return Bukkit.getBanList(BanList.Type.NAME).isBanned(name);
    }

    public boolean appliesForBan() {
        return currentHP <= 0/* || ((Calendar.getInstance().getTimeInMillis() / 1000) - lastDeathDate < ((int) LifeIsHard.getInst().getConfigInstance().getValue("ban.duration")))*/;
    }

    // ToDo: ulepszyc to jakos
    @Override
    public int compareTo(User o) {
        // Less
            if (this.getDeaths() > o.getDeaths())
                return -1;

        // Equal
            if (this.getDeaths() == o.getDeaths())
                return 0;

        // Greater
            if (this.getDeaths() < o.getDeaths())
                return 1;
            return -1;
    }
}
